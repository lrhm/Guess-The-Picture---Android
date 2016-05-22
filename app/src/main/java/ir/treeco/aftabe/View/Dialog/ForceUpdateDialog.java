package ir.treeco.aftabe.View.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import ir.treeco.aftabe.API.Socket.SocketAdapter;
import ir.treeco.aftabe.Adapter.ForceAdapter;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.DownloadTask;
import ir.treeco.aftabe.Util.FontsHolder;
import ir.treeco.aftabe.Util.SizeManager;
import ir.treeco.aftabe.Util.Tools;
import ir.treeco.aftabe.Util.UiUtil;
import ir.treeco.aftabe.View.Activity.MainActivity;
import ir.treeco.aftabe.View.Custom.ToastMaker;

public class ForceUpdateDialog extends Dialog implements View.OnClickListener, DownloadTask.DownloadTaskListener {
    Context context;

    boolean showButton;
    TextView progresTextView;

    public ForceUpdateDialog(Context context, boolean showButton) {
        super(context);
        SocketAdapter.disconnect();
        this.context = context;
        this.showButton = showButton;

        ForceAdapter.getInstance(context).setListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        setContentView(R.layout.dialog_force_update);
        TextView textView = (TextView) findViewById(R.id.dialog_force_update_text);

        Button button = (Button) findViewById(R.id.dialog_force_update_button);
        button.setOnClickListener(this);
        if (showButton)
            button.setVisibility(View.VISIBLE);

        button.setText("اینجا کلیک کن");
        button.setTextSize(TypedValue.COMPLEX_UNIT_PX, SizeManager.getScreenWidth() * 0.04f);

        textView.setText("اپدیت جدید اومده . باید اپدیت کنی");
        UiUtil.setTextViewSize(textView, (int) (SizeManager.getScreenWidth() * 0.3), 0.2f);
        textView.setTypeface(FontsHolder.getSansBold(context));

        progresTextView = (TextView) findViewById(R.id.dialog_force_update_progress);
        UiUtil.setTextViewSize(progresTextView, (int) (SizeManager.getScreenWidth() * 0.3), 0.2f);
        progresTextView.setTypeface(FontsHolder.getNumeralSansBold(context));

        UiUtil.setTopMargin(progresTextView, (int) (SizeManager.getScreenHeight() * 0.1f));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = SizeManager.getScreenWidth();
        lp.height = SizeManager.getScreenHeight();
        getWindow().setAttributes(lp);

    }

    @Override
    public void onClick(View view) {

        ForceAdapter.getInstance(context).openCafeBazzarAppPage((MainActivity) context);
    }

    @Override
    public void onBackPressed() {


//        super.onBackPressed();
        ((MainActivity) context).onBackPressed();
    }

    @Override
    public void onProgress(final int progress) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                progresTextView.setText(Tools.numeralStringToPersianDigits(progress + "%"));

            }
        });
    }

    @Override
    public void onDownloadSuccess() {

    }

    @Override
    public void onDownloadError(String error) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                ToastMaker.show(context, "لطفا از اتصاد به اینرنت مطمین شوید", Toast.LENGTH_SHORT);

            }
        });

    }
}

