package ir.treeco.aftabe.packages;

import android.content.Context;
import ir.treeco.aftabe.utils.Utils;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by hossein on 9/21/14.
 */
public class MetaPackage {
    private String name, description, dataUrl;
    private int id, cost;
    private Context context;
    private PackageState state;
    private PackageManager packageManager;

    @Override
    public String toString() {
        return name + " " + description + " " + id + " " + cost + " " + state;
    }

    public Context getContext() {
        return context;
    }

    public MetaPackage(Context context, String name, String description, int id, PackageState state, PackageManager packageManager) {
        this.context = context;
        this.name = name;
        this.description = description;
        this.id = id;
        this.state = state;
        this.packageManager = packageManager;
    }

    public void setCost(int cost) {

        this.cost = cost;
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

    public String getDescription() {
        return description;
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
        this.state = PackageState.local;
        try {
            Utils.download(this.context, dataUrl, this.getName()+".zip", new NotificationProgressListener(context, this));
//            Utils.download(this.context, "http://static.treeco.ir/packages/remoteAftabe.zip", this.getName() + ".zip", new NotificationProgressListener(context, this));
//            this.load();
        } catch (Exception e) {
            e.printStackTrace();
            this.state = PackageState.remote;
        }
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
