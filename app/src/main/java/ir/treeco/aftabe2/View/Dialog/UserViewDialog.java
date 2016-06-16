package ir.treeco.aftabe2.View.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import ir.treeco.aftabe2.API.Rest.AftabeAPIAdapter;
import ir.treeco.aftabe2.API.Rest.Interfaces.OnCancelFriendReqListener;
import ir.treeco.aftabe2.API.Rest.Interfaces.OnFriendRequest;
import ir.treeco.aftabe2.API.Socket.SocketAdapter;
import ir.treeco.aftabe2.Adapter.Cache.FriendRequestState;
import ir.treeco.aftabe2.Adapter.FriendsAdapter;
import ir.treeco.aftabe2.MainApplication;
import ir.treeco.aftabe2.Object.User;
import ir.treeco.aftabe2.R;
import ir.treeco.aftabe2.Util.FontsHolder;
import ir.treeco.aftabe2.Util.ImageManager;
import ir.treeco.aftabe2.Util.SizeConverter;
import ir.treeco.aftabe2.Util.SizeManager;
import ir.treeco.aftabe2.Util.Tools;
import ir.treeco.aftabe2.Util.UiUtil;
import ir.treeco.aftabe2.View.Activity.MainActivity;
import ir.treeco.aftabe2.View.Custom.DialogDrawable;
import ir.treeco.aftabe2.View.Custom.ToastMaker;
import ir.treeco.aftabe2.View.Custom.UserLevelView;

