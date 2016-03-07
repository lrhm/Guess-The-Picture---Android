package ir.treeco.aftabe.View.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import ir.treeco.aftabe.API.AftabeAPIAdapter;
import ir.treeco.aftabe.API.SMSValidationListener;
import ir.treeco.aftabe.API.UserFoundListener;
import ir.treeco.aftabe.API.UsernameCheckListener;
import ir.treeco.aftabe.API.Utils.GoogleToken;
import ir.treeco.aftabe.API.Utils.SMSRequestToken;
import ir.treeco.aftabe.API.Utils.SMSToken;
import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.FontsHolder;
import ir.treeco.aftabe.Util.SizeManager;
import ir.treeco.aftabe.Util.Tools;
import ir.treeco.aftabe.View.Activity.MainActivity;

public class SMSRegisterDialog extends Dialog implements SMSValidationListener, View.OnClickListener, UserFoundListener {

    Context context;
    Tools tools;

    private final static long CHECK_USER_THRESH_HOLD = 500;
    private long lastTimeChecked = 0;


    TextView firstTextView;
    TextView secondTextView;
    TextView upperSecondTextView;

    boolean isInPhoneReqState = true;

    EditText mEditText;
    Button mAcceptButton;
    SMSRequestToken mSmsRequestToken = null;
    SMSToken mSMSmsToken = null;

    String[] phoneReq = {"شماره تلفن", "کد فعال سازی به شما ارسال خواهد شد"};
    String[] codeReq = {"کد فعال سازی", "ممکن است مدتی طول بکشد"};


    public SMSRegisterDialog(Context context) {
        super(context);
        this.context = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        setContentView(R.layout.dialog_name_choose);

        firstTextView = (TextView) findViewById(R.id.dialog_username_choose_first_text);
        secondTextView = (TextView) findViewById(R.id.dialog_username_choose_second_text);
        upperSecondTextView = (TextView) findViewById(R.id.dialog_username_choose_upper_second_text);

        customizeTextView(firstTextView, phoneReq[0], 25, FontsHolder.SANS_MEDIUM);
        customizeTextView(secondTextView, phoneReq[1], 20, FontsHolder.SANS_REGULAR);
        customizeTextView(upperSecondTextView, "توجه", 20, FontsHolder.SANS_REGULAR);

        RelativeLayout.LayoutParams firstTextLp = (RelativeLayout.LayoutParams) firstTextView.getLayoutParams();
        firstTextLp.topMargin = (int) (SizeManager.getScreenHeight() * 0.1);


        RelativeLayout.LayoutParams secondTextLp = (RelativeLayout.LayoutParams) upperSecondTextView.getLayoutParams();
        secondTextLp.topMargin = (int) (SizeManager.getScreenHeight() * 0.55);

        mEditText = (EditText) findViewById(R.id.dialog_username_input);
        mEditText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
        mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

        mAcceptButton = (Button) findViewById(R.id.dialog_username_choose_accept_btn);
        mAcceptButton.setText("تایید");
        mAcceptButton.setTypeface(FontsHolder.getSansRegular(context));
        mAcceptButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
        mAcceptButton.setOnClickListener(this);
        mAcceptButton.setEnabled(false);

        RelativeLayout.LayoutParams lpAcceptButton = (RelativeLayout.LayoutParams) mAcceptButton.getLayoutParams();
        lpAcceptButton.width = (int) (SizeManager.getScreenWidth() * 0.8);
        lpAcceptButton.height = (int) (SizeManager.getScreenHeight() * 0.1);
        lpAcceptButton.leftMargin = (int) (SizeManager.getScreenWidth() * 0.05);

        RelativeLayout.LayoutParams lpTextInput = (RelativeLayout.LayoutParams) mEditText.getLayoutParams();
        lpTextInput.width = (int) (SizeManager.getScreenWidth() * 0.8);
        lpTextInput.height = (int) (SizeManager.getScreenHeight() * 0.1);
        lpTextInput.topMargin = (int) (SizeManager.getScreenHeight() * 0.35);
        lpTextInput.leftMargin = (int) (SizeManager.getScreenWidth() * 0.05);


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

            if (isInPhoneReqState) {
                if (!Tools.isAPhoneNumber(mEditText.getText().toString()))
                    return;
                String search = mEditText.getText().toString();
                String phoneNumber = (search.length() == 10) ? search : search.substring(1);
                Log.d("SMSRegisterDialog", phoneNumber + " is requested number to register");
                mSmsRequestToken = new SMSRequestToken(phoneNumber);
                AftabeAPIAdapter.requestSMSActivation(mSmsRequestToken, this);
                isInPhoneReqState = false;


                firstTextView.setText(codeReq[0]);
                secondTextView.setText(codeReq[1]);
                mEditText.setText("");
                return;
            }

            String text = mEditText.getText().toString();

            if (text.length() != 4) {
                return;
            }
            if (mSMSmsToken == null)
                return;

//            if(!text.equals(mSMSmsToken.code)) {
//                Toast.makeText(context, "code is wrong ", Toast.LENGTH_SHORT);
//                return;
//            }

//            AftabeAPIAdapter.submitSMSActivation(mSMSmsToken, text, this);

        }
    }

    @Override
    public void onSMSValidateSent(SMSToken smsToken) {
        this.mSMSmsToken = smsToken;
        isInPhoneReqState = false;
    }

    @Override
    public void onSMSValidationFail() {

    }

    @Override
    public void onGetUser(User user) {
        ((MainActivity)getOwnerActivity()).onGetUser(user);

    }

    @Override
    public void onGetError() {

    }
}

