package ir.treeco.aftabe;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import ir.treeco.aftabe.packages.Package;
import ir.treeco.aftabe.packages.PackageManager;
import ir.treeco.aftabe.packages.PackageState;
import ir.treeco.aftabe.utils.ImageManager;
import ir.treeco.aftabe.utils.LengthManager;
import ir.treeco.aftabe.utils.Utils;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hamed on 8/12/14.
 */
public class PackageListImplicitAdapter {
    private android.content.Context context;
    private PackageManager pManager;
    private Package[] packages;

    public final static int NEW_TAB_ADAPTER = 0, LOCAL_TAB_ADAPTER = 1, HOT_TAB_ADAPTER = 2;

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

    int mode;
    public PackageListImplicitAdapter(Context context, PackageManager pManager, int mode) {
        this.mode = mode;
        this.context = context;
        this.pManager = pManager;
        switch (mode) {
            case NEW_TAB_ADAPTER:
                this.packages = pManager.getNewPackages();
                break;
            case LOCAL_TAB_ADAPTER:
                this.packages = pManager.getLocalPackages();
                break;
            case HOT_TAB_ADAPTER:
                this.packages = pManager.getHotPackages();
                break;
        }
        setFilter(0);
    }

    void setFilter(int shape) {
        Log.d("vamDaneshjuyi",packages+" "+mode);
        itemData = new ItemData[packages.length + 20];
        for (int i = 0; i < itemData.length; i++) {
            itemData[i] = new ItemData();
            itemData[i].toMiddle = AnimationUtils.loadAnimation(context, R.anim.to_middle);
            itemData[i].fromMiddle = AnimationUtils.loadAnimation(context, R.anim.from_middle);
            itemData[i].shape = shape;
        }
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        for (DataSetObserver observer : observers)
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
            tag.packageInfo = (TextView) packageInfo.findViewById(R.id.package_info);
            tag.frontCard = (FrameLayout) packageInfo.findViewById(R.id.front_card);
            tag.backCard = (FrameLayout) packageInfo.findViewById(R.id.back_card);
            tag.frontImage = (ImageView) packageInfo.findViewById(R.id.front_image);
            tag.backImage = (ImageView) packageInfo.findViewById(R.id.back_image);
            tag.frontButton = (Button) packageInfo.findViewById(R.id.front_button);
            packageInfo.setTag(tag);
        }

        packageInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PackageListImplicitAdapter.this.flip(i, view);
            }
        });

        int myWidth = LengthManager.getScreenWidth() / 2;
        int myHeight = myWidth;

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

        Bitmap[] bitmaps = null;
        if (i < packages.length) {
                bitmaps = new Bitmap[]{
                        ImageManager.loadImageFromInputStream(packages[i].getFront(), myWidth, myHeight),
                        ImageManager.loadImageFromInputStream(packages[i].getBack(), myWidth, myHeight),
                };
        } else {
            bitmaps = new Bitmap[]{
                    ImageManager.loadImageFromResource(context, R.drawable.pack, myWidth, myHeight),
                    ImageManager.loadImageFromResource(context, R.drawable.packback, myWidth, myHeight)
            };
        }

        tag.frontImage.setImageBitmap(bitmaps[itemData[i].shape]);
        tag.backImage.setImageBitmap(bitmaps[1 - itemData[i].shape]);

        packageInfo.setLayoutParams(new LinearLayout.LayoutParams(myWidth, myHeight));

        tag.frontCard.setVisibility(itemData[i].flipped ? View.GONE : View.VISIBLE);
        tag.backCard.setVisibility(itemData[i].flipped ? View.VISIBLE : View.GONE);

        if (i < packages.length) {
            tag.packageInfo.setText(packages[i].getDescription());
            tag.packageName.setText(packages[i].getName());
        }

        //configure font card view layout-params
        if (i < packages.length) {
            if (packages[i].getState() == PackageState.remote)
                tag.frontButton.setText("خرید " + packages[i].getCost());
            else if (packages[i].getState() == PackageState.downloading)
                tag.frontButton.setText("در حال دانلود");
            else
                tag.frontButton.setText("ورود");
            float textSize = myWidth / 10.0f;
            tag.frontButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            tag.frontButton.setPadding((int) textSize, 0, (int) textSize, 0);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) tag.frontButton.getLayoutParams();
            params.setMargins(0, 0, 0, 3 * (int) textSize / 2);
            Log.e("parSize", "" + textSize);
            tag.packageName.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            params = (FrameLayout.LayoutParams) tag.packageName.getLayoutParams();
            params.setMargins(0, (int) textSize, 0, 0);

            tag.frontButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (packages[i].getState() == PackageState.remote) {
                        packages[i].becomeLocal();
                    }
                    else if(packages[i].getState() == PackageState.builtIn || packages[i].getState() == PackageState.local) {
                        PackageFragment fragment = PackageFragment.newInstance(packages[i], (FragmentActivity) context);
                        FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_container, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                }
            });
            tag.frontButton.setVisibility(View.VISIBLE);
        } else {
            tag.frontButton.setVisibility(View.INVISIBLE);
        }

        return packageInfo;
    }
}
