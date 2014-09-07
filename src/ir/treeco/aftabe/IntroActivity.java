package ir.treeco.aftabe;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import ir.treeco.aftabe.utils.ImageManager;
import ir.treeco.aftabe.utils.LengthManager;

public class IntroActivity extends FragmentActivity {
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

        ImageView header = (ImageView) findViewById(R.id.header_image);
        header.setImageBitmap(ImageManager.loadImageFromResource(IntroActivity.this, R.drawable.header, LengthManager.getScreenWidth(), LengthManager.getScreenWidth() * 714 / 2160));

        // List fragment transaction
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        PackageListFragment listFragment = new PackageListFragment();
        fragmentTransaction.add(R.id.fragment_container,listFragment);
        fragmentTransaction.commit();

        FrameLayout mainView = (FrameLayout) findViewById(R.id.main_view);
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[] {
                Color.parseColor("#F3C81D"),
                Color.parseColor("#F3C01E"),
                Color.parseColor("#F49C14")
        });
        gradientDrawable.mutate();
        gradientDrawable.setGradientRadius(LengthManager.getHeaderHeight() * 3);
        gradientDrawable.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        gradientDrawable.setGradientCenter(0.5F, 0.5F);
        mainView.setBackground(gradientDrawable);

        ImageView background = (ImageView) findViewById(R.id.background);
        background.setImageBitmap(ImageManager.loadImageFromResource(IntroActivity.this, R.drawable.circles, LengthManager.getScreenWidth(), LengthManager.getScreenHeight()));
    }

    /*private void loadBackground() {
        View mainView = findViewById(R.id.main_view);
        Bitmap background = ImageManager.loadImageFromResource(IntroActivity.this, R.drawable.background, LengthManager.getScreenWidth() / 2, LengthManager.getScreenHeight() / 2);
        if (Build.VERSION.SDK_INT >= 16)
            setBackgroundV16Plus(mainView, background);
        else
            setBackgroundV16Minus(mainView, background);
    }

    @TargetApi(16)
    private void setBackgroundV16Plus(View view, Bitmap bitmap) {
        view.setBackground(new BitmapDrawable(getResources(), bitmap));

    }

    @SuppressWarnings("deprecation")
    private void setBackgroundV16Minus(View view, Bitmap bitmap) {
        view.setBackgroundDrawable(new BitmapDrawable(bitmap));
    }*/

}
