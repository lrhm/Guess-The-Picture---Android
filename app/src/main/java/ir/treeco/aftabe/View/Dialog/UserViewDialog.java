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
import android.widget.Toast;

import ir.treeco.aftabe.API.AftabeAPIAdapter;
import ir.treeco.aftabe.API.Socket.SocketAdapter;
import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.SizeManager;
import ir.treeco.aftabe.Util.Tools;
import ir.treeco.aftabe.View.Custom.DialogDrawable;
import ir.treeco.aftabe.View.Custom.UserLevelView;

public class UserViewDialog extends Dialog implements View.OnClickListener {
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog_user_view);

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

        mMatchButton.setImageBitmap(imageManager.loadImageFromResource(
                !mUser.isFriend() ? R.drawable.ic_check_circle_black_24dp : R.drawable.challengebutton, size, size));
        mChatButton.setImageBitmap(imageManager.loadImageFromResource(
                !mUser.isFriend() ? R.drawable.ic_error_outline_black_24dp : R.drawable.chatbutton, size, size));


        mChatButton.setOnClickListener(this);
        mMatchButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.uv_start_chat_button) {
//            if (mUser.isFriend())
        }

        if (v.getId() == R.id.uv_match_button) {
            if (mUser.isFriend())
                requestMatch();
            else {
                AftabeAPIAdapter.requestFriend(Tools.getCachedUser(), mUser.getId(), null);
                Toast.makeText(getContext(), "friend request sent", Toast.LENGTH_SHORT).show();
            }
        }


    }

    public void requestMatch() {

        SocketAdapter.requestToAFriend(mUser.getId());

        new LoadingForMatchRequestResult(context, mUser).show();

    }

}

