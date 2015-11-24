package net.redborder.decompress;

import net.redborder.apache.commons.compress.PasswordRequiredException;
import net.redborder.decompress.exceptions.PasswordProtectedException;
import net.redborder.decompress.models.ArchiveFile;

import java.util.List;

/**
 * Created by fernando on 06/11/15.
 */
public interface Decompressor {

    public List<ArchiveFile> decompress() throws PasswordRequiredException;
}
