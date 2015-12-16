package net.redborder.decompress.models;

import eu.medsea.mimeutil.MimeType;

/**
 * Created by fernando on 15/12/15.
 */
public class CompressionType {

    private String extension;
    private MimeType mimeType;

    public CompressionType(String extension, MimeType mimeTypes) {
        this.extension = extension;
        this.mimeType = mimeTypes;
    }

    public String getExtension() {
        return extension;
    }

    public MimeType getMimeType() {
        return mimeType;
    }

    @Override
    public boolean equals(Object another) {
        if (another instanceof CompressionType) {
            CompressionType anotherCompressionType = (CompressionType) another;
            return this.mimeType.equals(anotherCompressionType.mimeType);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return mimeType.toString();
    }
}
