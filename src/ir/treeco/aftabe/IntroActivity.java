package ir.treeco.aftabe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import ir.treeco.aftabe.utils.*;

import java.util.ArrayList;

public class IntroActivity extends FragmentActivity {
    private SharedPreferences preferences;

    /**
     * Called when the activity is first created.
     */


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Initialize Height Manager
        LengthManager.initialize(IntroActivity.this);
        // Load Layout
        setContentView(R.layout.activity_intro);

        preferences = getSharedPreferences(Utils.SHARED_PREFRENCES_TAG, Context.MODE_PRIVATE);

//        Utils.updateLastTime(this);

        RelativeLayout header = (RelativeLayout) findViewById(R.id.header);
        header.setLayoutParams(new LinearLayout.LayoutParams(LengthManager.getScreenWidth(), LengthManager.getHeaderHeight()));

        ImageView logo = (ImageView) findViewById(R.id.logo);
        logo.setImageBitmap(ImageManager.loadImageFromResource(IntroActivity.this, R.drawable.header, LengthManager.getScreenWidth(), LengthManager.getScreenWidth() / 4));

        setUpCoinBox();



        // List fragment transaction
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        if (fragmentManager.getBackStackEntryCount() != 0) throw new AssertionError();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        PackageListFragment listFragment = PackageListFragment.newInstance();
        fragmentTransaction.replace(R.id.fragment_container, listFragment);
        fragmentTransaction.commit();

        mainView = (FrameLayout) findViewById(R.id.main_view);
        Utils.setViewBackground(mainView, new BackgroundDrawable(this, new int[]{
                Color.parseColor("#F3C81D"),
                Color.parseColor("#F3C01E"),
                Color.parseColor("#F49C14")
        }));


        /*ImageView background = (ImageView) findViewById(R.id.background);
        background.setImageBitmap(ImageManager.loadImageFromResource(IntroActivity.this, R.drawable.circles, LengthManager.getScreenWidth(), LengthManager.getScreenHeight()));
        */

        final RelativeLayout loadingView = (RelativeLayout) getLayoutInflater().inflate(R.layout.view_ehem, null);

        LoadingManager.setSomeTaskStartedListener(new SomeTaskStartedListener() {
            @Override
            public void someTaskStarted(final TaskCallback callback) {
                pushToViewStack(loadingView, false);

                ImageView ehem = (ImageView) loadingView.findViewById(R.id.ehem);
                ehem.setImageBitmap(ImageManager.loadImageFromResource(IntroActivity.this, R.drawable.load, LengthManager.getLoadingEhemWidth(), -1));
                ehem.getLayoutParams().width = LengthManager.getLoadingEhemWidth();
                ehem.setPadding(0, LengthManager.getLoadingEhemPadding(), 0, 0);

                ImageView toilet = (ImageView) loadingView.findViewById(R.id.toilet);
                toilet.setImageBitmap(ImageManager.loadImageFromResource(IntroActivity.this, R.drawable.toilet, LengthManager.getLoadingToiletWidth(), -1));
                toilet.getLayoutParams().width = LengthManager.getLoadingToiletWidth();


                loadingView.setVisibility(View.VISIBLE);

                final Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setInterpolator(new DecelerateInterpolator());
                fadeIn.setDuration(600);
                fadeIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        callback.done();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                loadingView.clearAnimation();
                loadingView.startAnimation(fadeIn);
            }
        });

        LoadingManager.setTasksFinishedListener(new TasksFinishedListener() {
            @Override
            public void tasksFinished() {
                final Animation fadeOut = new AlphaAnimation(1, 0);
                fadeOut.setInterpolator(new AccelerateInterpolator());
                fadeOut.setStartOffset(200);
                fadeOut.setDuration(200);

                fadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        loadingView.setVisibility(View.INVISIBLE);
                        popFromViewStack(loadingView);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                loadingView.clearAnimation();
                loadingView.startAnimation(fadeOut);
            }
        });
        Log.d("paspas",Utils.getAESkey(this));
        Log.d("paspas",Utils.getAESkey(this).getBytes().length+" ");
    }

    private void setUpCoinBox() {
        ImageView coinBox = (ImageView) findViewById(R.id.coin_box);
        int coinBoxWidth = LengthManager.getScreenWidth() * 9 / 20;
        int coinBoxHeight = LengthManager.getHeightWithFixedWidth(R.drawable.coin_box, coinBoxWidth);
        coinBox.setImageBitmap(ImageManager.loadImageFromResource(IntroActivity.this, R.drawable.coin_box, coinBoxWidth, coinBoxHeight));

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) coinBox.getLayoutParams();
        layoutParams.topMargin = LengthManager.getScreenWidth() / 15;
        layoutParams.leftMargin = LengthManager.getScreenWidth() / 50;

        LinearLayout digits = (LinearLayout) findViewById(R.id.digits);
        RelativeLayout.LayoutParams digitsLayoutParams = (RelativeLayout.LayoutParams) digits.getLayoutParams();
        digitsLayoutParams.topMargin = LengthManager.getScreenWidth() * 40 / 360;
        digitsLayoutParams.leftMargin = LengthManager.getScreenWidth() * 575 / 3600;
        digitsLayoutParams.width = LengthManager.getScreenWidth() / 5;

        coinBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadingManager.startTask(new TaskStartedListener() {
                    @Override
                    public void taskStarted() {
                        StoreFragment fragment = StoreFragment.getInstance();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                });
            }
        });


        CoinManager.setCoinsChangedListener(new CoinManager.CoinsChangedListener() {
            @Override
            public void changed(int newAmount) {
                LinearLayout digits = (LinearLayout) findViewById(R.id.digits);
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

                digits.addView(Utils.makeNewSpace(IntroActivity.this));
                for (int i = 0; i < number.length(); i++) {
                    int d = number.charAt(i) - '0';
                    ImageView digit = new ImageView(IntroActivity.this);
                    int digitHeight = LengthManager.getScreenWidth() / 21;
                    digit.setImageBitmap(ImageManager.loadImageFromResource(IntroActivity.this, digitResource[d], LengthManager.getWidthWithFixedHeight(digitResource[d], digitHeight), digitHeight));
                    digits.addView(digit);
                }
                digits.addView(Utils.makeNewSpace(IntroActivity.this));
            }
        }, preferences);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (StoreFragment.billingProcessor == null || !StoreFragment.billingProcessor.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    FrameLayout mainView;

    @Override
    public void onBackPressed() {
        if (!currentlyPushedViews.isEmpty()) {
            View view = currentlyPushedViews.get(currentlyPushedViews.size() - 1);
            popFromViewStack(view);
            return;
        }

        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();

        if (backStackEntryCount == 0) {
            super.onBackPressed();
            return;
        }

        LoadingManager.startTask(new TaskStartedListener() {
            @Override
            public void taskStarted() {
                Log.d("back", "pressed");
                IntroActivity.super.onBackPressed();
            }
        });
    }

    ArrayList<View> currentlyPushedViews = new ArrayList<View>();

    void pushToViewStack(View view, boolean popOnBackPressed) {
        mainView.addView(view);
        if (popOnBackPressed)
            currentlyPushedViews.add(view);
    }

    void popFromViewStack(View view) {
        currentlyPushedViews.remove(view);
        mainView.removeView(view);
    }
}
