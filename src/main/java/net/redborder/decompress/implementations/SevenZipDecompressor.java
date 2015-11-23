package net.redborder.decompress.implementations;

import net.redborder.decompress.AbstractDecompressor;
import net.redborder.decompress.Decompressor;
import net.redborder.decompress.helpers.ArchiveInputStreamHandler;
import net.redborder.decompress.helpers.FileHelper;
import net.redborder.decompress.models.ArchiveFile;
import net.sf.sevenzipjbinding.*;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;
import net.redborder.apache.commons.compress.utils.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Fernando Domínguez on 09/11/15.
 */
public class SevenZipDecompressor extends AbstractDecompressor implements Decompressor {

    /* Constructors */

    public SevenZipDecompressor(File archive) {
        super(archive);
    }

    public SevenZipDecompressor(File archive, File outputDir) {
        super(archive, outputDir);
    }

    /* Public methods */

    public List<ArchiveFile> decompress() {
        List<ArchiveFile> files = null;
        try{
            SevenZip.initSevenZipFromPlatformJAR();
            if (outputDir != null) files = decompress7Zip(archive, outputDir);
            else files = decompress7Zip(archive);
        }catch (IOException e){
            e.printStackTrace();
        } catch (SevenZipNativeInitializationException e) {
            e.printStackTrace();
        }
        return files;
    }

    /* Private methods */

    private List<ArchiveFile> decompress7Zip(File archive, File outputDir) throws IOException{

        if (outputDir != null) outputDir.mkdirs();

        List<ArchiveFile> files = new ArrayList<ArchiveFile>();
        RandomAccessFile randomAccessFile = new RandomAccessFile(archive, "r");
        IInArchive inArchive = null;
        try {
            inArchive = SevenZip.openInArchive(null,
                    new RandomAccessFileInStream(randomAccessFile));

            ISimpleInArchive archiveinterface = inArchive.getSimpleInterface();
            for (ISimpleInArchiveItem item : archiveinterface.getArchiveItems()){
                if (!item.isFolder()) {
                    String filename = outputDir + "/" + item.getPath();
                    InputStream inputStream = new ArchiveInputStreamHandler(item).getInputStream();
                    byte[] content = new byte[item.getSize().intValue()];
                    inputStream.read(content, 0, content.length);
                    FileOutputStream outputStream = new FileOutputStream(new File(FileHelper.parentPath(filename)));
                    IOUtils.copy(inputStream, outputStream);
                    files.add(new ArchiveFile(item.getPath(), content));
                }else{
                    new File(outputDir, item.getPath()).mkdirs();
                }
            }

        } catch (SevenZipException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inArchive != null) inArchive.close();
            } catch (SevenZipException e) {
                e.printStackTrace();
            }
        }

        return files;
    }

    private List<ArchiveFile> decompress7Zip(File archive) throws IOException{

        List<ArchiveFile> files = new ArrayList<ArchiveFile>();
        RandomAccessFile randomAccessFile = new RandomAccessFile(archive, "r");
        IInArchive inArchive = null;
        try {
            inArchive = SevenZip.openInArchive(null,
                    new RandomAccessFileInStream(randomAccessFile));

            ISimpleInArchive archiveinterface = inArchive.getSimpleInterface();
            for (ISimpleInArchiveItem item : archiveinterface.getArchiveItems()){
                if (!item.isFolder()) {
                    InputStream inputStream = new ArchiveInputStreamHandler(item).getInputStream();
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    IOUtils.copy(inputStream, outputStream);
                    files.add(new ArchiveFile(item.getPath(), outputStream.toByteArray()));
                }
            }

        } catch (SevenZipException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inArchive != null) inArchive.close();
            } catch (SevenZipException e) {
                e.printStackTrace();
            }
        }

        return files;
    }

    private void extractArchive(RandomAccessFile file) {
        IInArchive inArchive = null;
        final byte[][] content = new byte[0][];
        try {
            inArchive = SevenZip.openInArchive(null,
                    new RandomAccessFileInStream(file));

            ISimpleInArchive archiveinterface = inArchive.getSimpleInterface();
            for (ISimpleInArchiveItem item : archiveinterface.getArchiveItems()){
                final int[] hash = new int[] { 0 };
                if (!item.isFolder()) {
                    InputStream inputStream = new ArchiveInputStreamHandler(item).getInputStream();
                }
            }

        } catch (SevenZipException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inArchive != null) inArchive.close();
            } catch (SevenZipException e) {
               e.printStackTrace();
            }
        }
    }

    public static class ExtractCallback implements IArchiveExtractCallback {
        private int hash = 0;
        private int size = 0;
        private int index;
        private boolean skipExtraction;
        private IInArchive inArchive;

        public ExtractCallback(IInArchive inArchive) {
            this.inArchive = inArchive;
        }

        public ISequentialOutStream getStream(int index,
                                              ExtractAskMode extractAskMode) throws SevenZipException {
            this.index = index;
            skipExtraction = (Boolean) inArchive
                    .getProperty(index, PropID.IS_FOLDER);
            if (skipExtraction || extractAskMode != ExtractAskMode.EXTRACT) {
                return null;
            }
            return new ISequentialOutStream() {
                public int write(byte[] data) throws SevenZipException {
                    hash ^= Arrays.hashCode(data);
                    size += data.length;
                    return data.length; // Return amount of proceed data
                }
            };
        }

        public void prepareOperation(ExtractAskMode extractAskMode)
                throws SevenZipException {
        }

        public void setOperationResult(ExtractOperationResult
                                               extractOperationResult) throws SevenZipException {
            if (skipExtraction) {
                return;
            }
            if (extractOperationResult != ExtractOperationResult.OK) {
                System.err.println("Extraction error");
            } else {
                System.out.println(String.format("%9X | %10s | %s", hash, size,//
                        inArchive.getProperty(index, PropID.PATH)));
                hash = 0;
                size = 0;
            }
        }

        public void setCompleted(long completeValue) throws SevenZipException {
        }

        public void setTotal(long total) throws SevenZipException {
        }
    }


}
