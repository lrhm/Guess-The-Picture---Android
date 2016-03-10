package ir.treeco.aftabe.View.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.SizeManager;
import ir.treeco.aftabe.Util.Tools;
import ir.treeco.aftabe.View.Activity.MainActivity;
import ir.treeco.aftabe.View.Custom.DialogDrawable;
import ir.treeco.aftabe.View.Custom.UserLevelView;

/**
 * Created by al on 1/22/16.
 */
public class UserInfoFragment extends Fragment {

    RelativeLayout mDataContainer;
    Tools tools;
    ImageView mMatchButton;
    ImageView mChatButton;


    public UserInfoFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_user_info, container, false);
        User user = Tools.getCachedUser();

//        TODO add listener to main activities
        if (user != null) {
            UserLevelView userLevelView = (UserLevelView) view.findViewById(R.id.fragment_user_info_user_view);
            userLevelView.setUser(user);
            userLevelView.setClick(false);
        }
//
//
//        int size = (int) (SizeManager.getScreenWidth() * 0.1);
//
//        ImageManager imageManager = ((MainApplication) getContext().getApplicationContext()).getImageManager();


        return view;
    }
}
