package ir.treeco.aftabe.New.Util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Zip {
    public Zip() {
    }

    public boolean unpackZip(String path, int id, Context context) {
        Log.e("path", "start");
//        String fileName=path.substring(path.lastIndexOf("/")+1);
//        Log.e("path", "filename: " + fileName);

//        String zipdir = path.substring(path.lastIndexOf("/"));
//
//        Log.e("path", "zipdir: " + zipdir);
//        File newpathdir = new File(zipdir + fileName);
//        newpathdir.mkdirs();

        String newPath = context.getFilesDir().getPath() + "/Downloaded/";
        Log.e("path", "newPath: " + newPath);

                    File fmd1 = new File(newPath);
                    fmd1.mkdirs();
        InputStream is;
        ZipInputStream zis;
        try {
            String filename;
            is = new FileInputStream(path);
            zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze;
            byte[] buffer = new byte[1024];
            int count;

            while ((ze = zis.getNextEntry()) != null) {
                // zapis do souboru
                filename = ze.getName();

                // Need to create directories if not exists, or
                // it will generate an Exception...
//                if (ze.isDirectory()) {
//                    File fmd = new File(newPath + filename);
//                    fmd.mkdirs();
//                    continue;
//                }

                FileOutputStream fout = new FileOutputStream(newPath + id + "_" +filename);

                // cteni zipu a zapis
                while ((count = zis.read(buffer)) != -1) {
                    fout.write(buffer, 0, count);
                }

                fout.close();
                zis.closeEntry();
            }

            zis.close();
        }
        catch(IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
