package ir.treeco.aftabe.View.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ir.treeco.aftabe.API.Socket.Objects.Friends.MatchResponseHolder;
import ir.treeco.aftabe.API.Socket.SocketAdapter;
import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.SizeManager;
import ir.treeco.aftabe.Util.Tools;
import ir.treeco.aftabe.Util.UiUtil;
import ir.treeco.aftabe.View.Custom.DialogDrawable;
import ir.treeco.aftabe.View.Custom.UserLevelView;


public class MatchRequestDialog extends Dialog implements View.OnClickListener {
    Context context;
    RelativeLayout mDataContainer;
    Tools tools;
    ImageView mMatchButton;
    ImageView mChatButton;
    UserLevelView mUserLevelView;
    User mUser;
    Boolean toSend;

    View.OnClickListener yesClick;

    public MatchRequestDialog(Context context, User user) {
        super(context);
        this.context = context;
        tools = new Tools(context);
        mUser = user;
        toSend = false;

    }

    public MatchRequestDialog(Context context, User user, boolean toSend, View.OnClickListener yesClick) {
        super(context);
        this.context = context;
        tools = new Tools(context);
        mUser = user;
        this.toSend = toSend;
        this.yesClick = yesClick;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_match_request_view);

        mUserLevelView = (UserLevelView) findViewById(R.id.dialog_user_view_mark_view);
        mUserLevelView.setUser(mUser);
        mUserLevelView.setClick(false);

        mDataContainer = (RelativeLayout) findViewById(R.id.user_data_container);
        RelativeLayout.LayoutParams layoutParams = new
                RelativeLayout.LayoutParams((int) (0.8 * SizeManager.getScreenWidth()), ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = (int) (SizeManager.getScreenWidth() * 0.09);
        mDataContainer.setLayoutParams(layoutParams);
        tools.setViewBackground(mDataContainer, new DialogDrawable(getContext()));

        mMatchButton = (ImageView) findViewById(R.id.uv_match_button);
        mChatButton = (ImageView) findViewById(R.id.uv_start_chat_button);
        int size = (int) (SizeManager.getScreenWidth() * 0.1);

        ImageManager imageManager = ((MainApplication) getContext().getApplicationContext()).getImageManager();

        mMatchButton.setImageBitmap(imageManager.loadImageFromResource(R.drawable.yes, size, size));
        mChatButton.setImageBitmap(imageManager.loadImageFromResource(R.drawable.no, size, size));

        int padding = (int) (SizeManager.getScreenWidth() * 0.01);
        int leftMargin = (int) (SizeManager.getScreenWidth() * 0.8 - size * 2 - padding);
        UiUtil.setLeftMargin(mMatchButton, leftMargin / 2);
        UiUtil.setLeftMargin(mChatButton, padding);

        mChatButton.setOnClickListener(this);
        mMatchButton.setOnClickListener(this);

        TextView textView = (TextView) findViewById(R.id.dialog_match_request_text_view);
        if (toSend) {
            String msg = "درخواست بازی" + "\n" + "۱۰۰ سکه";
            textView.setText(msg);
        }

        leftMargin = (int) (SizeManager.getScreenWidth() * 0.8 - UiUtil.getTextViewWidth(textView));
        UiUtil.setLeftMargin(textView, leftMargin / 2);

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.uv_start_chat_button) {
            acceptOrDeclineMatch(false, v);
        }

        if (v.getId() == R.id.uv_match_button) {
            acceptOrDeclineMatch(true, v);
        }

        dismiss();


    }

    public void acceptOrDeclineMatch(boolean accepted, View v) {

        if (!toSend) {
            SocketAdapter.responseToMatchRequest(mUser.getId(), accepted);
            new LoadingDialog(context).show();

        } else {
            if (accepted)
                yesClick.onClick(v);
        }

    }

}

