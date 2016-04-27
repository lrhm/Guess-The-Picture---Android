package ir.treeco.aftabe.View.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import ir.treeco.aftabe.API.AftabeAPIAdapter;
import ir.treeco.aftabe.API.UsernameCheckListener;
import ir.treeco.aftabe.API.Utils.GoogleToken;
import ir.treeco.aftabe.API.Utils.SMSToken;
import ir.treeco.aftabe.API.Utils.SMSValidateToken;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.FontsHolder;
import ir.treeco.aftabe.Util.SizeManager;
import ir.treeco.aftabe.Util.Tools;
import ir.treeco.aftabe.View.Activity.MainActivity;

public class UsernameChooseDialog extends Dialog implements TextWatcher, UsernameCheckListener, View.OnClickListener {

    Context context;
    Tools tools;
    private final static long CHECK_USER_THRESH_HOLD = 500;
    private long lastTimeChecked = 0;
    GoogleToken googleToken = null;
    SMSValidateToken smsToken = null;
    EditText mEditText;
    Button mAcceptButton;
    ImageView mStatusImageView;
    ProgressBar mProgressBar;
    MainActivity mActivity;

    public UsernameChooseDialog(Context context, GoogleToken googleToken, MainActivity mainActivity) {
        super(context);
        this.context = context;
        this.googleToken = googleToken;
        mActivity = mainActivity;
    }

    public UsernameChooseDialog(Context context, SMSValidateToken smsToken, MainActivity mainActivity) {
        super(context);
        this.context = context;
        this.smsToken = smsToken;
        mActivity = mainActivity;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#90000000")));


        setContentView(R.layout.dialog_name_choose);

        TextView firstTextView = (TextView) findViewById(R.id.dialog_username_choose_first_text);
        TextView secondTextView = (TextView) findViewById(R.id.dialog_username_choose_second_text);
        TextView upperSecondTextView = (TextView) findViewById(R.id.dialog_username_choose_upper_second_text);

        mProgressBar = (ProgressBar) findViewById(R.id.dialog_username_choose_progress_bar);
        mStatusImageView = (ImageView) findViewById(R.id.dialog_username_choose_image_view);

        customizeTextView(firstTextView, "یه اسم انتخواب کن", 23, FontsHolder.SANS_MEDIUM);
        customizeTextView(secondTextView, "دیگر قابل تغیر نیست" + "\n" + "حد اقل ۶ حرف", 18, FontsHolder.SANS_MEDIUM);
        customizeTextView(upperSecondTextView, "توجه", 18, FontsHolder.SANS_BOLD);

        RelativeLayout.LayoutParams firstTextLp = (RelativeLayout.LayoutParams) firstTextView.getLayoutParams();
        firstTextLp.topMargin = (int) (SizeManager.getScreenHeight() * 0.1);


        RelativeLayout.LayoutParams secondTextLp = (RelativeLayout.LayoutParams) upperSecondTextView.getLayoutParams();
        secondTextLp.topMargin = (int) (SizeManager.getScreenHeight() * 0.55);

        mEditText = (EditText) findViewById(R.id.dialog_username_input);
        mEditText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 23);
        mEditText.addTextChangedListener(this);

        mAcceptButton = (Button) findViewById(R.id.dialog_username_choose_accept_btn);
        mAcceptButton.setText("تایید");
        mAcceptButton.setTypeface(FontsHolder.getSansBold(context));
        mAcceptButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 23);
        mAcceptButton.setOnClickListener(this);
        mAcceptButton.setEnabled(false);
        mAcceptButton.setTextColor(Color.BLACK);

        RelativeLayout.LayoutParams lpAcceptButton = (RelativeLayout.LayoutParams) mAcceptButton.getLayoutParams();
        lpAcceptButton.width = (int) (SizeManager.getScreenWidth() * 0.8);
        lpAcceptButton.height = (int) (SizeManager.getScreenHeight() * 0.1);
        lpAcceptButton.leftMargin = (int) (SizeManager.getScreenWidth() * 0.1);

        RelativeLayout.LayoutParams lpTextInput = (RelativeLayout.LayoutParams) mEditText.getLayoutParams();
        lpTextInput.width = (int) (SizeManager.getScreenWidth() * 0.8);
        lpTextInput.height = (int) (SizeManager.getScreenHeight() * 0.1);
        lpTextInput.topMargin = (int) (SizeManager.getScreenHeight() * 0.35);
        lpTextInput.leftMargin = (int) (SizeManager.getScreenWidth() * 0.1);

        RelativeLayout.LayoutParams lpProgressBar = (RelativeLayout.LayoutParams) mProgressBar.getLayoutParams();
        lpProgressBar.topMargin = (int) (SizeManager.getScreenHeight() * 0.38);
        lpProgressBar.leftMargin = (int) (SizeManager.getScreenWidth() * 0.75);

        RelativeLayout.LayoutParams lpImageView = (RelativeLayout.LayoutParams) mStatusImageView.getLayoutParams();
        lpImageView.topMargin = (int) (SizeManager.getScreenHeight() * 0.38);
        lpImageView.leftMargin = (int) (SizeManager.getScreenWidth() * 0.75);



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


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.toString().contains(" ")) {
            mEditText.setText(s.toString().replace(" ", ""));
            mEditText.setSelection(s.toString().replace(" ", "").length());
            return;
        }
        if (s.length() < 6 || Tools.isAEmail(s.toString()) || Tools.isAPhoneNumber(s.toString())) {
            mStatusImageView.setVisibility(View.VISIBLE);
            mStatusImageView.setImageResource(R.drawable.ic_error_outline_black_24dp);
            mProgressBar.setVisibility(View.GONE);
            return;
        }

        mProgressBar.setVisibility(View.VISIBLE);
        mStatusImageView.setVisibility(View.GONE);
        if (System.currentTimeMillis() - lastTimeChecked > CHECK_USER_THRESH_HOLD) {
            lastTimeChecked = System.currentTimeMillis();
            AftabeAPIAdapter.checkUsername(s.toString(), this);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onCheckedUsername(boolean status, String username) {

        mProgressBar.setVisibility(View.GONE);
        mStatusImageView.setVisibility(View.VISIBLE);
        if (!status || !mEditText.getText().toString().equals(username)) {
            mStatusImageView.setImageResource(R.drawable.ic_error_outline_black_24dp);
            return;
        }
        Toast.makeText(context, "this name is available ", Toast.LENGTH_SHORT).show();
        mStatusImageView.setImageResource(R.drawable.ic_check_circle_black_24dp);
        mAcceptButton.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.dialog_username_choose_accept_btn) {
            AftabeAPIAdapter.checkUsername(mEditText.getText().toString(), new UsernameCheckListener() {
                @Override
                public void onCheckedUsername(boolean status, String name) {
                    if (status) {
                        if (googleToken != null) {
                            googleToken.setUsername(name);

                            AftabeAPIAdapter.getMyUserByGoogle(googleToken, mActivity);
                        } else if (smsToken != null) {

                            Log.d("TAG", "subimt sms activation code in dialog calling ");
                            AftabeAPIAdapter.submitSMSActivationCode(smsToken, name, mActivity);
                        }
                        dismiss();
                    }
                }
            });
        }
    }
}

