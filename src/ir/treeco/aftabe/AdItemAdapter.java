package ir.treeco.aftabe;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import ir.treeco.aftabe.utils.ImageManager;
import ir.treeco.aftabe.utils.LengthManager;

/**
 * Created by hamed on 8/17/14.
 */
public class AdItemAdapter extends PagerAdapter {
    Context context;

    public AdItemAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Log.e("Aftabe+", "Inja ham umade!");
        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.ad_image, null);
        ImageView imageView = (ImageView) relativeLayout.getChildAt(0);
        imageView.setImageResource(R.drawable.ad);

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