public class UserViewDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = "UserViewDialog";
    Context context;
    RelativeLayout mDataContainer;
    Tools tools;
    ImageView mMatchButton;
    ImageView mChatButton;
    UserLevelView mUserLevelView;
    User mUser;
    ImageView mCancelImageView;
    ImageManager imageManager;
    public static final String[] titles = new String[]{"رتبه", "برد/باخت", "تعداد دوستان"};
    User myUser;


    public UserViewDialog(Context context, User user) {
        super(context);
        this.context = context;
        tools = new Tools(context);
        mUser = user;

        if (context instanceof MainActivity)
            myUser = ((MainActivity) context).getMyUser();
        if (myUser == null)
            myUser = Tools.getCachedUser(context);
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

        int containersHeight = (int) (SizeManager.getScreenHeight() * 0.50);
        mDataContainer = (RelativeLayout) findViewById(R.id.user_data_container);
        RelativeLayout.LayoutParams layoutParams = new
                RelativeLayout.LayoutParams((int) (0.8 * SizeManager.getScreenWidth()), containersHeight);
        layoutParams.topMargin = +(int) (mUserLevelView.getRealHeight() / 2);
        layoutParams.leftMargin = (int) (SizeManager.getScreenWidth() * 0.1);


        mDataContainer.setLayoutParams(layoutParams);
        tools.setViewBackground(mDataContainer, new DialogDrawable(getContext()));

        View textContainers = findViewById(R.id.dialog_user_view_text_containers);
        UiUtil.setTopMargin(textContainers, (int) (layoutParams.topMargin
                + UiUtil.getTextViewHeight(mUserLevelView.getUserNameTextView())
                + SizeManager.getScreenHeight() * 0.01));

        mMatchButton = (ImageView) findViewById(R.id.uv_match_button);
        mChatButton = (ImageView) findViewById(R.id.uv_start_chat_button);


        int size = (int) (SizeManager.getScreenWidth() * 0.135);


        SizeConverter converter = SizeConverter.SizeConvertorFromWidth((float) (SizeManager.getScreenWidth() * 0.2), 474, 192);
        mCancelImageView = (ImageView) findViewById(R.id.uv_cancel_friendship);

        imageManager = ((MainApplication) getContext().getApplicationContext()).getImageManager();

        mCancelImageView.setImageBitmap(imageManager.loadImageFromResource(R.drawable.deletefriend, converter.mWidth, converter.mHeight));

        if (mUser.isFriend()) {

            mCancelImageView.setVisibility(View.VISIBLE);
            mCancelImageView.setOnClickListener(this);

            mMatchButton.setImageBitmap(imageManager.loadImageFromResource(
                    R.drawable.challengebutton, size, size));
            mChatButton.setImageBitmap(imageManager.loadImageFromResource(
                    R.drawable.notifmsg, size, size));

        } else {
            mMatchButton.setVisibility(View.GONE);
            int friendReqDrawable = (FriendRequestState.getInstance().requestShallPASS(mUser) && !mUser.isGuest()) ? R.drawable.addfriends : R.drawable.notifreq;
            mChatButton.setImageBitmap(imageManager.loadImageFromResource(
                    friendReqDrawable, size, size, ImageManager.ScalingLogic.FIT));
        }

        mChatButton.setOnClickListener(this);
        mMatchButton.setOnClickListener(this);


        int[] textRightIds = new int[]{R.id.dialog_user_view_first_left, R.id.dialog_user_view_2nd_left, R.id.dialog_user_view_3rd_left};
        int[] textLeftIds = new int[]{R.id.dialog_user_view_first_right, R.id.dialog_user_view_2nd_right, R.id.dialog_user_view_3rd_right};
        int[] parentIds = new int[]{R.id.dialog_user_view_parent_1, R.id.dialog_user_view_parent_2, R.id.dialog_user_view_parent_3};

        String[] textRights = new String[]{mUser.getRank() + "", mUser.getLoses() + "/" + mUser.getWins(), mUser.getFriendCount() + ""};
        Integer leftMargin = null;

        for (int i = 0; i < 3; i++) {
            textRights[i] = Tools.numeralStringToPersianDigits(textRights[i]);
        }


        for (int i = 0; i < 3; i++) {
            TextView left = (TextView) findViewById(textLeftIds[i]);
            left.setTypeface(FontsHolder.getSansBold(context));
            left.setText(titles[i]);
            int margin = (int) (SizeManager.getScreenWidth() * 0.2);
            left.setTextColor(context.getResources().getColor(R.color.text_default_color));

            if (i == 1) {

                Spannable wordtoSpan = new SpannableString(titles[i]);
                wordtoSpan.setSpan(new ForegroundColorSpan(Color.RED), 4, titles[1].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                left.setText(wordtoSpan);

            }

            TextView right = (TextView) findViewById(textRightIds[i]);
            right.setTypeface(FontsHolder.getNumeralSansBold(context));
            right.setText(textRights[i]);


            right.setTextColor(context.getResources().getColor(R.color.text_default_color));

            if (i == 1) {
                Spannable wordtoSpan = new SpannableString(textRights[i]);
                wordtoSpan.setSpan(new ForegroundColorSpan(Color.RED), 0, (mUser.getLoses() + "").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                right.setText(wordtoSpan);


            }

            UiUtil.setLeftMargin(right, margin);

            UiUtil.setTopMargin(findViewById(parentIds[i]), (int) (SizeManager.getScreenHeight() * 0.02));
            UiUtil.setWidth(findViewById(parentIds[i]), (int) (SizeManager.getScreenWidth() * 0.8));

            UiUtil.setTextViewSize(right, (int) (SizeManager.getScreenHeight() * 0.1), 0.26f);
            UiUtil.setTextViewSize(left, (int) (SizeManager.getScreenHeight() * 0.1), 0.26f);
            UiUtil.setRightMargin(left, margin);


        }

        int topMargin = // margin top container
                (int) (+containersHeight + layoutParams.topMargin - size * (0.7));
        UiUtil.setTopMargin(mChatButton, topMargin);
        UiUtil.setTopMargin(mMatchButton, topMargin);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        lp.width = SizeManager.getScreenWidth();
        getWindow().setAttributes(lp);
    }


    @Override
    public void onClick(View v) {

        if (myUser.isGuest()) {
            dismiss();
            new RegistrationDialog(context, false).show();

            return;
        }

        if (v.getId() == R.id.uv_cancel_friendship) {
            DialogAdapter.makeFriendRemoveDialog(context, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AftabeAPIAdapter.removeFriend(myUser, mUser.getId(), new OnCancelFriendReqListener() {
                        @Override
                        public void onFail() {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastMaker.show(context, context.getResources().getString(R.string.try_later), Toast.LENGTH_SHORT);

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
                                            R.drawable.addfriends, size, size, ImageManager.ScalingLogic.FIT));
                                    mMatchButton.setVisibility(View.GONE);
                                    mCancelImageView.setVisibility(View.GONE);

                                    if (context instanceof MainActivity) {
                                        ((MainActivity) context).mFriendsAdapter.removeUser(mUser, FriendsAdapter.TYPE_FRIEND);
                                        ((MainActivity) context).mFriendsAdapter.removeUser(mUser, FriendsAdapter.TYPE_ONLINE_FRIENDS);
                                    }
                                }
                            });

                        }
                    });
                }
            });

        }

        if (v.getId() == R.id.uv_start_chat_button) {
            if (!mUser.isFriend()) {
                if (FriendRequestState.getInstance().requestShallPASS(mUser) && !mUser.isGuest())
                    DialogAdapter.makeFriendRequestDialog(context, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AftabeAPIAdapter.requestFriend(myUser, mUser.getId(), new OnFriendRequest() {
                                @Override
                                public void onFriendRequestSent() {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            int size = (int) (SizeManager.getScreenWidth() * 0.135);

                                            mChatButton.setImageBitmap(imageManager.loadImageFromResource(
                                                    R.drawable.notifreq, size, size));


                                            if (mUser.getAccess().isFRfrom())
                                                ((MainActivity) context).mFriendsAdapter.addUser(mUser, FriendsAdapter.TYPE_FRIEND);

                                        }
                                    });

                                }

                                @Override
                                public void onFriendRequestFailedToSend() {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {

                                            ToastMaker.show(context, context.getResources().getString(R.string.try_later), Toast.LENGTH_SHORT);

                                        }
                                    });
                                }
                            });

                        }
                    });
            } else {
                ToastMaker.show(context, context.getResources().getString(R.string.chat_not_available), Toast.LENGTH_SHORT);

            }

        }

        if (v.getId() == R.id.uv_match_button) {
            if (mUser.isFriend())
                requestMatch();
            else {
            }
        }


    }

    public void requestMatch() {

            DialogAdapter.makeMatchRequestDialog(context, mUser, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!((MainActivity) context).getCoinAdapter().spendCoinDiffless(100))
                        return;

                    SocketAdapter.requestToAFriend(mUser.getId());

                    new LoadingForMatchRequestResult(context, mUser).show();
                }
            });


    }

}

