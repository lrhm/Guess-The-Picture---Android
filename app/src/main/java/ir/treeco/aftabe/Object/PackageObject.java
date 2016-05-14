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

    @Expose
    Boolean isDownloaded;

    @Expose
    Boolean isPurchased;

    @Expose
    URLHolder image;

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


    public String getUrl() {
        return "https://aftabe2.com:2020/api/files/p/download/" + file.name;
    }

    public ArrayList<Level> getLevels() {
        return levels;
    }

    public void setLevels(ArrayList<Level> levels) {
        this.levels = levels;
    }

    public String getImageUrl() {
        return "https://aftabe2.com:2020/api/pictures/p/download/" + image.name;
    }

    public Boolean getPurchased() {
        return isPurchased;
    }

    public void setPurchased(Boolean purchased) {
        isPurchased = purchased;
    }

    public Boolean getDownloaded() {
        return isDownloaded;
    }

    public void setDownloaded(Boolean downloaded) {
        isDownloaded = downloaded;
    }

    public int getPrice() {
        return price;
    }

    public String getSku() {
        return sku;
    }

    public String getHash() {
        return hash;
    }

    public URLHolder getFile() {
        return file;
    }


    private class URLHolder {

        @Expose
        String name;
    }


}
