package ir.treeco.aftabe2.View.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.pixplicity.easyprefs.library.Prefs;

import ir.treeco.aftabe2.API.Socket.Interfaces.NotifListener;
import ir.treeco.aftabe2.API.Socket.Objects.Notifs.NotifCountHolder;
import ir.treeco.aftabe2.API.Socket.SocketAdapter;
import ir.treeco.aftabe2.API.Rest.Interfaces.UserFoundListener;
import ir.treeco.aftabe2.Adapter.Cache.OnlineOfferAdapter;
import ir.treeco.aftabe2.Adapter.CoinAdapter;
import ir.treeco.aftabe2.MainApplication;
import ir.treeco.aftabe2.Object.User;
import ir.treeco.aftabe2.R;
import ir.treeco.aftabe2.Util.ImageManager;
import ir.treeco.aftabe2.Util.LengthManager;
import ir.treeco.aftabe2.Util.SizeConverter;
import ir.treeco.aftabe2.Util.SizeManager;
import ir.treeco.aftabe2.Util.StoreAdapter;
import ir.treeco.aftabe2.Util.Tools;
import ir.treeco.aftabe2.Util.UiUtil;
import ir.treeco.aftabe2.View.Activity.MainActivity;
import ir.treeco.aftabe2.View.Custom.NotificationCountView;
import ir.treeco.aftabe2.View.Custom.UserLevelView;
import ir.treeco.aftabe2.View.Dialog.DialogAdapter;
import ir.treeco.aftabe2.View.Dialog.RegistrationDialog;

/**
 * Created by al on 12/25/15.
 */
public class OnlinePrimaryPageFragment extends Fragment implements UserFoundListener, View.OnClickListener, NotifListener, CoinAdapter.CoinsChangedListener, StoreAdapter.OnTapsellAvailability {

    private static final String TAG = "OnlinePrimaryPage";
    private static final String TOP_MARGIN_CACHED = TAG + "_CACHE";
    private ImageManager imageManager;
    private LengthManager lengthManager;
    private UserLevelView mUserLevelView;
    //    private NotificationCountView msgCountView;
//    private NotificationCountView frndReqCountView;
    private ImageView specialOffer;
    private CoinAdapter coinAdapter;
    NotificationCountView notifView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {


        final View view = inflater.inflate(R.layout.fragment_online_primary, container, false);

        SocketAdapter.addNotifListener(this);

        coinAdapter = new CoinAdapter(getActivity(), getActivity());

        lengthManager = ((MainApplication) getActivity().getApplication()).getLengthManager();
        imageManager = ((MainApplication) getActivity().getApplication()).getImageManager();
        ImageView startOnlineView = (ImageView) view.findViewById(R.id.multiplay_image_button);
        SizeConverter randplayconverter = SizeConverter.SizeConvertorFromWidth(lengthManager.getScreenWidth() * 0.85f, 1809, 492);
        startOnlineView.setImageBitmap(imageManager.loadImageFromResource(R.drawable.randomplaybutton
                , (int) (randplayconverter.mWidth), (int) (randplayconverter.mHeight), ImageManager.ScalingLogic.FIT));

        startOnlineView.setOnClickListener(this);
        mUserLevelView = (UserLevelView) view.findViewById(R.id.user_view_in_menu);

        int topMargin = (int) (SizeManager.getScreenHeight() * 0.0375 * (SizeManager.getScreenHeight() / (double) (SizeManager.getScreenWidth())));


        ((MainActivity) getActivity()).addUserFoundListener(this);

        LinearLayout notifContainer = (LinearLayout) view.findViewById(R.id.notification_count_container);
//        msgCountView = new NotificationCountView(getContext(), R.drawable.notifmsg);
//        frndReqCountView = new NotificationCountView(getContext(), R.drawable.notifreq);

//        frndReqCountView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((OnlineMenuFragment) getParentFragment()).verticalViewPager.setCurrentItem(1, true);
//            }
//        });

        LengthManager lengthManager = new LengthManager(getContext());

//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int) (SizeManager.getScreenWidth() * 0.01), 5);
//        View view1 = new View(getContext());


        SizeConverter arrowDownConverter = SizeConverter.SizeConvertorFromWidth((int) (SizeManager.getScreenWidth() * 0.14), 208, 190);

        notifView = new NotificationCountView(getContext(), R.drawable.downarrow, arrowDownConverter);
        notifContainer.addView(notifView);

        notifView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OnlineMenuFragment) getParentFragment()).verticalViewPager.setCurrentItem(1, true);

            }
        });


//
//        notifContainer.addView(msgCountView);
//        notifContainer.addView(view1, lp);
//        notifContainer.addView(frndReqCountView);


