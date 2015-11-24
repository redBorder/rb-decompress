package net.redborder.decompress.implementations;

import com.github.junrar.Archive;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.FileHeader;
import net.redborder.apache.commons.compress.PasswordRequiredException;
import net.redborder.decompress.AbstractDecompressor;
import net.redborder.decompress.Decompressor;
import net.redborder.decompress.models.ArchiveFile;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fernando Domínguez on 05/11/15.
 */
public class RarDecompressor extends AbstractDecompressor implements Decompressor {

    /* Constructors */

    public RarDecompressor(File archive) {
        super(archive);
    }

    public RarDecompressor(File archive, File outputDir) {
        super(archive, outputDir);
    }

    /* Public methods */

    public List<ArchiveFile> decompress() throws PasswordRequiredException{
        List<ArchiveFile> files = null;
        if (outputDir != null) files = extractArchive(archive, outputDir);
        else files = extractArchive(archive);
        return files;
    }

    /* Private methods */

    private List<ArchiveFile> extractArchive(File archive, File destination) throws PasswordRequiredException{
        outputDir.mkdirs();
        List<ArchiveFile> files = new ArrayList<ArchiveFile>();
        Archive arch = null;
        try {
            arch = new Archive(archive);
        } catch (RarException e) {
            logger.error(e);
        } catch (IOException e1) {
            logger.error(e1);
        }
        if (arch != null) {
            if (arch.isEncrypted()) {
                logger.warn("Archive is encrypted can not be extracted");
                return null;
            }
            FileHeader fileHeader = null;
            while (true) {
                fileHeader = arch.nextFileHeader();
                if (fileHeader == null) {
                    break;
                }
                if (fileHeader.isEncrypted()) {
                    // Next files could not be encrypted
                    // but that's the best way I can think of
                    throw new PasswordRequiredException(archive.getName());
                }
                logger.info("Extracting: " + fileHeader.getFileNameString());
                try {
                    if (fileHeader.isDirectory()) {
                        createDirectory(fileHeader, destination);
                    } else {
                        File f = createFile(fileHeader, destination);
                        FileOutputStream outStream = new FileOutputStream(f);
                        arch.extractFile(fileHeader, outStream);
                        // Return the extracted file
                        files.add( new ArchiveFile(fileHeader.getFileNameString(), Files.readAllBytes(f.toPath())) );
                        outStream.close();
                    }
                } catch (IOException e) {
                    logger.error("Error extracting the file", e);
                } catch (RarException e) {
                    logger.error("Error extraction the file", e);
                }
            }
        }
        return files;
    }

    private List<ArchiveFile> extractArchive(File archive) throws PasswordRequiredException{
        List<ArchiveFile> files = new ArrayList<ArchiveFile>();
        Archive arch = null;
        try {
            arch = new Archive(archive);
        } catch (RarException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        }
        if (arch != null) {
            if (arch.isEncrypted()) {
                logger.warn("Archive is encrypted can not be extracted");
                return null;
            }
            FileHeader fileHeader = null;
            while (true) {
                fileHeader = arch.nextFileHeader();
                if (fileHeader == null) {
                    break;
                }
                if (fileHeader.isEncrypted()) {
                    throw new PasswordRequiredException(archive.getName());
                }
                logger.info("Extracting: " + fileHeader.getFileNameString());
                try {
                    if (!fileHeader.isDirectory()) {
                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                        arch.extractFile(fileHeader, outStream);
                        // Return the extracted file
                        files.add( new ArchiveFile(fileHeader.getFileNameString(), outStream.toByteArray()) );
                        outStream.close();
                    }
                } catch (IOException e) {
                    logger.error("Error extracting the file", e);
                } catch (RarException e) {
                    logger.error("Error extraction the file", e);
                }
            }
        }
        return files;
    }

    private File createFile(FileHeader fh, File destination) {
        File f = null;
        String name = null;
        if (fh.isFileHeader() && fh.isUnicode()) {
            name = fh.getFileNameW();
        } else {
            name = fh.getFileNameString();
        }
        f = new File(destination, name);
        if (!f.exists()) {
            try {
                f = makeFile(destination, name);
            } catch (IOException e) {
                logger.error("Error creating the new file: " + f.getName(), e);
            }
        }
        return f;
    }

    private File makeFile(File destination, String name) throws IOException {
        String[] dirs = name.split("\\\\");
        if (dirs == null) {
            return null;
        }
        String path = "";
        int size = dirs.length;
        if (size == 1) {
            return new File(destination, name);
        } else if (size > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                path = path + File.separator + dirs[i];
                new File(destination, path).mkdir();
            }
            path = path + File.separator + dirs[dirs.length - 1];
            File f = new File(destination, path);
            f.createNewFile();
            return f;
        } else {
            return null;
        }
    }

    private void createDirectory(FileHeader fh, File destination) {
        File f = null;
        if (fh.isDirectory() && fh.isUnicode()) {
            f = new File(destination, fh.getFileNameW());
            if (!f.exists()) {
                makeDirectory(destination, fh.getFileNameW());
            }
        } else if (fh.isDirectory() && !fh.isUnicode()) {
            f = new File(destination, fh.getFileNameString());
            if (!f.exists()) {
                makeDirectory(destination, fh.getFileNameString());
            }
        }
    }

    private void makeDirectory(File destination, String fileName) {
        String[] dirs = fileName.split("\\\\");
        if (dirs == null) {
            return;
        }
        String path = "";
        for (String dir : dirs) {
            path = path + File.separator + dir;
            new File(destination, path).mkdir();
        }

    }
}
