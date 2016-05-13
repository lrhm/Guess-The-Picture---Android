package ir.treeco.aftabe.Object;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class PackageObject {


    @Expose
    int id;

    @Expose
    int price;

    @Expose
    String sku;

    @Expose
    String name;

    @Expose
    String hash;

    @Expose
    URLHolder file;

    private ArrayList<Level> levels;

    public String getName() {
        return name;
    }

    public String getFileName() {
        return file.name;
    }

    public int getId() {
        return id;
    }

    // this method must be called from getted packages
    public void incrementId() {

        id++;
    }

    public String getUrl() {
        return "https://aftabe2.com:2020/api/files/p/download/" + file.name;
    }

    public ArrayList<Level> getLevels() {
        return levels;
    }

    public void setLevels(ArrayList<Level> levels) {
        this.levels = levels;
    }


    private class URLHolder {

        @Expose
        String name;
    }


}
