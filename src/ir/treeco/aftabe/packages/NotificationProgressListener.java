package ir.treeco.aftabe.packages;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import ir.treeco.aftabe.R;

/**
 * Created by hossein on 9/19/14.
 */
public class NotificationProgressListener implements DownloadProgressListener {
    private int id;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private Context context;
    private MetaPackage metaPackage;

    public NotificationProgressListener(Context context, MetaPackage mPackage) {
        this.context = context;
        id = (int) (Math.random()*1000);
        mNotifyManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle(mPackage.getName())
                .setContentText("در حال آبگیری")
                .setSmallIcon(R.drawable.tiny);
        mBuilder.setProgress(100, 0, false);
        mNotifyManager.notify(id, mBuilder.build());
        metaPackage = mPackage;
        //TODO set onClick for notification
    }


    int last = 0;

    public void update(int progressInPercent) {
        if (last != progressInPercent) {
            mBuilder.setProgress(100, progressInPercent, false);
            mNotifyManager.notify(id, mBuilder.build());
        }
        last = progressInPercent;
    }

    public void success() {
        mBuilder.setContentText("آفتابه پر شد")
                .setProgress(0,0,false);
        mNotifyManager.notify(id, mBuilder.build());
        metaPackage.setState(PackageState.LOCAL);
    }

    public void failure() {
        mBuilder.setContentText("آب قطع شد")
                .setProgress(0,0,false);
        mNotifyManager.notify(id, mBuilder.build());
        metaPackage.setState(PackageState.REMOTE);
    }
}
