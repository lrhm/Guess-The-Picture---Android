package ir.treeco.aftabe.New.Object;

public class HeadObject {
    private PackageObject[] saller;
    private PackageObject[] news;
    private PackageObject[] downloadtask;

    public PackageObject[] getSaller ()
    {
        return saller;
    }

    public void setSaller (PackageObject[] saller)
    {
        this.saller = saller;
    }

    public PackageObject[] getNews ()
    {
        return news;
    }

    public void setNews (PackageObject[] news)
    {
        this.news = news;
    }

    public PackageObject[] getDownloadtask() {
        return downloadtask;
    }

    public void setDownloadtask(PackageObject[] downloadtask) {
        this.downloadtask = downloadtask;
    }
}


