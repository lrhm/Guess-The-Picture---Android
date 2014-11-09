package ir.treeco.aftabe;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import ir.treeco.aftabe.packages.*;
import ir.treeco.aftabe.packages.Package;
import ir.treeco.aftabe.utils.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hamed on 8/12/14.
 */
public class PackageListImplicitAdapter {
    private android.content.Context context;
    private PackageManager pManager;
    private MetaPackage[] packages;

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
            tag.frontCard = (FrameLayout) packageInfo.findViewById(R.id.front_card);
            tag.backCard = (FrameLayout) packageInfo.findViewById(R.id.back_card);
            tag.frontImage = (ImageView) packageInfo.findViewById(R.id.front_image);
            tag.backImage = (ImageView) packageInfo.findViewById(R.id.back_image);
            tag.frontButton = (ImageView) packageInfo.findViewById(R.id.front_button);
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

        Bitmap[] bitmaps;
        if (i < packages.length) {
            try {
                bitmaps = new Bitmap[]{
                        ImageManager.loadImageFromInputStream(packages[i].getFront(), myWidth, myHeight),
                        ImageManager.loadImageFromInputStream(packages[i].getBack(), myWidth, myHeight)
                };
            } catch (FileNotFoundException e) {
                throw new RuntimeException();
            }

        } else {
            bitmaps = new Bitmap[]{
                    ImageManager.loadImageFromResource(context, R.drawable.pack, myWidth, myHeight),
                    ImageManager.loadImageFromResource(context, R.drawable.packback, myWidth, myHeight)
            };
        }

//        Log.d("vamDaneshjuyi", i+" "+packages.length+" "+packages[i].getName()+" "+packages[i].getState());
//        try {
//            Log.d("vamDaneshjuyi", packages[i].getFront().toString());
//        } catch (FileNotFoundException e) {
//            Log.d("vamDaneshjuyi","why??????????!!!!");
//            e.printStackTrace();
//        }
//        Log.d("vamDaneshjuyi", bitmaps+"bitbit");
//        Log.d("vamDaneshjuyi", bitmaps.length+"lenlen");
//        Log.d("vamDaneshjuyi", itemData[i]+" ");
//        Log.d("vamDaneshjuyi", itemData[i].shape+" ");
//        Log.d("vamDaneshjuyi", bitmaps[itemData[i].shape]+" ");
//        Log.d("vamDaneshjuyi",itemData[i] + " " + itemData[i].shape + " " +bitmaps[itemData[i].shape]);
        final DownloadingDrawable frontDrawable = new DownloadingDrawable(bitmaps[itemData[i].shape]);
        tag.frontImage.setImageDrawable( frontDrawable );
//        tag.frontImage.setImageDrawable(new DownloadingDrawable(bitmaps[itemData[i].shape]));
        //tag.frontImage.setImageBitmap(bitmaps[itemData[i].shape]);
        tag.backImage.setImageBitmap(bitmaps[1 - itemData[i].shape]);

        packageInfo.setLayoutParams(new LinearLayout.LayoutParams(myWidth, myHeight));

        tag.frontCard.setVisibility(itemData[i].flipped ? View.GONE : View.VISIBLE);
        tag.backCard.setVisibility(itemData[i].flipped ? View.VISIBLE : View.GONE);

        //configure font card view layout-params
        if (i < packages.length) {
            /*if (packages[i].getState() == PackageState.REMOTE)
                tag.frontButton.setText("خرید " + packages[i].getCost());
            else if (packages[i].getState() == PackageState.DOWNLOADING)
                tag.frontButton.setText("در حال دانلود");
            else
                tag.frontButton.setText("ورود");*/
            int buttonWidth = myWidth * 3 / 8;
            tag.frontButton.setImageBitmap(ImageManager.loadImageFromResource(context, R.drawable.package_item_play, buttonWidth, LengthManager.getHeightWithFixedWidth(R.drawable.package_item_play, buttonWidth)));
            FrameLayout.LayoutParams buttonLayoutParams = (FrameLayout.LayoutParams) tag.frontButton.getLayoutParams();
            buttonLayoutParams.bottomMargin = myWidth / 7;

            tag.frontButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Log.d("migmig",packages[i].getName());
//                    if(packages[i].getName().equals("girls")) {
//                        Log.d("migmig",packages[i].getName() + " onClick");
//                        packages[i].becomeLocal();
//                        return;
//                    }
                    //TODO check if package data is outdated
                    if (packages[i].getState() == PackageState.REMOTE) {
                        DownloadProgressListener[] dpl = new DownloadProgressListener[] {
                            new NotificationProgressListener(packages[i].getContext(), packages[i]),
                            new PackageListProgressListener(frontDrawable)
                        };
                        packages[i].becomeLocal(dpl);
                    }
                    else if(packages[i].getState() == PackageState.LOCAL) {
                        LoadingManager.startTask(new TaskStartedListener() {
                            @Override
                            public void taskStarted() {
                                PackageFragment fragment = PackageFragment.newInstance(new Package(packages[i]));
                                FragmentTransaction transaction = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_container, fragment);
                                transaction.addToBackStack(null);
                                transaction.commit();
                            }
                        });
                    }
                }
            });
        }

        return packageInfo;
    }
}
