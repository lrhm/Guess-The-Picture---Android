package ir.treeco.aftabe.packages;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import ir.treeco.aftabe.R;

/**
 * Created by hossein on 9/19/14.
 */
public class NotificationProgressListener {
    private int id;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private Context context;

    public NotificationProgressListener(Context context, Package mPackage) {
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
        //TODO set onClick for notification
    }


    int last = 0;
    public void updateBar(int progressInPercent) {
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
    }

    public void failure() {
        mBuilder.setContentText("آب قطع شد")
                .setProgress(0,0,false);
        mNotifyManager.notify(id, mBuilder.build());
    }
}
