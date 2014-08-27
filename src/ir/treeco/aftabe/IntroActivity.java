package ir.treeco.aftabe;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import ir.treeco.aftabe.packages.PackageManager;
import ir.treeco.aftabe.utils.ImageManager;
import ir.treeco.aftabe.utils.LengthManager;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

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
        header.setBackground(new BitmapDrawable(getResources(), ImageManager.loadImageFromResource(IntroActivity.this, R.drawable.header_background, LengthManager.getScreenWidth(), LengthManager.getScreenWidth() * 714 / 2160)));

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
