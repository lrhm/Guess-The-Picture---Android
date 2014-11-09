package ir.treeco.aftabe.packages;

import ir.treeco.aftabe.DownloadingDrawable;

/**
 * Created by hossein on 10/23/14.
 */
public class PackageListProgressListener implements DownloadProgressListener {
    DownloadingDrawable drawable;
    public PackageListProgressListener( DownloadingDrawable drawable) {
        this.drawable = drawable;
    }

    @Override
    public void update(int progressInPercentage) {
        drawable.setPercentage(progressInPercentage);
    }

    @Override
    public void success() {
        drawable.setPercentage(0);
    }

    @Override
    public void failure() {
        drawable.setPercentage(0);
    }
}
