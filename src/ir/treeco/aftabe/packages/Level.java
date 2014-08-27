package ir.treeco.aftabe.packages;

import android.content.Context;
import ir.treeco.aftabe.utils.Encryption;
import ir.treeco.aftabe.utils.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by hossein on 7/31/14.
 */
public class Level {
    private String soultion, author;
    private Package wrapperPackage;
    private int id;
    private Context context;

    public String getSoultion() {
        return Encryption.decrypt(soultion);
    }

    public String getAuthor() {
        return author;
    }

    public Package getWrapperPackage() {
        return wrapperPackage;
    }

    public int getId() {
        return id;
    }

    public InputStream getImage() throws Exception {
        if(wrapperPackage.getState() == PackageState.builtIn) {
            ZipInputStream zipInputStream = new ZipInputStream(Utils.getInputStreamFromRaw(context,wrapperPackage.getName()
                            ,"zip"));
            for(ZipEntry e; (e=zipInputStream.getNextEntry())!=null ; ) {
                if(e.getName().equals(id+".jpg"))
                    break;
            }
            return zipInputStream;
        }
        else {
            ZipEntry entry = this.wrapperPackage.getData().getEntry(id+".jpg");
            return this.wrapperPackage.getData().getInputStream(entry);
        }
    }

    public Level(Context context, String author, String solution, Package wPackage, int id) {
        this.context = context;
        this.author = author;
        this.soultion = solution;
        this.wrapperPackage = wPackage;
        this.id = id;
    }
}
