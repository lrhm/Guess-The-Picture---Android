package ir.treeco.aftabe2.Object;

import android.content.Context;

import com.google.gson.annotations.Expose;

import java.io.File;
import java.util.ArrayList;

import ir.treeco.aftabe2.Util.Logger;

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

    @Expose
    String revision;

    URLHolder offerImage;

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
        return Logger.getUrl() + "api/files/p/download/" + file.name;
    }

    public ArrayList<Level> getLevels() {
        return levels;
    }

    public void setLevels(ArrayList<Level> levels) {
        this.levels = levels;
    }

    public String getImageUrl() {
        return Logger.getUrl() + "api/pictures/p/download/" + image.name;
    }

    public URLHolder getImage() {
        return image;
    }

    public int getPackageSize() {
        return file.size;
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


    private int getRevision(int i) {

        try {

            return Integer.parseInt(revision.replace(".", "/").split("/")[i]);
        } catch (Exception ignored) {

        }
        return 0;
    }

    public int getRevisionFile() {

        try {

            return Integer.parseInt(revision.replace(".", "/").split("/")[0]);
        } catch (Exception ignored) {

        }
        return 0;
    }

    public boolean isPackageDownloaded(Context context) {
        return new File(context.getFilesDir().getPath() + "/Packages/package_" + id + "/").exists();
    }

    public boolean isThereOffer() {
        return offerImage != null && !offerImage.equals("");
    }

    public String getOfferImageURL() {
        return Logger.getUrl() + "api/pictures/p/download/" + offerImage.name;
    }

    public boolean isOfferDownloaded(Context context) {
        return new File(getOfferImagePathInSD(context)).exists();

    }

    public String getOfferImagePathInSD(Context context) {
        return context.getFilesDir().getPath() + "package_" + id + "_offer.png";

    }

    private class URLHolder {

        @Expose
        String name;

        @Expose
        int size;
    }


}
