package net.redborder.decompress.helpers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import net.sf.sevenzipjbinding.ISequentialOutStream;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;

/**
 * Created by Fernando Dominguez on 17/11/15.
 */

public class ArchiveInputStreamHandler {

    private ISimpleInArchiveItem item;
    private ByteArrayInputStream arrayInputStream;
    public ArchiveInputStreamHandler(ISimpleInArchiveItem item) {
        this.item = item;
    }

    public InputStream getInputStream() throws SevenZipException{

        item.extractSlow(new ISequentialOutStream() {
            public int write(byte[] data) throws SevenZipException {
                arrayInputStream = new ByteArrayInputStream(data);
                return data.length; // Return amount of consumed data
            }
        });
        return arrayInputStream;
    }

    public static String slurp(final InputStream is, final int bufferSize){
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        try {
            final Reader in = new InputStreamReader(is, "UTF-8");
            try {
                for (;;) {
                    int rsz = in.read(buffer, 0, buffer.length);
                    if (rsz < 0)
                        break;
                    out.append(buffer, 0, rsz);
                }
            }
            finally {
                in.close();
            }
        }
        catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return out.toString();
    }
}
