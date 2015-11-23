package net.redborder.decompress;

import net.redborder.decompress.helpers.FileHelper;
import net.redborder.decompress.implementations.RarDecompressor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;

/**
 * Created by fernando on 09/11/15.
 */
public abstract class AbstractDecompressor implements Decompressor{

    protected File archive;
    protected File outputDir;

    protected static Log logger = LogFactory.getLog(RarDecompressor.class.getName());

    /* Constructors */

    public AbstractDecompressor(File archive) {
        this(archive, new File(FileHelper.defaultExtractFolderFor(archive)) );
    }

    public AbstractDecompressor(File archive, File outputDir) {
        this.archive = archive;
        this.outputDir = outputDir;
    }
}
