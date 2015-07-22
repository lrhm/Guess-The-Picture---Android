package ir.treeco.aftabe.New.View.Fragment;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.New.Util.ImageManager;
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
        setUpHeader();
        // setup coinBox
        setUpCoinBox();

        return v;
    }


    //region SetUpCoinBox
    private void setUpCoinBox() {
        ImageView coinBox = (ImageView) v.findViewById(R.id.coin_box);

        int coinBoxWidth = MainApplication.lengthManager.getScreenWidth() * 9 / 20;
        int coinBoxHeight = MainApplication.lengthManager.getHeightWithFixedWidth(R.drawable.coin_box, coinBoxWidth);
        coinBox.setImageBitmap(ImageManager.loadImageFromResource(getActivity(), R.drawable.coin_box, coinBoxWidth, coinBoxHeight));


        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) coinBox.getLayoutParams();
        layoutParams.topMargin = MainApplication.lengthManager.getScreenWidth() / 15;
        layoutParams.leftMargin = MainApplication.lengthManager.getScreenWidth() / 50;


        TextView digits = (TextView) v.findViewById(R.id.digits);

        RelativeLayout.LayoutParams digitsLayoutParams = (RelativeLayout.LayoutParams) digits.getLayoutParams();
        digitsLayoutParams.topMargin = MainApplication.lengthManager.getScreenWidth() * 34 / 400;
        digitsLayoutParams.leftMargin = MainApplication.lengthManager.getScreenWidth() * 577 / 3600;
        digitsLayoutParams.width = MainApplication.lengthManager.getScreenWidth() / 5;

        digits.setTypeface(Typeface.createFromAsset(activity.getAssets(), "yekan.ttf"));
        String number = "۸۸۸۸۸";
        digits.setText(number);

        coinBox.setOnClickListener(this);

    }


    //region SetUpHeader
    private void setUpHeader() {

        RelativeLayout header = (RelativeLayout) v.findViewById(R.id.header);
        header.setLayoutParams(new LinearLayout.LayoutParams(MainApplication.lengthManager.getScreenWidth(), MainApplication.lengthManager.getHeaderHeight()));

        ImageView logo = (ImageView) v.findViewById(R.id.logo);
        logo.setImageBitmap(ImageManager.loadImageFromResource(getActivity(), R.drawable.header, MainApplication.lengthManager.getScreenWidth(), MainApplication.lengthManager.getScreenWidth() / 4));

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
