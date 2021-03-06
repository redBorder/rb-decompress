package net.redborder.decompress.models;

import eu.medsea.mimeutil.MimeType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Fernando Domínguez on 15/12/15.
 */
public class Archive {
    private CompressionType compressionType;
    private List<ArchiveFile> files;
    private Collection<MimeType> mimeTypes;

    public Archive(CompressionType compressionType, List<ArchiveFile> files) {
        this.compressionType = compressionType;
        if (files != null) this.files = files;
        else this.files = new ArrayList<ArchiveFile>();
    }

    public Archive(CompressionType compressionType, List<ArchiveFile> files, Collection<MimeType> mimeType) {
        this.compressionType = compressionType;
        if (files != null) this.files = files;
        else this.files = new ArrayList<ArchiveFile>();
        this.mimeTypes = mimeType;
    }

    public CompressionType getCompressionType() {
        return compressionType;
    }

    public List<ArchiveFile> getFiles() {
        return files;
    }

    public Collection<MimeType> getMimeTypes() {
        return mimeTypes;
    }

    public MimeType getMimeType() {
        return (MimeType) mimeTypes.toArray()[0];
    }
}
