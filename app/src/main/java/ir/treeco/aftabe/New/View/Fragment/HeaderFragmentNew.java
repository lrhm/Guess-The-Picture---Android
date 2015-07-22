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
import android.widget.TextView;

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
        setUpHeader();
        // setup coinBox
        setUpCoinBox();

        return v;
    }


    //region SetUpCoinBox
    private void setUpCoinBox() {
        ImageView coinBox = (ImageView) v.findViewById(R.id.coin_box);

//        Picasso.with(getActivity()).load(R.drawable.coin_box).into(coinBox);
//        int coinBoxWidth = LengthManager.getScreenWidth() * 9 / 20;
//        int coinBoxHeight = LengthManager.getHeightWithFixedWidth(R.drawable.coin_box, coinBoxWidth);
//        coinBox.setImageBitmap(ImageManager.loadImageFromResource(getActivity(), R.drawable.coin_box, coinBoxWidth, coinBoxHeight));

//        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) coinBox.getLayoutParams();
//        layoutParams.topMargin = LengthManager.getScreenWidth() / 15;
//        layoutParams.leftMargin = LengthManager.getScreenWidth() / 50;

//        LinearLayout digits = (LinearLayout) v.findViewById(R.id.digits);
//        RelativeLayout.LayoutParams digitsLayoutParams = (RelativeLayout.LayoutParams) digits.getLayoutParams();
//        digitsLayoutParams.topMargin = LengthManager.getScreenWidth() * 40 / 360;
//        digitsLayoutParams.leftMargin = LengthManager.getScreenWidth() * 575 / 3600;
//        digitsLayoutParams.width = LengthManager.getScreenWidth() / 5;

        coinBox.setOnClickListener(this);


        TextView digits = (TextView) v.findViewById(R.id.digits);
        digits.setTypeface(Typeface.createFromAsset(activity.getAssets(), "yekan.ttf"));
        digits.setTextSize(20);
        String number = "۱۲۳۴۷۶۶۷۷۸۸۷۶۶۵۴۶";
        digits.setText(number);
    }

/*
        CoinManager.setCoinsChangedListener(new CoinManager.CoinsChangedListener() {
            @Override
            public void changed(int newAmount) {
                LinearLayout digits = (LinearLayout) v.findViewById(R.id.digits);
                digits.removeAllViews();

                String number = "" + CoinManager.getCoinsCount(preferences);

                int[] digitResource = new int[]{
                        R.drawable.digit_0,
                        R.drawable.digit_1,
                        R.drawable.digit_2,
                        R.drawable.digit_3,
                        R.drawable.digit_4,
                        R.drawable.digit_5,
                        R.drawable.digit_6,
                        R.drawable.digit_7,
                        R.drawable.digit_8,
                        R.drawable.digit_9,
                };

                digits.addView(Utils.makeNewSpace(getActivity()));
                for (int i = 0; i < number.length(); i++) {
                    int d = number.charAt(i) - '0';
                    ImageView digit = new ImageView(getActivity());
                    int digitHeight = LengthManager.getScreenWidth() / 21;
                    digit.setImageBitmap(ImageManager.loadImageFromResource(getActivity(), digitResource[d], LengthManager.getWidthWithFixedHeight(digitResource[d], digitHeight), digitHeight));
                    digits.addView(digit);
                }
                digits.addView(Utils.makeNewSpace(getActivity()));
            }
        }, preferences);

        */

//    }
    //endregion

    //region SetUpHeader
    private void setUpHeader() {
//        RelativeLayout header = (RelativeLayout) v.findViewById(R.id.header);
//        header.setLayoutParams(new RelativeLayout.LayoutParams(LengthManager.getScreenWidth(), LengthManager.getHeaderHeight()));
        ImageView logo = (ImageView) v.findViewById(R.id.logo);
//        Picasso.with(getActivity()).load(R.drawable.header).into(logo);
//        logo.setImageBitmap(ImageManager.loadImageFromResource(getActivity(), R.drawable.header, LengthManager.getScreenWidth(), LengthManager.getScreenWidth() / 4));

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
