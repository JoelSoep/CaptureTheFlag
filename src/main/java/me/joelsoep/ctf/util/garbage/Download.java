package me.joelsoep.ctf.util.garbage;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.GZIPOutputStream;

@Deprecated
public class Download {

   // https://drive.google.com/file/d/1Y_7FjXvF7-oedh1ffbssYODu2mxWu8Je/view?usp=sharing lobby
   // https://drive.google.com/file/d/1ZwBS5lFekC8MFmBcbcIl73nPr6isK1oB/view?usp=sharing map


    public static void download(URL url, String output) throws IOException {
        Files.copy(url.openStream(), Paths.get(output));
    }

    public static void unzip(String source, String destination){
        try {
            ZipFile zipFile = new ZipFile(source);
            zipFile.extractAll(destination);
        } catch (ZipException e) {
            e.printStackTrace();
        }
    }
}
