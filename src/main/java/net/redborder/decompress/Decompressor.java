package net.redborder.decompress;

import net.redborder.apache.commons.compress.PasswordRequiredException;
import net.redborder.decompress.models.ArchiveFile;

import java.util.List;

public interface Decompressor {

    public List<ArchiveFile> decompress() throws PasswordRequiredException;
}
