package ir.treeco.aftabe.View.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.SizeManager;
import ir.treeco.aftabe.Util.Tools;
import ir.treeco.aftabe.View.Custom.DialogDrawable;
import ir.treeco.aftabe.View.Custom.UserLevelView;

public class UserViewDialog extends Dialog  {
    Context context;
    RelativeLayout mDataContainer;
    Tools tools;
    ImageView mMatchButton;
    ImageView mChatButton;
    UserLevelView mUserLevelView;
    User mUser;

    public UserViewDialog(Context context, User user) {
        super(context);
        this.context = context;
        tools = new Tools(context);
        mUser = user;

    }



    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_user_view);

        mUserLevelView = (UserLevelView) findViewById(R.id.dialog_user_view_mark_view);
        mUserLevelView.setUser(mUser);

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

        mMatchButton.setImageBitmap(imageManager.loadImageFromResource(R.drawable.challengebutton, size, size));
        mChatButton.setImageBitmap(imageManager.loadImageFromResource(R.drawable.chatbutton, size, size));


    }

}

