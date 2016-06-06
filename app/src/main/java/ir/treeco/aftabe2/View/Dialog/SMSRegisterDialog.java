package ir.treeco.aftabe2.View.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import ir.treeco.aftabe2.Util.Logger;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import ir.treeco.aftabe2.API.Rest.AftabeAPIAdapter;
import ir.treeco.aftabe2.API.Rest.Interfaces.SMSValidationListener;
import ir.treeco.aftabe2.API.Rest.Utils.SMSCodeHolder;
import ir.treeco.aftabe2.API.Rest.Utils.SMSRequestToken;
import ir.treeco.aftabe2.API.Rest.Utils.SMSToken;
import ir.treeco.aftabe2.API.Rest.Utils.SMSValidateToken;
import ir.treeco.aftabe2.Adapter.MediaAdapter;
import ir.treeco.aftabe2.R;
import ir.treeco.aftabe2.Util.FontsHolder;
import ir.treeco.aftabe2.Util.ImageManager;
import ir.treeco.aftabe2.Util.RandomString;
import ir.treeco.aftabe2.Util.SizeConverter;
import ir.treeco.aftabe2.Util.SizeManager;
import ir.treeco.aftabe2.Util.Tools;
import ir.treeco.aftabe2.View.Activity.MainActivity;
import ir.treeco.aftabe2.View.Custom.ToastMaker;

public class SMSRegisterDialog extends Dialog implements SMSValidationListener, View.OnClickListener {

    Context context;
    Tools tools;

    private final static long CHECK_USER_THRESH_HOLD = 500;
    private long lastTimeChecked = 0;

    private final static int MAX_CODE_RETRY = 3;
    private int retryCount = 0;


    TextView firstTextView;
    TextView secondTextView;
    TextView upperSecondTextView;

    boolean isInPhoneReqState = true;
    boolean isSMSValidationCodeChecked = false;
    boolean isSMSValidated = false;

    EditText mEditText;
    Button mAcceptButton;
    SMSRequestToken mSmsRequestToken = null;
    SMSToken mSMSmsToken = null;
    SMSValidateToken mSmsValidateToken = null;

    String[] phoneReq = {"شماره تلفن", "کد فعال سازی به شما ارسال خواهد شد"};
    String[] codeReq = {"کد فعال سازی", "ممکن است مدتی طول بکشد"};

    MainActivity mActivity;

    ImageManager imageManager;

    public SMSRegisterDialog(Context context, MainActivity mainActivity) {
        super(context);
        this.context = context;
        mActivity = mainActivity;
        imageManager = ImageManager.getInstance(context);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#90000000")));


        setContentView(R.layout.dialog_name_choose);

        firstTextView = (TextView) findViewById(R.id.dialog_username_choose_first_text);
        secondTextView = (TextView) findViewById(R.id.dialog_username_choose_second_text);
        upperSecondTextView = (TextView) findViewById(R.id.dialog_username_choose_upper_second_text);

        customizeTextView(firstTextView, phoneReq[0], 23, FontsHolder.SANS_MEDIUM);
        customizeTextView(secondTextView, phoneReq[1], 18, FontsHolder.SANS_MEDIUM);
        customizeTextView(upperSecondTextView, "توجه", 18, FontsHolder.SANS_BOLD);

        RelativeLayout.LayoutParams firstTextLp = (RelativeLayout.LayoutParams) firstTextView.getLayoutParams();
        firstTextLp.topMargin = (int) (SizeManager.getScreenHeight() * 0.1);


        RelativeLayout.LayoutParams secondTextLp = (RelativeLayout.LayoutParams) upperSecondTextView.getLayoutParams();
        secondTextLp.topMargin = (int) (SizeManager.getScreenHeight() * 0.55);

        mEditText = (EditText) findViewById(R.id.dialog_username_input);
        mEditText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
        mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

        mAcceptButton = (Button) findViewById(R.id.dialog_username_choose_accept_btn);
        mAcceptButton.setText("تایید");
        mAcceptButton.setTypeface(FontsHolder.getSansBold(context));
        mAcceptButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
        mAcceptButton.setOnClickListener(this);

        RelativeLayout.LayoutParams lpAcceptButton = (RelativeLayout.LayoutParams) mAcceptButton.getLayoutParams();
        lpAcceptButton.width = (int) (SizeManager.getScreenWidth() * 0.8);
        lpAcceptButton.height = (int) (SizeManager.getScreenHeight() * 0.1);
        lpAcceptButton.leftMargin = (int) (SizeManager.getScreenWidth() * 0.1);

        RelativeLayout.LayoutParams lpTextInput = (RelativeLayout.LayoutParams) mEditText.getLayoutParams();
        lpTextInput.width = (int) (SizeManager.getScreenWidth() * 0.8);
        lpTextInput.height = (int) (SizeManager.getScreenHeight() * 0.1);
        lpTextInput.topMargin = (int) (SizeManager.getScreenHeight() * 0.35);
        lpTextInput.leftMargin = (int) (SizeManager.getScreenWidth() * 0.1);


        ImageView correctImageView = (ImageView) findViewById(R.id.dialog_sms_register_correct);
        SizeConverter winImageConverter = SizeConverter.SizeConvertorFromWidth(SizeManager.getScreenWidth() * 0.4f, 450, 450);
        correctImageView.setImageBitmap(imageManager.loadImageFromResource(R.drawable.levelwin, winImageConverter.mWidth, winImageConverter.mHeight));


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = SizeManager.getScreenWidth();
        lp.height = SizeManager.getScreenHeight();
        getWindow().setAttributes(lp);


    }

