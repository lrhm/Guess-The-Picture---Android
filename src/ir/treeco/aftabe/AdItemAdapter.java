package ir.treeco.aftabe;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import ir.treeco.aftabe.utils.ImageManager;
import ir.treeco.aftabe.utils.LengthManager;
import ir.treeco.aftabe.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by hamed on 8/17/14.
 */
public class AdItemAdapter extends PagerAdapter {
    Context context;
    public final static String ADS_KEY = "number_of_ads";
    private int num_of_ads;

    public AdItemAdapter(Context context) {
        this.context = context;
        update_ads();
    }

    public void update_ads() {
        SharedPreferences preferences = context.getSharedPreferences(Utils.sharedPrefrencesTag(), context.MODE_PRIVATE);
        num_of_ads = preferences.getInt(ADS_KEY,0);
    }

    @Override
    public int getCount() {
        return num_of_ads;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.view_ad_image, null);
        ImageView imageView = (ImageView) relativeLayout.getChildAt(0);
        try {
            imageView.setImageBitmap(ImageManager.loadImageFromInputStream(context.openFileInput("ad"+position+".jpg"),LengthManager.getScreenWidth(),-1));
        } catch (FileNotFoundException e) {
            imageView.setImageResource(R.drawable.ad);
            e.printStackTrace();
        }
//        imageView.setImageResource(R.drawable.ad);

        ImageView topShadow = (ImageView) relativeLayout.getChildAt(1);
        ImageView bottomShadow = (ImageView) relativeLayout.getChildAt(2);

        topShadow.setImageBitmap(ImageManager.loadImageFromResource(context, R.drawable.shadow_top, LengthManager.getScreenWidth(), -1));
        bottomShadow.setImageBitmap(ImageManager.loadImageFromResource(context, R.drawable.shadow_bottom, LengthManager.getScreenWidth(), -1));

        container.addView(relativeLayout);
        return relativeLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }
}
