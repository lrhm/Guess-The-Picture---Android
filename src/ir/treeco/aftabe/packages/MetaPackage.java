package ir.treeco.aftabe.packages;

import android.content.Context;
import android.content.SharedPreferences;
import ir.treeco.aftabe.utils.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by hossein on 9/21/14.
 */
public class MetaPackage {
    private final SharedPreferences preferences;
    private Context context;
    private PackageManager packageManager;

    private PackageState state;

    private int id;
    private String name;
    private String dataUrl;
    private String sku;
    private int cost;
    private int dataVersion;
    private int rate;

    private int[] color;
    private float[] backgroundHSV;
    private float[] cheat‌ButtonHSV;

    private boolean isDownloading;

    public Context getContext() {
        return context;
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }

    public PackageManager getPackageManager() {
        return packageManager;
    }

    public int getRate() {
        return rate;
    }

    public void setIsDownloading(boolean isDownloading) {
        this.isDownloading = isDownloading;
    }

    public boolean getIsDownloading() {
        return isDownloading;
    }

    public int[] getColor() {
        return color;
    }

    public float[] getThumbnailHSV() {
        return backgroundHSV;
    }

    public float[] getCheatButtonHSV() {
        return cheat‌ButtonHSV;
    }

    public MetaPackage(Context context, SharedPreferences preferences, int[] color, float[] backgroundHSV, float[] cheatButtonHSV, String name, int id, PackageState state, PackageManager packageManager, int rate) {
        this.context = context;
        this.name = name;
        this.id = id;
        this.state = state;
        this.packageManager = packageManager;
        this.color = color;
        this.backgroundHSV = backgroundHSV;
        this.preferences = preferences;
        this.cheat‌ButtonHSV = cheatButtonHSV;
        this.isDownloading = false;
        this.rate = rate;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public void setDataVersion(int dataVersion) {
        this.dataVersion = dataVersion;
    }

    public int getDataVersion() {
        return dataVersion;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSKU() {
        return sku;
    }

    public String getDataUrl() {
        return dataUrl;
    }

    public int getCost() {
        return cost;
    }

    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }

    public PackageState getState() {
        return state;
    }

    public InputStream getFront() throws FileNotFoundException {
        return context.openFileInput(this.name + "_front.png");
    }

    public InputStream getBack() throws FileNotFoundException {
        return context.openFileInput(this.name + "_back.png");
    }

    public void becomeLocal(final DownloadProgressListener[] listeners) {
        if (state == PackageState.LOCAL || isDownloading)
            return;

        isDownloading = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Utils.download(context, dataUrl, getName() + ".zip", listeners, MetaPackage.this);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                } finally {
                    isDownloading = false;
                }

                state = PackageState.LOCAL;
                preferences.edit().putInt(name + "_DATA_VERSION", dataVersion).commit();
                packageManager.generateAdapterResourceArrays();
            }
        }).start();
    }

    @Override
    public String toString() {
        return name + " " + id + " " + cost + " " + state;
    }
}