    private void customizeTextView(TextView textView, String label, int sizeDP, int fontType) {
        textView.setText(label);
        textView.setTypeface(FontsHolder.getFont(context, fontType));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, sizeDP);

    }


//    @Override
//    public void onCheckedUsername(boolean status, String username) {
//        if (!status || !mEditText.getText().toString().equals(username))
//            return;
//        Toast.makeText(context, "this name is available ", Toast.LENGTH_SHORT).show();
//        mAcceptButton.setEnabled(true);
//    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.dialog_username_choose_accept_btn) {

            Logger.d("TAG", "on click accept");
            if (isInPhoneReqState) {

                Logger.d("TAG", "is in phone req stsat");

                if (!Tools.isAPhoneNumber(mEditText.getText().toString()))
                    return;

                Logger.d("TAG", "valid numbser");

                String search = mEditText.getText().toString();
                String phoneNumber = (search.length() == 10) ? search : search.substring(1);
                Logger.d("SMSRegisterDialog", phoneNumber + " is requested number to register");
                mSmsRequestToken = new SMSRequestToken(phoneNumber);
                AftabeAPIAdapter.requestSMSActivation(mSmsRequestToken, this);
                isInPhoneReqState = false;


                firstTextView.setText(codeReq[0]);
                secondTextView.setText(codeReq[1]);
                mEditText.setText("");
                return;
            }

            String text = mEditText.getText().toString();

            if (text.length() < 4) {
                return;
            }
            if (mSmsValidateToken == null)
                return;

            if (isSMSValidationCodeChecked)
                return;

            SMSCodeHolder smsCodeHolder = new SMSCodeHolder();
            smsCodeHolder.setCode(text);
            AftabeAPIAdapter.checkSMSActivationCode(mSmsValidateToken, smsCodeHolder, this);
            isSMSValidationCodeChecked = true;

        }
    }


    @Override
    public void onSMSValidateSent(SMSValidateToken smsToken) {
        Logger.d("TAG", "valid sent");


        isInPhoneReqState = false;
        mSmsValidateToken = smsToken;


    }

    @Override
    public void onSMSValidationFail() {
        Logger.d("TAG", "valid fail");
        ToastMaker.show(getContext(), "دوباره تلاش کنید", Toast.LENGTH_SHORT);
        dismiss();


    }

    @Override
    public void onSMSValidationCodeFail() {

        retryCount++;
        if (retryCount != MAX_CODE_RETRY) {
            ToastMaker.show(context, "کد اشتباه است . دوباره تلاش کنید", Toast.LENGTH_SHORT);
            mEditText.setText("");
            isSMSValidationCodeChecked = false;
            return;
        }

        isSMSValidationCodeChecked = false;
        isInPhoneReqState = true;

        firstTextView.setText(phoneReq[0]);
        secondTextView.setText(phoneReq[1]);
        upperSecondTextView.setText("توجه");
        mEditText.setText("");

    }

    @Override
    public void onValidatedCode(final SMSValidateToken smsValidateToken) {
        Logger.d("TAG", "valid code");


        MediaAdapter.getInstance(context).playCorrectSound();

        isSMSValidated = true;


        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                startCorrectAnimation(smsValidateToken);
            }
        });

    }

    public void startCorrectAnimation(final SMSValidateToken smsValidateToken) {

        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        alphaAnimation.setDuration(1000);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {


                dismiss();
                if (!smsValidateToken.isOlduser())
                    new UsernameChooseDialog(getContext(), smsValidateToken, mActivity).show();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        findViewById(R.id.dialog_sms_reg_correct_container).setVisibility(View.VISIBLE);
        findViewById(R.id.dialog_sms_reg_correct_container).startAnimation(alphaAnimation);

        if (smsValidateToken.isOlduser())
            AftabeAPIAdapter.submitSMSActivationCode(smsValidateToken, RandomString.nextString(), mActivity);

    }

}

