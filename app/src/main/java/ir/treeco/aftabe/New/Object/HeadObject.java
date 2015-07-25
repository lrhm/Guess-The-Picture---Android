package ir.treeco.aftabe.New.Object;

import java.util.ArrayList;

public class HeadObject {
    private ArrayList<PackageObject> saller;
    private ArrayList<PackageObject> news;
    private ArrayList<PackageObject> downloaded;
    private ArrayList<PackageObject> downloadtask;
    private ArrayList<PackageObject> local;

    public ArrayList<PackageObject> getSaller ()
    {
        return saller;
    }

    public void setSaller (ArrayList<PackageObject> saller)
    {
        this.saller = saller;
    }

    public ArrayList<PackageObject> getNews ()
    {
        return news;
    }

    public void setNews (ArrayList<PackageObject> news)
    {
        this.news = news;
    }

    public ArrayList<PackageObject> getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(ArrayList<PackageObject> downloaded) {
        this.downloaded = downloaded;
    }

    public ArrayList<PackageObject> getDownloadtask() {
        return downloadtask;
    }

    public void setDownloadtask(ArrayList<PackageObject> downloadtask) {
        this.downloadtask = downloadtask;
    }

    public ArrayList<PackageObject> getLocal() {
        return local;
    }

    public void setLocal(ArrayList<PackageObject> local) {
        this.local = local;
    }
}


