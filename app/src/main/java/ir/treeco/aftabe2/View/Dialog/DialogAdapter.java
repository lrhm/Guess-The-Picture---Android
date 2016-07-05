package ir.treeco.aftabe2.View.Dialog;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.pixplicity.easyprefs.library.Prefs;

import ir.treeco.aftabe2.Object.User;
import ir.treeco.aftabe2.R;
import ir.treeco.aftabe2.Synchronization.Synchronize;
import ir.treeco.aftabe2.View.Custom.ToastMaker;

/**
 * Created by root on 5/11/16.
 */
public class DialogAdapter {

    public static void makeFriendRequestDialog(Context context, View.OnClickListener yesClick) {
        String msg = "درخواست دوستی";
        String yes = "بفرست";
        String no = "نفرست";

        if (DialogAdapter.checkInternetConnection(context))
            new SkipAlertDialog(context, msg, yesClick, null).show();
    }

    public static void makeFriendRemoveDialog(Context context, View.OnClickListener yesClick) {
        String msg = "حذف دوستی";
        String yes = "بکن";
        String no = "نکن";
        if (DialogAdapter.checkInternetConnection(context))
            new SkipAlertDialog(context, msg, yesClick, null).show();

    }

    public static void makeMatchRequestDialog(Context context, User user, View.OnClickListener yesClick) {

        if (DialogAdapter.checkInternetConnection(context))
            new MatchRequestDialog(context, user, true, yesClick).show();

    }


    public static boolean makeTutorialDialog(Context context, String firstLine, String secondLine) {


        String text = (secondLine.equals("")) ? firstLine : String.format("%s\n%s", firstLine, secondLine);

        if (Prefs.contains(text.hashCode() + ""))
            return false;
        Prefs.putBoolean(text.hashCode() + "", true);
        SkipAlertDialog skipAlertDialog = new SkipAlertDialog(context, text);
        skipAlertDialog.setCanceledOnTouchOutside(false);

        skipAlertDialog.show();

        return true;

    }

    private static long lastTimeToastShowd = 0;

    public static boolean checkInternetConnection(Context context) {
        if (!Synchronize.isOnline(context)) {

            long curTime = System.currentTimeMillis();
            if (curTime - lastTimeToastShowd > 2000) {
                ToastMaker.show(context, context.getResources().getString(R.string.connection_to_internet_sure), Toast.LENGTH_SHORT);
                lastTimeToastShowd = curTime;
            }

        } else return true;
        return false;

    }

    public static void enemyInternetConnectionFailed(Context context) {

        if (Synchronize.isOnline(context)) {
            long curTime = System.currentTimeMillis();
            if (curTime - lastTimeToastShowd > 2000) {
                ToastMaker.show(context, context.getResources().getString(R.string.enemy_internet_fucked), Toast.LENGTH_SHORT);
                lastTimeToastShowd = curTime;
            }
        }


    }
}
