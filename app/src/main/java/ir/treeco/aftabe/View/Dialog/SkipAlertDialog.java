package ir.treeco.aftabe.View.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.FontsHolder;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.SizeManager;
import ir.treeco.aftabe.Util.UiUtil;

/**
 * Created by root on 5/2/16.
 */
public class SkipAlertDialog extends Dialog {

    String message;
    TextView.OnClickListener okListener;
    TextView.OnClickListener cancelListener;
    Context context;
    private OnDismissListener onDismissListener;

    public SkipAlertDialog(Context context, String msg, TextView.OnClickListener okListener,
                           TextView.OnClickListener cancelListener) {
        super(context);
        this.context = context;
        message = msg;
        this.okListener = okListener;
        this.cancelListener = cancelListener;


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_skip);

        ImageManager imageManager = new ImageManager(context);

        TextView mainTextView = (TextView) findViewById(R.id.skip_dialog_main_text);

        ImageView yesImageView = (ImageView) findViewById(R.id.dialog_skip_ok_image);
        ImageView noImageView = (ImageView) findViewById(R.id.dialog_skip_cancel_image);

        UiUtil.setWidth(findViewById(R.id.dialog_skip_image_container), SizeManager.getScreenWidth());

        setWidth(mainTextView, 0.7, message);
        UiUtil.setBottomMargin(mainTextView, (int) (SizeManager.getScreenHeight() * 0.04));


        noImageView.setOnClickListener((cancelListener == null) ? new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        } : cancelListener);

        yesImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (okListener != null)
                    okListener.onClick(v);
                dismiss();
            }
        });

        int size = (int) (SizeManager.getScreenWidth() * 0.12);

        yesImageView.setImageBitmap(imageManager.loadImageFromResource(R.drawable.yes, size, size));
        noImageView.setImageBitmap(imageManager.loadImageFromResource(R.drawable.no, size, size));

        int space = (int) (SizeManager.getScreenWidth() * 0.05);
        int leftMargin = (int) (SizeManager.getScreenWidth() * 0.5 - size -space/2 );
        UiUtil.setLeftMargin(noImageView, leftMargin);
        UiUtil.setLeftMargin(yesImageView, space);

    }

    public void setWidth(TextView textView, double percent, String text) {
        textView.getLayoutParams().width = (int) (SizeManager.getScreenWidth() * percent);
        textView.setTypeface(FontsHolder.getSansMedium(getContext()));
        textView.setTextColor(Color.WHITE);
        textView.setText(text);
        textView.setGravity(Gravity.CENTER);

        UiUtil.setTextViewSize(textView, (int) (SizeManager.getScreenWidth() * percent), 0.08f);
    }


    @Override
    public void dismiss() {
        if (onDismissListener != null) onDismissListener.onDismiss();
        super.dismiss();
    }

    public SkipAlertDialog setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
        return this;
    }


    public interface OnDismissListener {
        void onDismiss();
    }
}
