package ir.treeco.aftabe.View.Dialog;

import android.content.Context;
import android.view.View;

import ir.treeco.aftabe.Object.User;

/**
 * Created by root on 5/11/16.
 */
public class DialogAdapter {

    public static void makeFriendRequestDialog(Context context, View.OnClickListener yesClick) {
        String msg = "درخواست دوستی";
        String yes = "بفرست";
        String no = "نفرست";
        new SkipAlertDialog(context, msg, yesClick, null).show();
    }

    public static void makeFriendRemoveDialog(Context context, View.OnClickListener yesClick) {
        String msg = "حذف دوستی";
        String yes = "بکن";
        String no = "نکن";
        new SkipAlertDialog(context, msg, yesClick , null).show();

    }

    public static void makeMatchRequestDialog(Context context, User user, View.OnClickListener yesClick) {

        new MatchRequestDialog(context, user, true, yesClick).show();

    }
}
