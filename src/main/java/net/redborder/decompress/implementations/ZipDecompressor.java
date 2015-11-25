package net.redborder.decompress.implementations;

import net.redborder.apache.commons.compress.PasswordRequiredException;
import net.redborder.apache.commons.compress.archivers.zip.UnsupportedZipFeatureException;
import net.redborder.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import net.redborder.apache.commons.compress.archivers.zip.ZipFile;
import net.redborder.apache.commons.compress.utils.IOUtils;
import net.redborder.decompress.AbstractDecompressor;
import net.redborder.decompress.Decompressor;
import net.redborder.decompress.helpers.StreamHelper;
import net.redborder.decompress.models.ArchiveFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by Fernando Dominguez on 06/11/15.
 */
public class ZipDecompressor extends AbstractDecompressor implements Decompressor {

    /* Constructors */

    public ZipDecompressor(File archive) {
        super(archive);
    }

    public ZipDecompressor(File archive, File outputDir) {
        super(archive, outputDir);
    }

    /* Public methods */

    public List<ArchiveFile> decompress() throws PasswordRequiredException{

        List<ArchiveFile> files = null;
        try{
            ZipFile zipFile = new ZipFile(archive);
            if (outputDir != null) files = decompressZip(zipFile, outputDir.getAbsolutePath());
            else files = decompressZip(zipFile);
        } catch (UnsupportedZipFeatureException e) {
            // Zip encryption is not supported by Apache Commons Compress
            // This is the best way of guessing if the zip is encrypted so far
            throw new PasswordRequiredException(archive.getName());
        } catch (IOException e){
            logger.error("IOException while trying to decompress " + archive.getName(), e);
        }
        return files;
    }

    /* Private methods */

    private List<ArchiveFile> decompressZip(ZipFile zipFile, String outputDir) throws IOException {

        Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
        List<ArchiveFile> files = new ArrayList<ArchiveFile>();

        try {
            while (entries.hasMoreElements()) {

                ZipArchiveEntry entry = entries.nextElement();
                logger.info(this.getClass().toString() + ": Writing '" + entry.getName() + "' to " + outputDir);
                File targetFile = new File(outputDir, entry.getName());
                if (entry.isDirectory()) {
                    targetFile.mkdirs();
                } else {
                    targetFile.getParentFile().mkdirs();
                    InputStream in = zipFile.getInputStream(entry);
                    OutputStream out = new FileOutputStream(targetFile);
                    IOUtils.copy(in, out);
                    // Return the extracted file
                    files.add( new ArchiveFile(entry.getName(), StreamHelper.toByteArray(in)) );

                    in.close();
                    out.close();
                }
            }
        } finally {
            zipFile.close();
        }

        return files;
    }

    private List<ArchiveFile> decompressZip(ZipFile zipFile) throws IOException {

        Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
        List<ArchiveFile> files = new ArrayList<ArchiveFile>();

        try {
            while (entries.hasMoreElements()) {

                ZipArchiveEntry entry = entries.nextElement();
                if (!entry.isDirectory()) {
                    InputStream in = zipFile.getInputStream(entry);
                    // Return the extracted file
                    files.add( new ArchiveFile(entry.getName(), StreamHelper.toByteArray(in)) );
                    in.close();
                }
            }
        } finally {
            zipFile.close();
        }

        return files;
    }
}
