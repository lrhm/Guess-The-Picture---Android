package ir.treeco.aftabe.New;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
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
    // FIXME Here I set the number of ads to 3, but as I Mentioned with a todo in MainApplication we should add number of ads to the DB or SP then load it here in constructor
    private int numberOfAds = 3;

    public AdItemAdapter(Context context) {
        this.context = context;
        //updateAds();
    }

    public void updateAds() {
        /*SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFRENCES_TAG, Context.MODE_PRIVATE);
        numberOfAds = preferences.getInt(ADS_KEY, 0);*/
    }

    @Override
    public int getCount() {
        return numberOfAds;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View v = LayoutInflater.from(context).inflate(R.layout.view_ad_image, container, false);
        ImageView imageView = (ImageView) v.findViewById(R.id.adImageView);
       try {
           imageView.setImageBitmap(ImageManager.loadImageFromInputStream(context.openFileInput("ad" + position + ".jpg"), MainApplication.lengthManager.getScreenWidth(), -1));
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Could not load ad!", e);
           imageView.setImageBitmap(ImageManager.loadImageFromResource(context,R.drawable.ad,MainApplication.lengthManager.getScreenWidth(), -1));
        }ImageView topShadow = (ImageView) v.findViewById(R.id.top_shadow);
        ImageView bottomShadow = (ImageView) v.findViewById(R.id.bottom_shadow);

        topShadow.setImageBitmap(ImageManager.loadImageFromResource(context, R.drawable.shadow_top,
                MainApplication.lengthManager.getScreenWidth(), -1));
        bottomShadow.setImageBitmap(ImageManager.loadImageFromResource(context, R.drawable.shadow_bottom,
                MainApplication.lengthManager.getScreenWidth(), -1));
        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}
