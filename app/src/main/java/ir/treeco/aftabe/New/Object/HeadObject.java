package ir.treeco.aftabe.New.Object;

public class HeadObject {
    private PackageObject[] saller;
    private PackageObject[] news;
    private PackageObject[] downloadtask;
    private PackageObject[] notification;
    private PackageObject[] local;

    public PackageObject[] getNotification() {
        return notification;
    }

    public void setNotification(PackageObject[] notification) {
        this.notification = notification;
    }

    public PackageObject[] getSaller() {
        return saller;
    }

    public void setSaller(PackageObject[] saller) {
        this.saller = saller;
    }

    public PackageObject[] getNews() {
        return news;
    }

    public void setNews(PackageObject[] news) {
        this.news = news;
    }

    public PackageObject[] getDownloadtask() {
        return downloadtask;
    }

    public void setDownloadtask(PackageObject[] downloadtask) {
        this.downloadtask = downloadtask;
    }

    public PackageObject[] getLocal() {
        return local;
    }

    public void setLocal(PackageObject[] local) {
        this.local = local;
    }
}


