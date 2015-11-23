package net.redborder.decompress.implementations;

import eu.medsea.mimeutil.MimeType;
import net.redborder.decompress.AbstractDecompressor;
import net.redborder.decompress.Decompressor;
import net.redborder.decompress.constants.Extensions;
import net.redborder.decompress.constants.General;
import net.redborder.decompress.helpers.FileHelper;
import net.redborder.decompress.models.ArchiveFile;
import net.redborder.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fernando Dominguez on 09/11/15.
 */
public class GzDecompressor extends AbstractDecompressor implements Decompressor {

    /* Constructors */

    public GzDecompressor(File archive) {
        super(archive);
    }

    public GzDecompressor(File archive, File outputDir) {
        super(archive, outputDir);
    }

    /* Public methods */

    public List<ArchiveFile> decompress() {
        List<ArchiveFile> files = null;
        try{
            FileInputStream fileInputStream = new FileInputStream(archive);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            if (outputDir != null) files = decompressGzip(new GzipCompressorInputStream(bufferedInputStream), outputDir.getAbsolutePath());
            else files = decompressGzip( FileHelper.nameFromFile(archive.getName()), new GzipCompressorInputStream(bufferedInputStream));
        }catch (IOException e){
            e.printStackTrace();
        }
        return files;
    }

    /* Private methods */

    private List<ArchiveFile> decompressGzip(GzipCompressorInputStream inputStream, String output) throws IOException {
        List<ArchiveFile> files = new ArrayList<ArchiveFile>();
        FileOutputStream out = new FileOutputStream(output);
        ByteArrayOutputStream content = new ByteArrayOutputStream();
        final byte[] buffer = new byte[General.BUFFER_SIZE];
        int n = 0;

        while (-1 != (n = inputStream.read(buffer))) {
            out.write(buffer, 0, n);
            // FIXME: 12/11/15 Do not write twice
            content.write(buffer, 0, n);
        }
        inputStream.close();

        // Gzips are usually chained, so result may be a .tar
        // if that's the case decompress it
        File decompressed = new File(output);
        if ( FileHelper.checkMime(decompressed, new MimeType(Extensions.TAR_MIMETYPE)) ){

            Decompressor tarDecompressor = new TarDecompressor(decompressed);
            files = tarDecompressor.decompress();

            // delete the .tar file
            File tar = new File(output);
            tar.delete();
        } else{
            files.add( new ArchiveFile(decompressed.getName(), content.toByteArray()) );
        }
        out.close();
        return files;
    }

    private List<ArchiveFile> decompressGzip(String filename, GzipCompressorInputStream inputStream) throws IOException {
        List<ArchiveFile> files = new ArrayList<ArchiveFile>();
        ByteArrayOutputStream content = new ByteArrayOutputStream();
        final byte[] buffer = new byte[General.BUFFER_SIZE];
        int n = 0;

        while (-1 != (n = inputStream.read(buffer))) {
            content.write(buffer, 0, n);
        }

        // FIXME: 17/11/15 It would be ideal to use stream metadata
        // but it is not available until commons-compress 1.8
        // so a dirty workaround is needed
        files.add( new ArchiveFile(filename, content.toByteArray()) );
        inputStream.close();

        return files;
    }
}
