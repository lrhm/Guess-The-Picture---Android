package ir.treeco.aftabe2.Adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.FileNotFoundException;

import ir.treeco.aftabe2.MainApplication;
import ir.treeco.aftabe2.R;
import ir.treeco.aftabe2.Util.ImageManager;
import ir.treeco.aftabe2.Util.LengthManager;

public class AdItemAdapter extends PagerAdapter {
    private static final String TAG = "PagerAdapter";
    Context context;
    public final static String ADS_KEY = "number_of_ads";
    // FIXME Here I set the number of ads to 3, but as I Mentioned with a todo in MainApplication we should add number of ads to the DB or SP then load it here in constructor
    private int numberOfAds = 3;
    private LayoutInflater inflater;
    private ImageManager imageManager;
    private LengthManager lengthManager;

    public AdItemAdapter(Context context) {
        this.context = context;
        imageManager = ((MainApplication) context.getApplicationContext()).getImageManager();
        lengthManager = ((MainApplication) context.getApplicationContext()).getLengthManager();
        inflater = LayoutInflater.from(context);
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
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View v = inflater.inflate(R.layout.ad_image, null);
        ImageView imageView = (ImageView) v.findViewById(R.id.adImageView);

       try {
           imageView.setImageBitmap(imageManager.loadImageFromInputStream(context.openFileInput("ad" + position + ".jpg"), lengthManager.getScreenWidth(), -1));
        } catch (FileNotFoundException e) {
           imageView.setImageBitmap(imageManager.loadImageFromResource(R.drawable.ad, lengthManager.getScreenWidth(), -1));
        }

        ImageView topShadow = (ImageView) v.findViewById(R.id.top_shadow);
        ImageView bottomShadow = (ImageView) v.findViewById(R.id.bottom_shadow);

        topShadow.setImageBitmap(imageManager.loadImageFromResource(R.drawable.shadow_top,
                lengthManager.getScreenWidth(), -1));
        bottomShadow.setImageBitmap(imageManager.loadImageFromResource(R.drawable.shadow_bottom,
                lengthManager.getScreenWidth(), -1));
        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
