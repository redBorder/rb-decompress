package net.redborder.decompress.helpers;

import eu.medsea.mimeutil.MimeException;
import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil;
import net.redborder.decompress.constants.Extensions;
import net.redborder.decompress.models.CompressionType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Fernando Domínguez on 06/11/15.
 */
public class FileHelper {

    public static boolean mimeDetectorRegistered = false;

    // Returns the name for a file without extension
    public static String nameFromFile(File file){
        return nameFromFile(file.getName());
    }

    // Returns the name for a file without extension
    public static String nameFromFile(String file){
        String name = "";
        try {
            name = file.substring(0, file.lastIndexOf('.'));
        } catch (StringIndexOutOfBoundsException e){
            name = file;
        }
        return name;
    }

    public static String defaultExtractFolderFor(File file){
        return file.getParentFile().getAbsolutePath() + "/" + FileHelper.nameFromFile(file);
    }

    public static String extensionFromFile(File file){
        Pattern extensionPattern = Pattern.compile("(?:.+)\\.(?<ext>\\w+)");
        Matcher matcher = extensionPattern.matcher(file.getName());
        if (matcher.matches()) {
            return matcher.group("ext");
        }else{
            return null;
        }
    }

    public static String mimeTypeToExtension(String mimeType){
        String extension = null;

        if (mimeType.equals(Extensions.ZIP_MIMETYPE)){
            extension = Extensions.ZIP_EXTENSION;
        }else if (mimeType.equals(Extensions.RAR_MIMETYPE)){
            extension = Extensions.RAR_EXTENSION;
        }else if (mimeType.equals(Extensions.TAR_MIMETYPE)){
            extension = Extensions.TAR_EXTENSION;
        }else if (mimeType.equals(Extensions.GZIP_MIMETYPE)){
            extension = Extensions.GZIP_EXTENSION;
        }else if (mimeType.equals(Extensions.SEVENZIP_MIMETYPE)){
            extension = Extensions.SEVENZIP_EXTENSION;
        }

        return extension;
    }

    public static String mimeTypeToExtension(Collection<MimeType> mimeTypes){
        String extension = null;

        for (MimeType mimeType : mimeTypes){

            extension = mimeTypeToExtension(mimeType.toString());
            // Break the loop if a match is found
            if (extension != null) break;
        }

        return extension;
    }

    /**
     * Returns the compression type for a given file
     * @param file The file
     * @return the compression type
     */
    public static CompressionType fileToCompressionType(File file){
        CompressionType compressionType = null;
        Collection<MimeType> mimeTypes = FileHelper.getMimeTypes(file);
        compressionType = mimeToCompressionType(mimeTypes);

        if (compressionType == null){
            compressionType = new CompressionType(FileHelper.extensionFromFile(file),
                    (MimeType) mimeTypes.toArray()[0]);
        }

        return compressionType;
    }

    /**
     * Returns the compression type for a given mime type
     * @param mimeTypes A list of mime types for a file
     * @return the compression type
     */
    private static CompressionType mimeToCompressionType(Collection<MimeType> mimeTypes){
        CompressionType compressionType = null;

        for (MimeType mimeType : mimeTypes){
            if (mimeType.toString().equals(Extensions.ZIP_MIMETYPE)){
                compressionType = new CompressionType(Extensions.ZIP_EXTENSION, mimeType);
            }else if (mimeType.toString().equals(Extensions.RAR_MIMETYPE)){
                compressionType = new CompressionType(Extensions.RAR_EXTENSION, mimeType);
            }else if (mimeType.toString().equals(Extensions.TAR_MIMETYPE)){
                compressionType = new CompressionType(Extensions.TAR_EXTENSION, mimeType);
            }else if (mimeType.toString().equals(Extensions.GZIP_MIMETYPE)){
                compressionType = new CompressionType(Extensions.GZIP_EXTENSION, mimeType);
            }else if (mimeType.toString().equals(Extensions.SEVENZIP_MIMETYPE)){
                compressionType = new CompressionType(Extensions.SEVENZIP_EXTENSION, mimeType);
            }

            // Break the loop if a match is found
            if (compressionType != null) break;
        }

        return compressionType;
    }

    /**
    * Returns the mime types for a file
    * */
    public static Collection<MimeType> getMimeTypes(File file){
        registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
        return (Collection<MimeType>) MimeUtil.getMimeTypes(file);
    }

    /**
    * Checks that a file corresponds to the given mime type
    * */
    public static boolean checkMime(File file, MimeType mimeType){
        Collection<MimeType> mimeTypes = FileHelper.getMimeTypes(file);
        return mimeTypes.contains(mimeType);
    }

    public static String calculateHash(File file, String algorithm)
            throws NoSuchAlgorithmException, IOException{

        String hash = null;
        FileInputStream inputStream = new FileInputStream(file);
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        byte[] buffer = new byte[1024];
        int bytesRead = 0;

        while ((bytesRead = inputStream.read(buffer)) != -1){
            digest.update(buffer, 0, bytesRead);
        }

        byte[] hashedBytes = digest.digest();
        hash = convertByteArrayToHexString(hashedBytes);


        return hash;
    }

    public static String parentPath(File file){
        return file.getAbsolutePath().substring( 0, file.getAbsolutePath().lastIndexOf('/') );
    }

    public static String parentPath(String path){
        return path.substring( 0, path.lastIndexOf('/') );
    }

    /* Private methods */

    private static String convertByteArrayToHexString(byte[] arrayBytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arrayBytes.length; i++) {
            stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return stringBuffer.toString();
    }

    private static void registerMimeDetector(String detectorClass){
        if (!mimeDetectorRegistered) {
            MimeUtil.registerMimeDetector(detectorClass);
            mimeDetectorRegistered = true;
        }
    }
}
