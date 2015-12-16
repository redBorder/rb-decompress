package net.redborder.decompress.implementations;

import net.redborder.apache.commons.compress.archivers.tar.TarArchiveEntry;
import net.redborder.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import net.redborder.decompress.AbstractDecompressor;
import net.redborder.decompress.Decompressor;
import net.redborder.decompress.constants.General;
import net.redborder.decompress.helpers.FileHelper;
import net.redborder.decompress.models.Archive;
import net.redborder.decompress.models.ArchiveFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fernando Domínguez on 09/11/15.
 */
public class TarDecompressor extends AbstractDecompressor implements Decompressor {

    /* Constructors */

    public TarDecompressor(File archive) {
        super(archive);
    }

    public TarDecompressor(File archive, File outputDir) {
        super(archive, outputDir);
    }

    /* Public methods */

    public Archive decompress() {
        List<ArchiveFile> files = null;
        try{
            TarArchiveInputStream tarInput = new TarArchiveInputStream(new FileInputStream(archive));
            if (outputDir != null) files = decompressTar(tarInput, outputDir);
            else files = decompressTar(tarInput);
        }catch (IOException e){
            e.printStackTrace();
        }
        return new Archive(FileHelper.fileToCompressionType(archive), files,
                            FileHelper.getMimeTypes(archive));
    }

    /* Private methods */

    private List<ArchiveFile> decompressTar(TarArchiveInputStream tarInput, File targetDir) throws IOException {
        targetDir.mkdirs();

        List<ArchiveFile> files = new ArrayList<ArchiveFile>();

        TarArchiveEntry entry;
        while ((entry = tarInput.getNextTarEntry()) != null){
            if (!entry.isDirectory()){
                // OS X stores additional info on tars.
                // This info is only relevant to OS X itself so let's ignore it.
                // Said info is stored in files named "./._<filename>"
                if (!entry.getName().startsWith("./._")) {
                    byte[] buffer = new byte[General.BUFFER_SIZE];
                    String outputFilePath = targetDir.getAbsolutePath() + "/" + entry.getName();
                    BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFilePath), General.BUFFER_SIZE);
                    ByteArrayOutputStream content = new ByteArrayOutputStream();
                    int count = 0;
                    while ((count = tarInput.read(buffer, 0, General.BUFFER_SIZE)) != -1) {
                        outputStream.write(buffer, 0, count);
                        // FIXME: 12/11/15 Do not write twice
                        content.write(buffer, 0, count);
                    }
                    files.add( new ArchiveFile(entry.getName(), content.toByteArray()) );
                }
            }else{      // if it's a folder we can just create it
                String path = targetDir.getAbsolutePath() + "/" + entry.getName();
                File dir = new File(path);
                dir.mkdirs();
            }
        }

        return files;
    }

    private List<ArchiveFile> decompressTar(TarArchiveInputStream tarInput) throws IOException {

        List<ArchiveFile> files = new ArrayList<ArchiveFile>();

        TarArchiveEntry entry;
        while ((entry = tarInput.getNextTarEntry()) != null){
            // OS X stores additional info on tars.
            // This info is only relevant to OS X itself so let's ignore it.
            // Said info is stored in files named "./._<filename>"
            if (!entry.isDirectory() && !entry.getName().startsWith("./._")) {
                byte[] buffer = new byte[General.BUFFER_SIZE];
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                int count = 0;
                while ((count = tarInput.read(buffer, 0, General.BUFFER_SIZE)) != -1) {
                    outputStream.write(buffer, 0, count);
                }
                files.add( new ArchiveFile(entry.getName(), outputStream.toByteArray()) );
            }
        }

        return files;
    }
}
