package ir.treeco.aftabe.View.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ir.treeco.aftabe.API.UserFoundListener;
import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.FontsHolder;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.SizeManager;
import ir.treeco.aftabe.Util.Tools;
import ir.treeco.aftabe.View.Activity.MainActivity;
import ir.treeco.aftabe.View.Custom.DialogDrawable;
import ir.treeco.aftabe.View.Custom.UserLevelView;
import ir.treeco.aftabe.View.Dialog.LeaderboardDialog;
import ir.treeco.aftabe.View.Dialog.RegistrationDialog;

/**
 * Created by al on 1/22/16.
 */
public class UserInfoFragment extends Fragment implements UserFoundListener {


    public static final String[] titles = new String[]{"رتبه", "تعداد برد", "تعداد باخت"};
    TextView[] lefts;
    TextView[] rights;
    UserLevelView userLevelView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_info, container, false);
        User user = Tools.getCachedUser();
        userLevelView = (UserLevelView) view.findViewById(R.id.fragment_user_info_user_view);
        userLevelView.setClick(false);

        lefts = new TextView[3];
        rights = new TextView[3];

        int[] textRightIds = new int[]{R.id.fragment_user_info_first_left, R.id.fragment_user_info_2nd_left, R.id.fragment_user_info_3rd_left};
        int[] textLeftIds = new int[]{R.id.fragment_user_info_first_right, R.id.fragment_user_info_2nd_right, R.id.fragment_user_info_3rd_right};

        for (int i = 0; i < 3; i++) {
            lefts[i] = (TextView) view.findViewById(textLeftIds[i]);
            rights[i] = (TextView) view.findViewById(textRightIds[i]);
        }

        ((MainActivity) getActivity()).addUserFoundListener(this);
        if (user != null) {
            userLevelView.setUser(user);
            initTextViews(user);

        } else if (user.isGuest()) {
            Button button = (Button) view.findViewById(R.id.fragment_user_info_register_guest);

            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new RegistrationDialog(getContext(), false).show();

                    Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("leaderboard");
                    if (prev != null) {
                        LeaderboardDialog df = (LeaderboardDialog) prev;
                        df.dismiss();
                    }
                }
            });
        }


        return view;
    }

    public void initTextViews(User user) {

        String[] textRights = new String[]{user.getRank() + "", user.getWins() + "", user.getLoses() + ""};

        for (int i = 0; i < 3; i++) {
            TextView left = lefts[i];
            left.setTypeface(FontsHolder.getSansBold(getContext()));
            left.setText(titles[i]);

            TextView right = rights[i];
            right.setTypeface(FontsHolder.getNumeralSansBold(getContext()));
            right.setText(textRights[i]);

        }

    }

    @Override
    public void onGetUser(User user) {

    }

    @Override
    public void onGetError() {

    }

    @Override
    public void onGetMyUser(final User myUser) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                initTextViews(myUser);
                userLevelView.setUser(myUser);
            }
        });

    }

    @Override
    public void onForceLogout() {

    }
}
