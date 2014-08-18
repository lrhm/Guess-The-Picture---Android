package ir.treeco.aftabe;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import ir.treeco.aftabe.utils.ImageManager;
import ir.treeco.aftabe.utils.LengthManager;
import ir.treeco.aftabe.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hamed on 8/12/14.
 */
public class PackageListImplicitAdapter {
    private android.content.Context context;

    public void flip(int i, View view) {
        if (!itemData[i].enabled)
            return;
        view.clearAnimation();
        view.startAnimation(itemData[i].toMiddle);
    }

    static class ItemData {
        boolean flipped = false;
        boolean enabled = true;
        Animation toMiddle;
        Animation fromMiddle;
        public int shape;
    }

    ItemData[] itemData;

    public PackageListImplicitAdapter(Context context) {
        this.context = context;
        setFilter(0);
    }

    void setFilter(int shape) {
        itemData = new ItemData[shape == 1? 3: 55];
        for (int i = 0; i < itemData.length; i++) {
            itemData[i] = new ItemData();
            itemData[i].toMiddle = AnimationUtils.loadAnimation(context, R.anim.to_middle);
            itemData[i].fromMiddle = AnimationUtils.loadAnimation(context, R.anim.from_middle);
            itemData[i].shape = shape;
        }
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        for (DataSetObserver observer: observers)
            observer.onChanged();
    }

    List<DataSetObserver> observers = new ArrayList<DataSetObserver>();
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {
        observers.add(dataSetObserver);
    }

    public int getCount() {
        return itemData.length;
    }

    public View getView(final int i, View packageInfo, ViewGroup viewGroup) {
        if (packageInfo == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            packageInfo = inflater.inflate(R.layout.package_info, null);
            final PackageInfoTag tag = new PackageInfoTag();
            tag.packageName = (TextView) packageInfo.findViewById(R.id.package_name);
            tag.frontCard = (FrameLayout) packageInfo.findViewById(R.id.front_card);
            tag.backCard = (FrameLayout) packageInfo.findViewById(R.id.back_card);
            tag.frontImage = (ImageView) packageInfo.findViewById(R.id.front_image);
            tag.backImage = (ImageView) packageInfo.findViewById(R.id.back_image);
            packageInfo.setTag(tag);
        }

        packageInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PackageListImplicitAdapter.this.flip(i, view);
            }
        });

        int myWidth = LengthManager.getScreenWidth() / 2;
        int myHeight = myWidth * 430 / 410;

        final PackageInfoTag tag = (PackageInfoTag) packageInfo.getTag();
        final View finalPackageInfo = packageInfo;

        itemData[i].toMiddle.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                itemData[i].enabled = false;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                finalPackageInfo.clearAnimation();
                finalPackageInfo.startAnimation(itemData[i].fromMiddle);
                Utils.toggleVisibility(tag.frontCard);
                Utils.toggleVisibility(tag.backCard);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        itemData[i].fromMiddle.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                itemData[i].enabled = true;
                itemData[i].flipped = !itemData[i].flipped;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        Bitmap[] bitmaps = new Bitmap[] {
                ImageManager.loadImageFromResource(context, R.drawable.aftab, myWidth, myHeight),
                ImageManager.loadImageFromResource(context, R.drawable.chaman, myWidth, myHeight)
        };

        tag.frontImage.setImageBitmap(bitmaps[itemData[i].shape]);
        tag.backImage.setImageBitmap(bitmaps[1 - itemData[i].shape]);

        packageInfo.setLayoutParams(new LinearLayout.LayoutParams(myWidth, myHeight));

        tag.frontCard.setVisibility(itemData[i].flipped? View.GONE: View.VISIBLE);
        tag.backCard.setVisibility(itemData[i].flipped? View.VISIBLE: View.GONE);

        return packageInfo;
    }
}
