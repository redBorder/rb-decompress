package net.redborder.decompress;

import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil;
import net.redborder.apache.commons.compress.PasswordRequiredException;
import net.redborder.decompress.exceptions.UnsupportedFormatException;
import net.redborder.decompress.helpers.ClassHelper;
import net.redborder.decompress.helpers.FileHelper;
import net.redborder.decompress.implementations.RarDecompressor;
import net.redborder.decompress.models.Archive;
import net.redborder.decompress.models.ArchiveFile;
import net.redborder.decompress.models.CompressionType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;

/**
 * Created by Fernando Domínguez on 04/11/15.
 */
public class UniversalDecompressor{

    protected static Log logger = LogFactory.getLog(RarDecompressor.class.getName());

    private static boolean DECOMPRESS_ON_DEFAULT_LOCATION = false;

    /* Public static methods (public interface) */

    public static Archive decompress(File source) throws IOException, UnsupportedFormatException {

        Archive archive;
        String where = DECOMPRESS_ON_DEFAULT_LOCATION ? FileHelper.defaultExtractFolderFor(source) : null;
        archive = decompress(source, where);

        return archive;
    }

    public static Archive decompress(File source, String where) throws IOException, UnsupportedFormatException {

        Archive archive = null;

        // Check extension using magic headers
        Collection<MimeType> mimeTypes = FileHelper.getMimeTypes(source);
        String extension = FileHelper.mimeTypeToExtension(mimeTypes);
        if (extension == null) {
            // Fallback for filename checking if there was no match using magic headers
            logger.warn("Magic header check failed. Trying to probe content...");
            String mime = Files.probeContentType(source.toPath());
            extension = FileHelper.mimeTypeToExtension(mime);
            logger.info("Probing returned " + mime);
        } else {
            logger.info("The extension " + extension + " was detected for the file "
                    + source.getName() + " using magic headers");
        }

        if (extension != null) {
            try {
                File output = null;
                if (where != null) output = new File(where);
                Constructor constructor = Class.forName(ClassHelper.class_from_extension(extension))
                        .getConstructor(File.class, File.class);
                Decompressor decompressor = (Decompressor) constructor.newInstance(source, output);
                archive = decompressor.decompress();
            } catch (ClassNotFoundException e) {
                System.out.println("There is no decompressor for " + source.getName() + " (unsupported format)");
                e.printStackTrace();
                throw new UnsupportedFormatException();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }else{
            logger.error("[ERROR] File extension for " + source.getName()
                    + "could not be detected");
            throw new UnsupportedFormatException();
        }

        return archive;
    }
}
