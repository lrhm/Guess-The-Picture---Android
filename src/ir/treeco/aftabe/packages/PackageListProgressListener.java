package ir.treeco.aftabe.packages;

import android.widget.ImageView;
import ir.treeco.aftabe.DownloadingDrawable;

/**
 * Created by hossein on 10/23/14.
 */
public class PackageListProgressListener implements DownloadProgressListener {
    DownloadingDrawable drawable;
    ImageView imageView;
    public PackageListProgressListener( ImageView imageView) {
        this.drawable = (DownloadingDrawable) imageView.getDrawable();
        this.imageView = imageView;
    }


    int last = 0;
    @Override
    public void update(int progressInPercentage) {
        if(last != progressInPercentage) {
            drawable.setPercentage(progressInPercentage);
            imageView.setImageDrawable(drawable);
        }
        last = progressInPercentage;
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
