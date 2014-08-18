package ir.treeco.packages;

import ir.treeco.utils.Encryption;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;

/**
 * Created by hossein on 7/31/14.
 */
public class Level {
    private String soultion, author;
    private Package context;
    private int id;

    public String getSoultion() {
        return Encryption.decrypt(soultion);
    }

    public String getImagePath() {
        return id+".jpg";
    }

    public String getAuthor() {
        return author;
    }

    public Package getContext() {
        return context;
    }

    public int getId() {
        return id;
    }

    public InputStream getImage() throws FileNotFoundException {
        ZipEntry entry = this.context.getData().getEntry(getImagePath());
        try {
            return this.context.getData().getInputStream(entry);
        } catch (IOException e) {
            throw new FileNotFoundException("can't find level's image");
        }
    }

    public Level(String author, String solution, Package context, int id) {
        this.author = author;
        this.soultion = solution;
        this.context = context;
        this.id = id;
    }
}
