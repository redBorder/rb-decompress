package net.redborder.decompress;

import net.redborder.apache.commons.compress.PasswordRequiredException;
import net.redborder.decompress.models.Archive;

import java.util.List;

public interface Decompressor {

    public Archive decompress() throws PasswordRequiredException;
}
