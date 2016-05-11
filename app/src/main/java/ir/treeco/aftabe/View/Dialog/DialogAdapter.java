package ir.treeco.aftabe.View.Dialog;

import android.content.Context;
import android.view.View;

/**
 * Created by root on 5/11/16.
 */
public class DialogAdapter {

    public static void makeFriendRequestDialog(Context context, View.OnClickListener yesClick) {
        String msg = "درخواست دوستی";
        String yes = "بفرست";
        String no = "نفرست";
        new CustomAlertDialog(context, msg, yes, yesClick, no, null).show();
    }

    public static void makeFriendRemoveDialog(Context context, View.OnClickListener yesClick){
        String msg = "حذف دوستی";
        String yes = "بکن";
        String no = "نکن";
        new CustomAlertDialog(context, msg, yes, yesClick, no, null).show();

    }

    public static void makeMatchRequestDialog(Context context, View.OnClickListener yesClick) {
        String msg = "درخواست بازی" + "\n" + "۱۰۰ سکه";
        String yes = "بفرست";
        String no = "نفرست";
        new CustomAlertDialog(context, msg, yes, yesClick, no, null).show();

    }
}
