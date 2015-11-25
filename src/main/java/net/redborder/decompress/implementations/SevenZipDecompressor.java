package net.redborder.decompress.implementations;

import net.redborder.apache.commons.compress.PasswordRequiredException;
import net.redborder.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import net.redborder.apache.commons.compress.archivers.sevenz.SevenZFile;
import net.redborder.decompress.AbstractDecompressor;
import net.redborder.decompress.Decompressor;
import net.redborder.decompress.models.ArchiveFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fernando Domínguez on 09/11/15.
 */
public class SevenZipDecompressor extends AbstractDecompressor implements Decompressor {

    /* Constructors */

    public SevenZipDecompressor(File archive) {
        super(archive);
    }

    public SevenZipDecompressor(File archive, File outputDir) {
        super(archive, outputDir);
    }

    /* Public methods */

    public List<ArchiveFile> decompress() throws PasswordRequiredException{
        List<ArchiveFile> files = null;
        try{
            SevenZFile sevenZFile = new SevenZFile(archive);
            if (outputDir != null) files = decompress7Zip(sevenZFile, outputDir);
            else files = decompress7Zip(sevenZFile);
        } catch (PasswordRequiredException e) {
            throw new PasswordRequiredException(archive.getName());
        } catch (IOException e){
            e.printStackTrace();
        }
        return files;
    }

    /* Private methods */

    private List<ArchiveFile> decompress7Zip(SevenZFile archive, File outputDir) throws IOException{

        if (outputDir != null) outputDir.mkdirs();

        List<ArchiveFile> files = new ArrayList<ArchiveFile>();
        SevenZArchiveEntry entry;
        while( (entry = archive.getNextEntry()) != null ){

            new File(entry.getName()).mkdirs();
            if (!entry.isDirectory()) {
                byte[] content = new byte[(int) entry.getSize()];
                FileOutputStream out = new FileOutputStream(entry.getName());

                archive.read(content, 0, content.length);
                out.write(content);
                out.close();
                files.add(new ArchiveFile(entry.getName(), content));
            }
        }

        return files;
    }

    private List<ArchiveFile> decompress7Zip(SevenZFile archive) throws IOException{

        List<ArchiveFile> files = new ArrayList<ArchiveFile>();
        SevenZArchiveEntry entry;
        while( (entry = archive.getNextEntry()) != null ){
            byte[] content = new byte[(int) entry.getSize()];
            archive.read(content, 0, content.length);
            files.add(new ArchiveFile( entry.getName(), content));
        }

        return files;
    }

}
