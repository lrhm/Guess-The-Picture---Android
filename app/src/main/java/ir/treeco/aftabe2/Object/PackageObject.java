package ir.treeco.aftabe2.Object;

import android.content.Context;

import com.google.gson.annotations.Expose;

import java.io.File;
import java.util.ArrayList;

import ir.treeco.aftabe2.Adapter.Cache.PackageSolvedCache;
import ir.treeco.aftabe2.Util.Tools;

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
    int revision;

    URLHolder offer;

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
        return Tools.getUrl() + "api/files/p/download/" + file.name;
    }

    public ArrayList<Level> getLevels() {
        return levels;
    }

    public void setLevels(ArrayList<Level> levels) {
        this.levels = levels;
    }

    public String getImageUrl() {
        return Tools.getUrl() + "api/pictures/p/download/" + image.name;
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


    public int getRevisionFile() {

        return revision;
    }

    public boolean isPackageDownloaded(Context context) {
        return new File(context.getFilesDir().getPath() + "/Packages/package_" + id + "/").exists();
    }

    public boolean isThereOffer() {
        return offer != null && !offer.equals("");
    }

    public String getOfferImageURL() {
        return Tools.getUrl() + "api/pictures/offer/download/" + offer.name;
    }

    public URLHolder getOffer() {
        return offer;
    }

    public boolean isOfferDownloaded(Context context) {
        return new File(getOfferImagePathInSD(context)).exists();

    }

    public int getShownPrice(Context context) {

        User myUser = Tools.getCachedUser(context);
        int intPrice = ((myUser != null && myUser.isPackagePurchased(id))) ? 0 : getPrice();
        intPrice = PackageSolvedCache.getInstance().isPackagePurchased(id) ? 0 : intPrice;
        return intPrice;
    }

    public String getOfferImagePathInSD(Context context) {
        return context.getFilesDir().getPath() + "/package_" + id + "_offer.png";

    }

    public boolean isFrontImageExist(Context context) {
        return new File(context.getFilesDir().getPath() + "/package_" + id + "_" + "front" + ".png").exists();
    }

    public boolean shouldShowPackage(Context context) {

        if (isPackageDownloaded(context))
            return true;

        if (isThereOffer() && isOfferDownloaded(context))
            return true;

        if (isFrontImageExist(context))
            return true;

        return false;
    }

    private class URLHolder {

        @Expose
        String name;

        @Expose
        int size;
    }


}
