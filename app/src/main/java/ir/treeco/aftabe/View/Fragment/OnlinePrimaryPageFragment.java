package ir.treeco.aftabe.View.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import com.pixplicity.easyprefs.library.Prefs;

import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.LengthManager;
import ir.treeco.aftabe.Util.SizeConverter;
import ir.treeco.aftabe.View.Custom.UserLevelMarkView;
import ir.treeco.aftabe.View.Custom.VerticalViewPager;
import ir.treeco.aftabe.View.Dialog.RegistrationDialog;

/**
 * Created by al on 12/25/15.
 */
public class OnlinePrimaryPageFragment extends Fragment {

    private ImageManager imageManager;
    private LengthManager lengthManager;
    private UserLevelMarkView mUserLevelMarkView;
    private final String IS_USER_REGISTRATED = "isUserRegistratedKey";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_online_primary, container, false);
        lengthManager = ((MainApplication) getActivity().getApplication()).getLengthManager();
        imageManager = ((MainApplication) getActivity().getApplication()).getImageManager();
        ImageView startOnlineView = (ImageView)view.findViewById(R.id.multiplay_image_button);
        SizeConverter randplayconverter = SizeConverter.SizeConvertorFromWidth(lengthManager.getScreenWidth() * 0.85f ,1809 , 492 );
        startOnlineView.setImageBitmap(imageManager.loadImageFromResource(R.drawable.randomplaybutton
                ,(int) (randplayconverter.mWidth ), (int)(randplayconverter.mHeight) , ImageManager.ScalingLogic.FIT ));
        mUserLevelMarkView = (UserLevelMarkView) view.findViewById(R.id.user_view_in_menu);

        if(!Prefs.getBoolean(IS_USER_REGISTRATED  , false)){ //user not registrated
            mUserLevelMarkView.setUserGuest();
            mUserLevelMarkView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new RegistrationDialog(getContext()).show();
                    Log.d("TAGH", "TAHG");
                }
            });
        }


        return view;
    }

}
