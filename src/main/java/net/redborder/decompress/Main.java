package net.redborder.decompress;

import net.redborder.decompress.UniversalDecompressor;
import net.redborder.decompress.models.ArchiveFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by Fernando Dominguez on 05/11/15.
 */
public class Main {

    public static void main(String[] args){
        try {
            System.out.println("Decompressing file " + args[0] + "...");
            List<ArchiveFile> files = null;
            if (args.length < 2) {
                files = UniversalDecompressor.decompress(new File(args[0]));
            }else {
                files = UniversalDecompressor.decompress(new File(args[0]), args[1]);
            }
            for (ArchiveFile file : files){
                file.calculateSha256();
            }
            System.out.println(files);
        }catch (IOException e){
            e.printStackTrace();
        };
    }
}
