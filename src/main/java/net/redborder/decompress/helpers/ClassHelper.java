package net.redborder.decompress.helpers;

import net.redborder.decompress.constants.Extensions;
import org.apache.commons.lang3.text.WordUtils;

/**
 * Created by Fernando Domínguez on 09/11/15.
 */
public class ClassHelper {

    public static String class_from_extension(String extension){

        String className = "net.redborder.decompress.implementations.";
        StringBuilder builder = new StringBuilder(className);
        // 7zip is the only one where class name and extension do not follow the usual pattern
        if (extension.equals(Extensions.SEVENZIP_EXTENSION)){
            builder.append("SevenZipDecompressor");
        }else{
            builder.append(String.format("%sDecompressor", WordUtils.capitalize(extension)));
        }

        return builder.toString();
    }
}
