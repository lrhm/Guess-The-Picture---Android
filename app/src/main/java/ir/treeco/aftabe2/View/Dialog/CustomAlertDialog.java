package ir.treeco.aftabe2.View.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import ir.treeco.aftabe2.R;
import ir.treeco.aftabe2.Util.FontsHolder;
import ir.treeco.aftabe2.Util.SizeManager;
import ir.treeco.aftabe2.Util.Tools;

/**
 * Created by root on 5/2/16.
 */
public class CustomAlertDialog extends Dialog {

    String message;
    String okMsg;
    String cancelMsg;
    TextView.OnClickListener okListener;
    TextView.OnClickListener cancelListener;
    Context context;
    private OnDismissListener onDismissListener;

    public CustomAlertDialog(Context context, String msg, String okMsg, TextView.OnClickListener okListener,
                             String cancelMsg, TextView.OnClickListener cancelListener) {
        super(context);
        this.context = context;
        message = msg;
        this.okMsg = okMsg;
        this.okListener = okListener;
        this.cancelMsg = cancelMsg;
        this.cancelListener = cancelListener;


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_alert);


        TextView mainTextView = (TextView) findViewById(R.id.skip_dialog_main_text);
        TextView cancelTextView = (TextView) findViewById(R.id.dialog_skip_cancel);
        TextView okTextView = (TextView) findViewById(R.id.dialog_skip_ok);

        setWidth(mainTextView, 0.7, message);
        setWidth(cancelTextView, 0.3, cancelMsg);
        setWidth(okTextView, 0.3, okMsg);
        cancelTextView.getLayoutParams().height = (int) (SizeManager.getScreenHeight() * 0.07);
        cancelTextView.setOnClickListener((cancelListener == null) ? new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        } : cancelListener);

        okTextView.getLayoutParams().height = (int) (SizeManager.getScreenHeight() * 0.07);
        okTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (okListener != null)
                    okListener.onClick(v);
                dismiss();
            }
        });

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) cancelTextView.getLayoutParams();
        layoutParams.leftMargin = Tools.convertDPtoPixel(20, getContext());


    }

    public void setWidth(TextView textView, double percent, String text) {
        textView.getLayoutParams().width = (int) (SizeManager.getScreenWidth() * percent);
        textView.setTypeface(FontsHolder.getSansMedium(getContext()));
        textView.setTextColor(Color.WHITE);
        textView.setText(text);
        textView.setGravity(Gravity.CENTER);
    }


    @Override
    public void dismiss() {
        if (onDismissListener != null) onDismissListener.onDismiss();
        super.dismiss();
    }

    public CustomAlertDialog setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
        return this;
    }


    public interface OnDismissListener {
        void onDismiss();
    }
}
