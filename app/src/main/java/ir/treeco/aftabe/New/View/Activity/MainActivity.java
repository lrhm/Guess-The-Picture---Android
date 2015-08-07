package ir.treeco.aftabe.New.View.Activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.New.Util.ImageManager;
import ir.treeco.aftabe.New.View.BackgroundDrawable;
import ir.treeco.aftabe.New.View.Fragment.MainFragment;
import ir.treeco.aftabe.R;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
    Context context;
    private ImageView cheatButton;
    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_main);

        cheatButton = (ImageView) findViewById(R.id.cheat_button);
        logo = (ImageView) findViewById(R.id.logo);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) cheatButton.getLayoutParams();
        layoutParams.leftMargin = (int) (0.724 * MainApplication.lengthManager.getScreenWidth());
        layoutParams.topMargin = (int) (0.07 * MainApplication.lengthManager.getScreenWidth());

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        if (fragmentManager.getBackStackEntryCount() != 0) throw new IllegalStateException();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MainFragment mainFragment = new MainFragment();
        fragmentTransaction.replace(R.id.fragment_container, mainFragment);
        fragmentTransaction.commit();

        context = this;

        setUpCoinBox();
        setUpHeader();
        setOriginalBackgroundColor();
    }

    private void setUpCoinBox() {
        ImageView coinBox = (ImageView) findViewById(R.id.coin_box);

        int coinBoxWidth = MainApplication.lengthManager.getScreenWidth() * 9 / 20;
        int coinBoxHeight = MainApplication.lengthManager.getHeightWithFixedWidth(R.drawable.coin_box, coinBoxWidth);
        coinBox.setImageBitmap(ImageManager.loadImageFromResource(this, R.drawable.coin_box, coinBoxWidth, coinBoxHeight));

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) coinBox.getLayoutParams();
        layoutParams.topMargin = MainApplication.lengthManager.getScreenWidth() / 15;
        layoutParams.leftMargin = MainApplication.lengthManager.getScreenWidth() / 50;

        TextView digits = (TextView) findViewById(R.id.digits);

        RelativeLayout.LayoutParams digitsLayoutParams = (RelativeLayout.LayoutParams) digits.getLayoutParams();
        digitsLayoutParams.topMargin = MainApplication.lengthManager.getScreenWidth() * 34 / 400;
        digitsLayoutParams.leftMargin = MainApplication.lengthManager.getScreenWidth() * 577 / 3600;
        digitsLayoutParams.width = MainApplication.lengthManager.getScreenWidth() / 5;

        digits.setTypeface(Typeface.createFromAsset(getAssets(), "yekan.ttf"));
        String number = "۸۸۸۸۸";
        digits.setText(number);

        coinBox.setOnClickListener(this);
    }

    private void setUpHeader() {
        RelativeLayout header = (RelativeLayout) findViewById(R.id.header);
        header.setLayoutParams(new LinearLayout.LayoutParams(
                MainApplication.lengthManager.getScreenWidth(),
                MainApplication.lengthManager.getHeaderHeight()
        ));

        logo.setImageBitmap(ImageManager.loadImageFromResource(
                this, R.drawable.header, MainApplication.lengthManager.getScreenWidth(),
                MainApplication.lengthManager.getScreenWidth() / 4
        ));
    }

    private void setOriginalBackgroundColor() {
        ImageView background = (ImageView) findViewById(R.id.background);
        background.setImageDrawable(new BackgroundDrawable(this, new int[]{
                Color.parseColor("#29CDB8"),
                Color.parseColor("#1FB8AA"),
                Color.parseColor("#0A8A8C")
        }));
    }

    @Override
    public void onClick(View v) {

    }

    public void setupCheatButton(int id) {
        cheatButton.setVisibility(View.VISIBLE);
        logo.setVisibility(View.INVISIBLE);
//        cheatButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View _view) {
//                if (!areCheatsVisible) {
//                    showCheats();
//                } else {
//                    hideCheats();
//                }
//            }
//        });


        String cheatImagePath = "file://" + context.getFilesDir().getPath() + "/Downloaded/"
                + id + "_cheatBitmap.png";

        Picasso.with(context).load(cheatImagePath).into(cheatButton);
    }

    public void hideCheatButton() {
        cheatButton.setVisibility(View.INVISIBLE);
        logo.setVisibility(View.VISIBLE);

    }
}
