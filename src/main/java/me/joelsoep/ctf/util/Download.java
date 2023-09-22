package me.joelsoep.ctf.util;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.GZIPOutputStream;

public class Download {

    public static void compress(String file, String gzipFile) {
        try {
            FileInputStream fis = new FileInputStream(file);
            FileOutputStream fos = new FileOutputStream(gzipFile);
            GZIPOutputStream gzipOS = new GZIPOutputStream(fos);
            byte[] buffer = new byte[1024];
            int len;
            while((len=fis.read(buffer)) != -1){
                gzipOS.write(buffer, 0, len);
            }
            //close resources
            gzipOS.close();
            fos.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

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
