package ir.treeco.aftabe2.View.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import ir.treeco.aftabe2.API.Rest.AftabeAPIAdapter;
import ir.treeco.aftabe2.API.Rest.Interfaces.UserFoundListener;
import ir.treeco.aftabe2.MainApplication;
import ir.treeco.aftabe2.Object.TokenHolder;
import ir.treeco.aftabe2.Object.User;
import ir.treeco.aftabe2.R;
import ir.treeco.aftabe2.Util.FontsHolder;
import ir.treeco.aftabe2.Util.ImageManager;
import ir.treeco.aftabe2.Util.SizeConverter;
import ir.treeco.aftabe2.Util.SizeManager;
import ir.treeco.aftabe2.Util.Tools;
import ir.treeco.aftabe2.Util.UiUtil;
import ir.treeco.aftabe2.View.Activity.MainActivity;

public class RegistrationDialog extends Dialog {
    Context context;
    Tools tools;
    ImageManager imageManager;
    private boolean showGuest = false;
    private boolean showText = false;

    public RegistrationDialog(Context context, boolean showGuest) {
        super(context);
        this.context = context;
        tools = new Tools(context);
        imageManager = ((MainApplication) getContext().getApplicationContext()).getImageManager();
        this.showGuest = showGuest;

    }

    public RegistrationDialog setTextVisible(){
        showText = true;
        return this;
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

        UiUtil.setTopMargin(phoneImageView , (int) (SizeManager.getScreenHeight() * 0.03));

        if(showText){
            TextView regText = (TextView) findViewById(R.id.text_registration);
            UiUtil.setTextViewSize(regText, (int) (SizeManager.getScreenWidth() * 0.2), 0.6f);
            regText.setGravity(Gravity.CENTER);
            regText.setVisibility(View.VISIBLE);
            regText.setTypeface(FontsHolder.getSansBold(getContext()));
            regText.setText("عضویت");
            regText.setTextColor(Color.WHITE);
            UiUtil.setBottomMargin(regText, (int) (SizeManager.getScreenHeight() * 0.1));

        }

        if (showGuest) {


            ImageView guestImageView = (ImageView) findViewById(R.id.guest_register_image_view);

            UiUtil.setTopMargin(guestImageView , (int) (SizeManager.getScreenHeight() * 0.03));

            guestImageView.setImageBitmap(imageManager.loadImageFromResource(R.drawable.login_guest, sizeConverter.mWidth, sizeConverter.mHeight));
            guestImageView.setVisibility(View.VISIBLE);
            guestImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AftabeAPIAdapter.createGuestUser(new UserFoundListener() {
                        @Override
                        public void onGetUser(User user) {

                        }

                        @Override
                        public void onGetError() {

                        }

                        @Override
                        public void onGetMyUser(User myUser) {
                            myUser.setGuest(true);
                            Tools.updateSharedPrefsToken(context, myUser, new TokenHolder(myUser));
                            ((MainActivity) context).onGetMyUser(myUser);
                        }

                        @Override
                        public void onForceLogout() {

                        }
                    });


                    dismiss();

                    DialogAdapter.makeTutorialDialog(context, "هروقت خواستید به اسم خودتون حساب بسازید کافیه روی دایره پروفایل کلیک کنید و ساخت حساب و بزنید", "");

                }
            });
        }

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = SizeManager.getScreenWidth();
        lp.height = SizeManager.getScreenHeight();
        getWindow().setAttributes(lp);

    }

}

