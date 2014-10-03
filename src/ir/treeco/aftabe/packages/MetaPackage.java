package ir.treeco.aftabe.packages;

import android.content.Context;
import android.content.SharedPreferences;
import ir.treeco.aftabe.utils.Utils;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by hossein on 9/21/14.
 */
public class MetaPackage {
    private String name, dataUrl;
    private int id;
    private int cost;
    private int dataVersion;
    private Context context;
    private PackageState state;
    private PackageManager packageManager;

    @Override
    public String toString() {
        return name + " " + id + " " + cost + " " + state;
    }

    public Context getContext() {
        return context;
    }

    public MetaPackage(Context context, String name, int id, PackageState state, PackageManager packageManager) {
        this.context = context;
        this.name = name;
        this.id = id;
        this.state = state;
        this.packageManager = packageManager;
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

    public void becomeLocal() {
        try {
            Utils.download(this.context, dataUrl, this.getName()+".zip", new NotificationProgressListener(context, this));
//            Utils.download(this.context, "http://static.treeco.ir/packages/remoteAftabe.zip", this.getName() + ".zip", new NotificationProgressListener(context, this));
//            this.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.state = PackageState.local;
        SharedPreferences preferences = context.getSharedPreferences(Utils.sharedPrefrencesTag(), Context.MODE_PRIVATE);
        preferences.edit().putInt(name+"_DATA_VERSION", dataVersion).commit();
        packageManager.generateAdapterResourceArrays();
    }

    public InputStream getFront() throws FileNotFoundException {
        if(this.state == PackageState.builtIn)
            return Utils.getInputStreamFromRaw(this.context, this.name+"_front","jpg");
        else
            return context.openFileInput(this.name+"_front.jpg");
    }

    public InputStream getBack() throws FileNotFoundException {
        if (this.state == PackageState.builtIn)
            return Utils.getInputStreamFromRaw(this.context, this.name + "_back", "jpg");
        else
            return context.openFileInput(this.name + "_back.jpg");
    }
}
