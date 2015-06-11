package ir.treeco.aftabe.New.View.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import ir.treeco.aftabe.New.View.Activity.StoreActivity;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.utils.LengthManager;

public class HeaderFragmentNew extends Fragment {
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
        LengthManager.initialize(activity.getBaseContext());
        // setup header
        setUpHeader();
        // setup coinBox
        setUpCoinBox();

        return v;
    }


    //region SetUpCoinBox
    private void setUpCoinBox() {
        ImageView coinBox = (ImageView) v.findViewById(R.id.coin_box);

        Picasso.with(getActivity()).load(R.drawable.coin_box).into(coinBox);
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

        coinBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getActivity(), StoreActivity.class));
            }
        });

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
    }
    //endregion

    //region SetUpHeader
    private void setUpHeader() {
//        RelativeLayout header = (RelativeLayout) v.findViewById(R.id.header);
//        header.setLayoutParams(new RelativeLayout.LayoutParams(LengthManager.getScreenWidth(), LengthManager.getHeaderHeight()));

        ImageView logo = (ImageView) v.findViewById(R.id.logo);
        Picasso.with(getActivity()).load(R.drawable.header).into(logo);
//        logo.setImageBitmap(ImageManager.loadImageFromResource(getActivity(), R.drawable.header, LengthManager.getScreenWidth(), LengthManager.getScreenWidth() / 4));

    }
    //endregion
}