//        msgCountView.setCount(0);
//        frndReqCountView.setCount(0);

        User myUser = Tools.getCachedUser(getContext());

        if (myUser != null) {

            mUserLevelView.setUser(myUser);

        }

        if (!Tools.isUserRegistered()) {
            mUserLevelView.setUserGuest();

        }


        SizeConverter offerConverter = SizeConverter.SizeConvertorFromWidth(SizeManager.getScreenWidth() * 0.7f, 911, 137);


        int notifsTopMargin = (int) ((int) (SizeManager.getScreenHeight()
                - SizeManager.getScreenHeight() * 0.08 // height of tab bar
                - lengthManager.getHeaderHeight() // header
                - 2 * topMargin - randplayconverter.mHeight // margins and random play
                - (arrowDownConverter.mHeight) // notifs
                - mUserLevelView.getHeightPlusTextView() // userLevelView
                - offerConverter.getHeight()
        ) / 2 + randplayconverter.mHeight + SizeManager.getScreenHeight() * 0.015);


        while (notifsTopMargin + SizeManager.getScreenHeight() * 0.01 < offerConverter.mHeight + randplayconverter.mHeight) {

            if (Prefs.contains(TOP_MARGIN_CACHED)) {
                topMargin = Prefs.getInt(TOP_MARGIN_CACHED, topMargin) + 2;
            }

            topMargin -= 2;

            notifsTopMargin = (int) ((int) (SizeManager.getScreenHeight()
                    - SizeManager.getScreenHeight() * 0.08 // height of tab bar
                    - lengthManager.getHeaderHeight() // header
                    - 2 * topMargin - randplayconverter.mHeight // margins and random play
                    - (arrowDownConverter.mHeight)  // notifs
                    - mUserLevelView.getHeightPlusTextView() // userLevelView
                    - offerConverter.getHeight()
            ) / 2 + randplayconverter.mHeight + SizeManager.getScreenHeight() * 0.015);
        }
        Prefs.putInt(TOP_MARGIN_CACHED, topMargin);

        ((LinearLayout.LayoutParams) mUserLevelView.getLayoutParams()).topMargin = topMargin;
        UiUtil.setTopMargin(view.findViewById(R.id.play_buttons_containers), topMargin);
        ((RelativeLayout.LayoutParams) notifContainer.getLayoutParams()).topMargin = notifsTopMargin;


        specialOffer = (ImageView) view.findViewById(R.id.fragment_online_primary_special_offer);


        if (coinAdapter.getCoinsCount() < 100 && OnlineOfferAdapter.getInstance().isThereOfflineOffer()) {
            specialOffer.setVisibility(View.VISIBLE);
            specialOffer.setImageBitmap(imageManager.loadImageFromResource(R.drawable.randomplayoffer,
                    offerConverter.mWidth, offerConverter.mHeight));
            specialOffer.setOnClickListener(this);
            UiUtil.setTopMargin(specialOffer, (int) (randplayconverter.getHeight() * 0.87));
        }

        coinAdapter.setCoinsChangedListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!OnlineOfferAdapter.getInstance().isThereOfflineOffer()) {
            specialOffer.setVisibility(View.GONE);
        } else {
            specialOffer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onGetUser(User user) {
    }

    @Override
    public void onGetError() {

    }

    @Override
    public void onGetMyUser(User myUser) {
        mUserLevelView.setUser(myUser);
    }

    @Override
    public void onForceLogout() {
        mUserLevelView.setUserGuest();
    }

    private long lastTimeClicked = 0;

    @Override
    public void onClick(View v) {

        if (System.currentTimeMillis() - lastTimeClicked < 500)
            return;
        lastTimeClicked = System.currentTimeMillis();

        if (!Tools.isUserRegistered()) {

            new RegistrationDialog(getContext(), true).show();

            return;

        }

        if (DialogAdapter.makeTutorialDialog(getContext()
                , "این بازی رندم (Random ) هستش. شما با یک نفر به صورت تصادفی وارد یک رقابت آفتابه ای می شوید."
                , "هر رقابت ۲ تا مرحله داره. هرکسی که زودتر و تعداد مراحل بیشتری رو حل کنه  برنده است.هر بازی ۱۰۰ سکه لازم داره که اگه ببرید ۱۸۰ تا پس میگیرید و اگه مساوی شه ۸۰ تا.")) {  // first time showing this
            return;
        }

        if (v.getId() == R.id.multiplay_image_button) {

            if (coinAdapter.getCoinsCount() < 100 && OnlineOfferAdapter.getInstance().isThereOfflineOffer()) {

                StoreAdapter.checkTapsellAvailable(getActivity(), false, this);


                return;
            }

            ((MainActivity) getActivity()).requestRandomGame();
        }

        if (v.getId() == R.id.fragment_online_primary_special_offer) {

            StoreAdapter.checkTapsellAvailable(getActivity(), false, this);

        }
    }

    @Override
    public void onNewNotification(final NotifCountHolder countHolder) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                notifView.setCount(countHolder.getRequests());
            }
        });

    }

    @Override
    public void changed(int newAmount) {

        if (newAmount < 100)
            specialOffer.setVisibility(View.VISIBLE);
        else
            specialOffer.setVisibility(View.GONE);
    }

    @Override
    public void onAvailable(boolean avail) {

        if (!avail)
            ImageManager.getInstance(getContext()).toGrayscale(specialOffer);

    }
}
