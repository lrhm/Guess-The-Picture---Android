package ir.treeco.aftabe.packages;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import ir.treeco.aftabe.DownloadingDrawable;
import ir.treeco.aftabe.utils.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by hossein on 9/21/14.
 */
public class MetaPackage {
    private final SharedPreferences preferences;
    private String name, dataUrl;
    private int id;
    private boolean isDownloading;
    private int cost;
    private int dataVersion;
    private int rate;
    private Context context;
    private PackageState state;
    private PackageManager packageManager;
    private int[] color;
    private float[] backgroundHSV, cheat‌ButtonHSV;

    public int getRate() {
        return rate;
    }

    @Override
    public String toString() {
        return name + " " + id + " " + cost + " " + state;
    }

    public void setIsDownloading(boolean isDownloading) {
        this.isDownloading = isDownloading;
    }

    public boolean getIsDownloading() {
        return isDownloading;
    }

    public void setState (PackageState state) {
        this.state = state;
    }

    public Context getContext() {
        return context;
    }

    public int[] getColor() {
        return color;
    }

    public float[] getBackgroundHSV() {
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
        this.cheat‌ButtonHSV = cheat‌ButtonHSV;
        this.isDownloading = false;
        this.rate = rate;
    }

    public void setCost(int cost) {

        this.cost = cost;
    }

    public int getDataVersion() {
        return dataVersion;
    }

    public void setDataVersion(int dataVersion) {
        this.dataVersion = dataVersion;
    }

    public void setDataUrl(String dataUrl) {
        this.dataUrl = dataUrl;
    }

    public PackageManager getPackageManager() {
        return packageManager;
    }

    public String getName() {
        return name;
    }

    public String getDataUrl() {
        return dataUrl;
    }

    public int getId() {
        return id;
    }

    public int getCost() {
        return cost;
    }

    public PackageState getState() {
        return state;
    }

    public void becomeLocal(final DownloadProgressListener[] dpl) {
        if( state == PackageState.LOCAL || isDownloading)
            return;
        final MetaPackage metaPackage = this;
        isDownloading = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Utils.download(context, dataUrl, getName()+".zip", dpl, metaPackage);
                } catch (IOException e) {
                    e.printStackTrace();
                    metaPackage.isDownloading = false;
                    return;
                }
                metaPackage.isDownloading = false;
                state = PackageState.LOCAL;
                preferences.edit().putInt(name+"_DATA_VERSION", dataVersion).commit();
                packageManager.generateAdapterResourceArrays();
            }
        }).start();
    }

    public InputStream getFront() throws FileNotFoundException {
            return context.openFileInput(this.name+"_front.png");
    }

    public InputStream getBack() throws FileNotFoundException {
            return context.openFileInput(this.name + "_back.png");
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }
}
