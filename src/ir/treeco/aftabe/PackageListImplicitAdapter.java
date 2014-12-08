package ir.treeco.aftabe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import ir.treeco.aftabe.packages.*;
import android.widget.TextView;
import ir.treeco.aftabe.packages.MetaPackage;
import ir.treeco.aftabe.packages.Package;
import ir.treeco.aftabe.utils.*;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hamed on 8/12/14.
 */
public class PackageListImplicitAdapter {
    private int filter;

    abstract class PackageInfoListener implements View.OnTouchListener, View.OnClickListener {
        float x;
        float y;

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            x = motionEvent.getX();
            y = motionEvent.getY();
            return false;
        }
    }
    
    class PackageInfoTag {
        ImageView frontCard;
        ImageView backCard;
    }

    private IntroActivity activity;
    private PackageManager pManager;
    private MetaPackage[] packages;

    public final static int NEW_TAB = 2, LOCAL_TAB = 1, HOT_TAB = 0;

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
    public PackageListImplicitAdapter(IntroActivity activity, PackageManager pManager) {
        this.mode = mode;
        this.activity = activity;
        this.pManager = pManager;
        setFilter(-1);
    }

    void setFilter(int filter) {
        this.filter = filter;
        switch (filter) {
            case NEW_TAB:
                packages = pManager.getNewPackages();
                break;
            case LOCAL_TAB:
                packages = pManager.getLocalPackages();
                break;
            case HOT_TAB:
                packages = pManager.getHotPackages();
                break;
            default:
                packages = new MetaPackage[0];
        }
        itemData = new ItemData[packages.length];
        for (int i = 0; i < itemData.length; i++) {
            itemData[i] = new ItemData();
            itemData[i].toMiddle = AnimationUtils.loadAnimation(activity, R.anim.to_middle);
            itemData[i].fromMiddle = AnimationUtils.loadAnimation(activity, R.anim.from_middle);
        }
        notifyDataSetChanged();
    }

    public int getFilter() {
        return filter;
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
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            packageInfo = inflater.inflate(R.layout.view_package_info, null);

            final PackageInfoTag tag = new PackageInfoTag();
            tag.frontCard = (ImageView) packageInfo.findViewById(R.id.front_card);
            tag.backCard = (ImageView) packageInfo.findViewById(R.id.back_card);

            packageInfo.setTag(tag);
        }

        final int mySize = LengthManager.getScreenWidth() / LengthManager.getPackagesListColumnCount();

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
        {
            final InputStream front;
            final InputStream back;
            try {
                front = packages[i].getFront();
                back = packages[i].getBack();
            } catch (FileNotFoundException e) {
                throw new RuntimeException();
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    {
                        Bitmap frontBitmap = ImageManager.loadImageFromInputStream(front, mySize, mySize);
                        final DownloadingDrawable frontDrawable = new DownloadingDrawable(frontBitmap);
                        tag.frontCard.post(new Runnable() {
                            @Override
                            public void run() {
                                tag.frontCard.setImageDrawable(frontDrawable);
                            }
                        });
                    }
                    {
                        final Bitmap backBitmap = ImageManager.loadImageFromInputStream(back, mySize, mySize);
                        tag.backCard.post(new Runnable() {
                            @Override
                            public void run() {
                                tag.backCard.setImageBitmap(backBitmap);
                            }
                        });
                    }
                }
            }).start();
        }

        packageInfo.setLayoutParams(new LinearLayout.LayoutParams(mySize, mySize));

        tag.frontCard.setVisibility(itemData[i].flipped ? View.GONE : View.VISIBLE);
        tag.backCard.setVisibility(itemData[i].flipped ? View.VISIBLE : View.GONE);

        PackageInfoListener packageInfoListener = new PackageInfoListener() {
            @Override
            public void onClick(View view) {
                Log.d("ImplicitAdapter::OnClick", " clicked general");
                if (itemData[i].flipped || x < mySize / 3 && y < mySize / 3) {
                    PackageListImplicitAdapter.this.flip(i, view);
                    return;
                }

                if( packages[i].getIsDownloading() == true ) {
                    DownloadCancelAlert dialog = new DownloadCancelAlert(packages[i]);
                    dialog.show(activity.getSupportFragmentManager(), "CancelDownload");
                    return;
                }

                if (/* true || */ packages[i].getState() == PackageState.REMOTE) {
                    Log.d("ImplicitAdapter::OnClick", " clicked for dl");
//                    createPackagePurchaseDialog(packages[i]);
                    DownloadProgressListener[] dpl = new DownloadProgressListener[] {
                            new NotificationProgressListener(packages[i].getContext(), packages[i]),
                            new DownloadProgressListener() {
                                DownloadingDrawable drawable = (DownloadingDrawable) tag.frontCard.getDrawable();
                                ImageView imageView = tag.frontCard;

                                int last=0;
                                @Override
                                public void update(int progressInPercentage) {
                                    if(last != progressInPercentage) {
                                        drawable.setPercentage(progressInPercentage);
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                imageView.setImageDrawable(drawable);
                                                notifyDataSetChanged();
                                            }
                                        });
                                    }
                                    last = progressInPercentage;
                                }

                                @Override
                                public void success() {
                                    drawable.setPercentage(100);
                                }

                                @Override
                                public void failure() {
                                    drawable.setPercentage(100);
                                }
                            }
                    };
                    packages[i].becomeLocal(dpl);
                } else if(packages[i].getState() == PackageState.LOCAL) {
                    LoadingManager.startTask(new TaskStartedListener() {
                        @Override
                        public void taskStarted() {
                            PackageFragment fragment = PackageFragment.newInstance(new Package(packages[i]));
                            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.fragment_container, fragment);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }
                    });
                }
            }
        };

        packageInfo.setOnClickListener(packageInfoListener);
        packageInfo.setOnTouchListener(packageInfoListener);

        return packageInfo;
    }

    enum TextType {
        TITLE,
        DESCRIPTION,
        ITEM_TEXT
    }

    private void customizeTextView(TextView textView, String label, TextType textType) {
        textView.setText(label);
        textView.setTypeface(FontsHolder.getHoma(textView.getContext()));

        switch (textType) {
            case TITLE: {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, LengthManager.getPackagePurchaseTitleSize());
                textView.setTextColor(Color.WHITE);
                textView.setShadowLayer(1, 2, 2, Color.BLACK);
                break;
            }
            case DESCRIPTION: {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, LengthManager.getPackagePurchaseDescriptionSize());
                textView.setTextColor(Color.GRAY);
                break;
            }
            case ITEM_TEXT: {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, LengthManager.getPackagePurchaseItemTextSize());
                textView.setTextColor(Color.WHITE);
                textView.setShadowLayer(1, 2, 2, Color.BLACK);
                break;
            }
        }
    }

    private void createPackagePurchaseDialog(MetaPackage metaPackage) {
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout packagePurchase = (LinearLayout) inflater.inflate(R.layout.view_package_purchase, null);
        activity.pushToViewStack(packagePurchase, true);

        LinearLayout dialog = (LinearLayout) packagePurchase.findViewById(R.id.dialog);

        {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) dialog.getLayoutParams();
            layoutParams.leftMargin = layoutParams.rightMargin = layoutParams.topMargin = layoutParams.bottomMargin = LengthManager.getStoreDialogMargin();
        }

        {
            int padding = LengthManager.getLevelFinishedDialogPadding();
            dialog.setPadding(padding, padding, padding, padding);
        }

        {
            TextView title = (TextView) dialog.findViewById(R.id.title);
            customizeTextView(title, (String) title.getText(), TextType.TITLE);
        }

        {
            TextView description = (TextView) dialog.findViewById(R.id.description);
            customizeTextView(description, (String) description.getText(), TextType.DESCRIPTION);
        }

        {
            View purchaseFromBazaar = dialog.findViewById(R.id.purchase_from_bazaar);
            setupStoreItem(purchaseFromBazaar, "خرید از بازار", "۱۲۳۴", R.drawable.single_button_green, true);
        }

        {
            View purchaseByCoin = dialog.findViewById(R.id.purchase_by_coin);
            setupStoreItem(purchaseByCoin, "خرید با سکه", "۱۲۳۴", R.drawable.single_button_red, false);
        }

        Utils.setViewBackground(dialog, new DialogDrawable(activity));
    }

    private void setupStoreItem(View storeItem, String label, String price, int resourceId, boolean reversed) {
        {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) storeItem.getLayoutParams();
            layoutParams.width = LengthManager.getPackagePurchaseItemWidth();
            layoutParams.height = LengthManager.getPackagePurchaseItemHeight();
            layoutParams.gravity = Gravity.CENTER;
        }

        if (reversed) {
            LinearLayout textViews = (LinearLayout) storeItem.findViewById(R.id.text_views);
            Utils.reverseLinearLayout(textViews);
        }

        {
            TextView itemLabel = (TextView) storeItem.findViewById(R.id.label);
            customizeTextView(itemLabel, label, TextType.ITEM_TEXT);
        }

        {
            TextView itemPrice = (TextView)  storeItem.findViewById(R.id.price);
            customizeTextView(itemPrice, price, TextType.ITEM_TEXT);
        }

        {
            ImageView background = (ImageView) storeItem.findViewById(R.id.item_background);
            background.setImageBitmap(ImageManager.loadImageFromResource(activity, resourceId, LengthManager.getPackagePurchaseItemWidth(), -1));
        }
    }
}

class DownloadCancelAlert extends android.support.v4.app.DialogFragment {
    private MetaPackage metaPackage;
    public DownloadCancelAlert(MetaPackage metaPackage) {
        this.metaPackage = metaPackage;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("از کرده خود پشیمانی؟")
                .setPositiveButton("بلی", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        metaPackage.setIsDownloading(false);
                    }
                })
                .setNegativeButton("خیر", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
        return builder.create();
    }
}
