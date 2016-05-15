package ir.treeco.aftabe.View.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import ir.tapsell.tapselldevelopersdk.developer.DeveloperCtaInterface;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.FontsHolder;
import ir.treeco.aftabe.Util.SizeManager;
import ir.treeco.aftabe.Util.Tools;


/**
 * Created by al on 3/18/16.
 */
public class FreeCoinOfferDialog extends Dialog implements View.OnClickListener {


    private Activity mActivity;

    public FreeCoinOfferDialog(Context context, Activity activity) {
        super(context);
        mActivity = activity;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#90000000")));

        setContentView(R.layout.dialog_free_coin_offer);


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = SizeManager.getScreenWidth();
        lp.height = SizeManager.getScreenHeight();
        getWindow().setAttributes(lp);


        TextView textView = (TextView) findViewById(R.id.dialog_free_coin_offer_tv);
        TextView button = (TextView) findViewById(R.id.dialog_free_coin_offer_btn);
        String text = "سکه کافی ندارید" + "\n" + "میتوانید با دیدن تبلیغ ۲۰ سکه بدست اورید";
        textView.setGravity(Gravity.CENTER);
        textView.setTypeface(FontsHolder.getSansMedium(context));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        textView.setText(text);

        button.setGravity(Gravity.CENTER);
        button.setText("برای دیدن تبلیغ اینجا کلیک کنید");
        button.setBackgroundColor(Color.TRANSPARENT);
        button.setTypeface(FontsHolder.getSansMedium(context));
        button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        button.setOnClickListener(this);


//      TODO add button from fragment store
    }

    @Override
    public void onClick(View v) {
        DeveloperCtaInterface.getInstance().showNewCta(DeveloperCtaInterface.TAPSELL_DIRECT_ADD_REQUEST_CODE, null, mActivity);


    }
}
