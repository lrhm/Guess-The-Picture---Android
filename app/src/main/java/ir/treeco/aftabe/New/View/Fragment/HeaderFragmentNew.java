package ir.treeco.aftabe.New.View.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import ir.treeco.aftabe.R;

public class HeaderFragmentNew extends Fragment implements View.OnClickListener {
//    SharedPreferences preferences;
    View v;
    Activity activity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        preferences = activity.getSharedPreferences(Utils.SHARED_PREFRENCES_TAG, Context.MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.new_fragment_header, container, false);
//        LengthManager.initialize(activity.getBaseContext());
        // setup header
//        setUpHeader();
        // setup coinBox
//        setUpCoinBox();

        return v;
    }


    public void setUpHeader(int resourceID) {
        ImageView logo = (ImageView) v.findViewById(R.id.logo);
        ImageView cheatButton = (ImageView) v.findViewById(R.id.cheat_button);
        logo.setVisibility(View.GONE);
        /*FrameLayout.LayoutParams layoutParams = ((FrameLayout.LayoutParams) logo.getLayoutParams());
        cheatButton.setLayoutParams(layoutParams);*/
        Picasso.with(getActivity()).load(resourceID).into(cheatButton);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.coin_box:
//                getActivity().startActivity(new Intent(getActivity(), StoreActivity.class));
                break;
        }

    }
    //endregion
}
