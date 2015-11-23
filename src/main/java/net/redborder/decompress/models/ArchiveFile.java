package net.redborder.decompress.models;

import net.redborder.decompress.implementations.RarDecompressor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Fernando Dominguez on 10/11/15.
 */
public class ArchiveFile{

    /* Instance variables */

    private String relativePath;
    private String sha256;
    private byte[] content;

    private static Log logger = LogFactory.getLog(RarDecompressor.class.getName());

    /* Constructors */

    public ArchiveFile(String relativePath, byte[] content) {
        this.relativePath = relativePath;
        this.content = content;
    }

    /* Public methods */

    public byte[] getContent(){ return this.content; }

    public String calculateSha256(){
        sha256 = calculateHash("SHA-256");
        return sha256;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public String getSha256() {
        return sha256;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    /* Private methods */

    private String calculateHash(String algorithm){
        String hash = null;
        try{
            InputStream inputStream = new ByteArrayInputStream(content);
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] buffer = new byte[1024];
            int bytesRead = 0;

            while ((bytesRead = inputStream.read(buffer)) != -1){
                digest.update(buffer, 0, bytesRead);
            }

            byte[] hashedBytes = digest.digest();
            hash = convertByteArrayToHexString(hashedBytes);

        } catch (NoSuchAlgorithmException e){
            logger.error("The algorithm " + algorithm + " was not found");
            e.printStackTrace();
        } catch (IOException e){
            logger.error("Could not open streams for file " + this.getRelativePath());
            e.printStackTrace();
        }

        return hash;
    }

    private String convertByteArrayToHexString(byte[] arrayBytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arrayBytes.length; i++) {
            stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return stringBuffer.toString();
    }
}
