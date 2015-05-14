package ir.treeco.aftabe.packages;

/**
 * Created by hossein on 10/23/14.
 */
public interface DownloadProgressListener {
    public void update(int progressInPercentage);
    public void success();
    public void failure();
}
