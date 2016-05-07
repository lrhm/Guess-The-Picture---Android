package ir.treeco.aftabe.View.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import ir.treeco.aftabe.API.AftabeAPIAdapter;
import ir.treeco.aftabe.API.OnCancelFriendReqListener;
import ir.treeco.aftabe.API.OnFriendRequest;
import ir.treeco.aftabe.API.Socket.SocketAdapter;
import ir.treeco.aftabe.Adapter.FriendsAdapter;
import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.SizeManager;
import ir.treeco.aftabe.Util.Tools;
import ir.treeco.aftabe.View.Activity.MainActivity;
import ir.treeco.aftabe.View.Custom.DialogDrawable;
import ir.treeco.aftabe.View.Custom.UserLevelView;

public class UserViewDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = "UserViewDialog";
    Context context;
    RelativeLayout mDataContainer;
    Tools tools;
    ImageView mMatchButton;
    ImageView mChatButton;
    UserLevelView mUserLevelView;
    User mUser;
    TextView mCancelTextView;
    ImageManager imageManager;


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

        mCancelTextView = (TextView) findViewById(R.id.uv_cancel_friendship);
        mCancelTextView.setText("unfriend");

        imageManager = ((MainApplication) getContext().getApplicationContext()).getImageManager();

        if (mUser.isFriend()) {

            mCancelTextView.setVisibility(View.VISIBLE);
            mCancelTextView.setOnClickListener(this);

            mMatchButton.setImageBitmap(imageManager.loadImageFromResource(
                    R.drawable.challengebutton, size, size));
            mChatButton.setImageBitmap(imageManager.loadImageFromResource(
                    R.drawable.chatbutton, size, size));

        } else {
            mMatchButton.setVisibility(View.GONE);
            mChatButton.setImageBitmap(imageManager.loadImageFromResource(
                    R.drawable.addfriends, size, size));
        }

        mChatButton.setOnClickListener(this);
        mMatchButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.uv_cancel_friendship) {
            AftabeAPIAdapter.removeFriend(Tools.getCachedUser(), mUser.getId(), new OnCancelFriendReqListener() {
                @Override
                public void onFail() {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "nashod", Toast.LENGTH_SHORT).show();

                        }
                    });
                }

                @Override
                public void onSuccess() {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            int size = (int) (SizeManager.getScreenWidth() * 0.1);
                            mChatButton.setImageBitmap(imageManager.loadImageFromResource(
                                    R.drawable.addfriends, size, size));
                            mMatchButton.setVisibility(View.GONE);
                            mCancelTextView.setVisibility(View.GONE);

                            if (context instanceof MainActivity) {
                                ((MainActivity) context).mFriendsAdapter.removeUser(mUser, FriendsAdapter.TYPE_FRIEND);

                                ((MainActivity) context).mFriendsAdapter.removeUser(mUser, FriendsAdapter.TYPE_ONLINE_FRIENDS);
                            }
                        }
                    });

                }
            });

        }

        if (v.getId() == R.id.uv_start_chat_button) {
            AftabeAPIAdapter.requestFriend(Tools.getCachedUser(), mUser.getId(), new OnFriendRequest() {
                @Override
                public void onFriendRequestSent() {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(getContext(), "friend request sent", Toast.LENGTH_SHORT).show();

                        }
                    });

                }

                @Override
                public void onFriendRequestFailedToSend() {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(getContext(), "friend request failed to send", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            });

        }

        if (v.getId() == R.id.uv_match_button) {
            if (mUser.isFriend())
                requestMatch();
            else {
            }
        }


    }

    public void requestMatch() {

        SocketAdapter.requestToAFriend(mUser.getId());

        new LoadingForMatchRequestResult(context, mUser).show();

    }

}

