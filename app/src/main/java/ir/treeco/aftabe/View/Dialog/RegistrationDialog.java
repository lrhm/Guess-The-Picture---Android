package ir.treeco.aftabe.View.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.common.SignInButton;

import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.SizeConverter;
import ir.treeco.aftabe.Util.SizeManager;
import ir.treeco.aftabe.Util.Tools;
import ir.treeco.aftabe.View.Activity.MainActivity;
import ir.treeco.aftabe.View.Custom.DialogDrawable;

public class RegistrationDialog extends Dialog {
    Context context;
    Tools tools;
    ImageManager imageManager;

    public RegistrationDialog(Context context) {
        super(context);
        this.context = context;
        tools = new Tools(context);
        imageManager = ((MainApplication) getContext().getApplicationContext()).getImageManager();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#90000000")));
        setContentView(R.layout.dialog_registration);


        SizeConverter sizeConverter = SizeConverter.SizeConvertorFromWidth(SizeManager.getScreenWidth() * 0.8f, 1559, 300);

        ImageView gmailImageView = (ImageView) findViewById(R.id.gmail_registration_image_view);

        ImageView phoneImageView = (ImageView) findViewById(R.id.phone_registration_image_view);


        gmailImageView.setImageBitmap(imageManager.loadImageFromResource(R.drawable.login_gmail, sizeConverter.mWidth, sizeConverter.mHeight));
        phoneImageView.setImageBitmap(imageManager.loadImageFromResource(R.drawable.login_phone, sizeConverter.mWidth, sizeConverter.mHeight));


        gmailImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) context).signInWithGoogle();
                dismiss();
            }
        });

        phoneImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SMSRegisterDialog(context, ((MainActivity) context)).show();
                dismiss();

            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = SizeManager.getScreenWidth();
        lp.height = SizeManager.getScreenHeight();
        getWindow().setAttributes(lp);

    }

}

