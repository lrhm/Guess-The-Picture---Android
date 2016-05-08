package ir.treeco.aftabe.View.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.pixplicity.easyprefs.library.Prefs;

import ir.treeco.aftabe.API.UserFoundListener;
import ir.treeco.aftabe.Adapter.CoinAdapter;
import ir.treeco.aftabe.Adapter.DBAdapter;
import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.LengthManager;
import ir.treeco.aftabe.Util.SizeConverter;
import ir.treeco.aftabe.Util.SizeManager;
import ir.treeco.aftabe.Util.Tools;
import ir.treeco.aftabe.View.Activity.MainActivity;
import ir.treeco.aftabe.View.Custom.NotificationCountView;
import ir.treeco.aftabe.View.Custom.UserLevelView;
import ir.treeco.aftabe.View.Custom.VerticalViewPager;
import ir.treeco.aftabe.View.Dialog.RegistrationDialog;

/**
 * Created by al on 12/25/15.
 */
public class OnlinePrimaryPageFragment extends Fragment implements UserFoundListener, View.OnClickListener {

    private ImageManager imageManager;
    private LengthManager lengthManager;
    private UserLevelView mUserLevelView;
    private NotificationCountView msgCountView;
    private NotificationCountView frndReqCountView;
    private CoinAdapter coinAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_online_primary, container, false);

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

        ((LinearLayout.LayoutParams) mUserLevelView.getLayoutParams()).topMargin = topMargin;
        ((LinearLayout.LayoutParams) startOnlineView.getLayoutParams()).topMargin = topMargin;


        ((MainActivity) getActivity()).addUserFoundListener(this);

        LinearLayout notifContainer = (LinearLayout) view.findViewById(R.id.notification_count_container);
        msgCountView = new NotificationCountView(getContext(), R.drawable.notifmsg);
        frndReqCountView = new NotificationCountView(getContext(), R.drawable.notifreq);

        frndReqCountView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OnlineMenuFragment) getParentFragment()).verticalViewPager.setCurrentItem(1, true);
            }
        });

        LengthManager lengthManager = new LengthManager(getContext());

        topMargin = (int) (SizeManager.getScreenHeight()
                - SizeManager.getScreenHeight() * 0.12 // height of tab bar
                - lengthManager.getHeaderHeight() // header
                - 2 * topMargin - randplayconverter.mHeight // margins and random play
               - (int) (SizeManager.getScreenWidth() * 0.14) // notifs
                - mUserLevelView.getHeightPlusTextView() // userLevelView
                    ) / 2;

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int) (SizeManager.getScreenWidth() * 0.01), 5);
        View view1 = new View(getContext());

        ((LinearLayout.LayoutParams) notifContainer.getLayoutParams()).topMargin = topMargin;


        notifContainer.addView(msgCountView);
        notifContainer.addView(view1, lp);
        notifContainer.addView(frndReqCountView);


        msgCountView.setCount(0);
        frndReqCountView.setCount(0);


        if (Prefs.contains(Tools.USER_SAVED_DATA)) {
            String jsonString = Prefs.getString(Tools.USER_SAVED_DATA, "");
            Gson gson = new Gson();
            User myUser = gson.fromJson(jsonString, User.class);
            mUserLevelView.setUser(myUser);

        }

        if (!Tools.isUserRegistered()) {
            mUserLevelView.setUserGuest();

        }


        return view;
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

        if (v.getId() == R.id.multiplay_image_button) {
            if (coinAdapter.spendCoins(100))
                ((MainActivity) getActivity()).requestRandomGame();
        }
    }
}
