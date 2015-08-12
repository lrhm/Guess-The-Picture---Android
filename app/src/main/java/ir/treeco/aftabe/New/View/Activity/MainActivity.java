package ir.treeco.aftabe.New.View.Activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
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

import java.io.File;
import java.io.FileNotFoundException;

import ir.treeco.aftabe.New.AdItemAdapter;

import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.New.AutoScrollViewPager;
import ir.treeco.aftabe.New.Util.ImageManager;
import ir.treeco.aftabe.New.View.Custom.BackgroundDrawable;
import ir.treeco.aftabe.New.View.Fragment.GameFragmentNew;
import ir.treeco.aftabe.New.View.Fragment.MainFragment;
import ir.treeco.aftabe.R;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
    Context context;
    private ImageView cheatButton;
    private ImageView logo;
    private AutoScrollViewPager autoScrollViewPager;
   // private AdItemAdapter adItemAdapter;
    private boolean areCheatsVisible = false;
    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_main);

        cheatButton = (ImageView) findViewById(R.id.cheat_button);
        logo = (ImageView) findViewById(R.id.logo);

        autoScrollViewPager = ((AutoScrollViewPager) findViewById(R.id.ad_view_pager));

        cheatButton.setOnClickListener(this);

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
        setUpAds(autoScrollViewPager);
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
        switch (v.getId()) {
            case R.id.cheat_button:
                toggleCheatButton();
                break;
        }
    }

    public void setupCheatButton(int id) {
        cheatButton.setVisibility(View.VISIBLE);
        logo.setVisibility(View.INVISIBLE);
        areCheatsVisible = false;
        currentLevel = id;

        String cheatImagePath = "file://" + context.getFilesDir().getPath() + "/Downloaded/"
                + id + "_cheatBitmap.png";

        Picasso.with(context).load(cheatImagePath).into(cheatButton);
    }

    public void hideCheatButton() {
        cheatButton.setVisibility(View.INVISIBLE);
        logo.setVisibility(View.VISIBLE);
    }

    public void toggleCheatButton() {
        disableCheatButton(false);
        if (!areCheatsVisible) {
            String cheatImagePath = "file://" + context.getFilesDir().getPath() + "/Downloaded/"
                    + currentLevel + "_backBitmap.png";

            Picasso.with(context).load(cheatImagePath).into(cheatButton);
            areCheatsVisible = true;

            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (fragment instanceof GameFragmentNew)
                ((GameFragmentNew) fragment).showCheats();

        } else {
            String cheatImagePath = "file://" + context.getFilesDir().getPath() + "/Downloaded/"
                    + currentLevel + "_cheatBitmap.png";

            Picasso.with(context).load(cheatImagePath).into(cheatButton);
            areCheatsVisible = false;

            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (fragment instanceof GameFragmentNew)
                ((GameFragmentNew) fragment).hideCheats();
        }
    }


    private void setUpAds(AutoScrollViewPager autoScrollViewPager) {
        AdItemAdapter adItemAdapter = new AdItemAdapter(context);
        autoScrollViewPager.setAdapter(adItemAdapter);
        autoScrollViewPager.setOffscreenPageLimit(1);
        autoScrollViewPager.setInterval(5000);
        autoScrollViewPager.startAutoScroll();
    }

    public void disableCheatButton(boolean enable) {
        cheatButton.setClickable(enable);}

}
