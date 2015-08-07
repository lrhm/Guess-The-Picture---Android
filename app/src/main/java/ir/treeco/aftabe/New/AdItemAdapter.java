package ir.treeco.aftabe.New;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.FileNotFoundException;

import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.New.Util.ImageManager;
import ir.treeco.aftabe.R;

public class AdItemAdapter extends PagerAdapter {
    private static final String TAG = "PagerAdapter";
    Context context;
    public final static String ADS_KEY = "number_of_ads";
    private String SHARED_PREFRENCES_TAG = "aftabe_plus";
    private int numberOfAds;

    public AdItemAdapter(Context context) {
        this.context = context;
        updateAds();
    }

    public void updateAds() {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFRENCES_TAG, Context.MODE_PRIVATE);
        numberOfAds = preferences.getInt(ADS_KEY, 0);
    }

    @Override
    public int getCount() {
        return numberOfAds;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.view_ad_image, null);
        ImageView imageView = (ImageView) relativeLayout.findViewById(R.id.adImageView);

        try {
            imageView.setImageBitmap(ImageManager.loadImageFromInputStream(context.openFileInput("ad" + position + ".jpg"), MainApplication.lengthManager.getScreenWidth(), -1));
            //imageView.setImageBitmap(ImageManager.loadImageFromInputStream(context.openFileInput("ad" + position + ".jpg"), LengthManager.getScreenWidth(), -1));
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Could not load ad!", e);
        }

        ImageView topShadow = (ImageView) relativeLayout.findViewById(R.id.top_shadow);
        ImageView bottomShadow = (ImageView) relativeLayout.findViewById(R.id.bottom_shadow);

        topShadow.setImageBitmap(ImageManager.loadImageFromResource(context, R.drawable.shadow_top,
                MainApplication.lengthManager.getScreenWidth(), -1));
        bottomShadow.setImageBitmap(ImageManager.loadImageFromResource(context, R.drawable.shadow_bottom,
                MainApplication.lengthManager.getScreenWidth(), -1));

        /*topShadow.setImageBitmap(ImageManager.loadImageFromResource(context, R.drawable.shadow_top, LengthManager.getScreenWidth(), -1));
        bottomShadow.setImageBitmap(ImageManager.loadImageFromResource(context, R.drawable.shadow_bottom, LengthManager.getScreenWidth(), -1));*/

        container.addView(relativeLayout);
        return relativeLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }
}
