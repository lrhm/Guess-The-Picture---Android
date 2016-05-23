package ir.treeco.aftabe.Adapter;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import ir.treeco.aftabe.API.Rest.AftabeAPIAdapter;
import ir.treeco.aftabe.API.Rest.Interfaces.OnPackageBuyListener;
import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.Object.PackageObject;
import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.DownloadTask;
import ir.treeco.aftabe.Util.FontsHolder;
import ir.treeco.aftabe.Util.LengthManager;
import ir.treeco.aftabe.Util.PackageTools;
import ir.treeco.aftabe.Util.SizeManager;
import ir.treeco.aftabe.Util.Tools;
import ir.treeco.aftabe.Util.UiUtil;
import ir.treeco.aftabe.View.Activity.MainActivity;
import ir.treeco.aftabe.View.Custom.ToastMaker;
import ir.treeco.aftabe.View.Dialog.CustomAlertDialog;
import ir.treeco.aftabe.View.Dialog.PackagePurchaseDialog;
import ir.treeco.aftabe.View.Fragment.PackageFragment;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.ViewHolder> {
    private static final String TAG = "PackageAdapter";
    private ArrayList<PackageObject> packageObjects;
    private Activity context;
    private Tools tools;
    private LengthManager lengthManager;

    public PackageAdapter(Activity context, PackageObject[] packageObjectss) {
        this.context = context;
        this.packageObjects = new ArrayList<>();
        Collections.addAll(packageObjects, packageObjectss);
        tools = new Tools(context);
        lengthManager = ((MainApplication) context.getApplicationContext()).getLengthManager();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, DownloadTask.DownloadTaskListener {
        ImageView imageView;
        TextView textView;
        ImageView packagePrice;
        TextView price;

        private long lastTimeClicked = 0;
        private long timeStamp = 1000;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) itemView.findViewById(R.id.itemPackage);
            textView = (TextView) itemView.findViewById(R.id.package_progress_dl);
            price = (TextView) itemView.findViewById(R.id.package_coin_text);
            packagePrice = (ImageView) itemView.findViewById(R.id.package_coin_image);

            if (SizeManager.getScreenWidth() < 800)
                textView.setShadowLayer(0.5f, 1, 1, Color.BLACK);
            textView.setTypeface(FontsHolder.getNumeralSansMedium(v.getContext()));
            UiUtil.setTextViewSize(textView, (int) (SizeManager.getScreenWidth() * 0.3), 0.4f);

            if (SizeManager.getScreenWidth() < 800)
                price.setShadowLayer(0.5f, 1, 1, Color.BLACK);
            price.setTypeface(FontsHolder.getNumeralSansMedium(v.getContext()));
            UiUtil.setTextViewSize(price, (int) (SizeManager.getScreenWidth() * 0.3), 0.15f);


            int packageSize = (int) (SizeManager.getScreenWidth() * 0.47);
            imageView.getLayoutParams().height = packageSize;
            imageView.getLayoutParams().width = packageSize;

            UiUtil.setWidth(packagePrice, packageSize);
            UiUtil.setHeight(packagePrice, packageSize);

            int textSize = (int) (packageSize * 0.11);
            UiUtil.setLeftMargin(price, textSize);
            UiUtil.setTopMargin(price, (int) (packageSize * 0.09));
            UiUtil.setWidth(price, (int) (packageSize * 0.22));
            UiUtil.setHeight(price, (int) (packageSize * 0.22));


            Picasso.with(context).load(R.drawable.package_price).fit().into(packagePrice);

            price.setText(Tools.numeralStringToPersianDigits("2000"));
            v.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {

            if (System.currentTimeMillis() - lastTimeClicked < timeStamp)
                return;

            lastTimeClicked = System.currentTimeMillis();

            final int id = packageObjects.get(getAdapterPosition()).getId();
            final PackageObject packageObject = packageObjects.get(getAdapterPosition());

            File file = new File(context.getFilesDir().getPath() + "/Packages/package_" + id + "/");

            if (!file.exists()) {


                final CoinAdapter coinAdapter = ((MainActivity) context).getCoinAdapter();


                new PackagePurchaseDialog(context, packageObject).setYesClick(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (coinAdapter.spendCoins(packageObject.getPrice())) {
                            AftabeAPIAdapter.buyPackage(id, new OnPackageBuyListener() {
                                @Override
                                public void onPurchasedBefore() {
                                    //          coinAdapter.earnCoins(packageObject.getPrice());
                                }

                                @Override
                                public void onPurchaseSuccess() {

                                }
                            });
                            PackageTools.getInstance(context).downloadPackage(packageObject, ViewHolder.this);
                            ToastMaker.show(context, "درحال دانلود....", Toast.LENGTH_SHORT);
                        }
                    }
                }).show();


            } else {
                Bundle bundle = new Bundle();
                bundle.putInt("id", id);

                PackageFragment packageFragment = new PackageFragment();
                packageFragment.setArguments(bundle);

                FragmentTransaction transaction = ((MainActivity) context).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, packageFragment, "LevelsActivityNew1");
                transaction.addToBackStack(null);
                transaction.commitAllowingStateLoss();
            }
        }


        @Override
        public void onProgress(final int progress) {
            Log.d(TAG, "on progress" + progress);

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (textView.getVisibility() != View.VISIBLE)
                        textView.setVisibility(View.VISIBLE);
                    textView.setText(Tools.numeralStringToPersianDigits(progress + "") + "%");

                }
            });
        }

        @Override
        public void onDownloadSuccess() {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    ToastMaker.show(context, "دانلود شد", Toast.LENGTH_SHORT);

                    packagePrice.setVisibility(View.GONE);
                    price.setVisibility(View.GONE);
                    ColorMatrix matrix = new ColorMatrix();

                    ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                    imageView.setColorFilter(filter);

                    textView.setVisibility(View.GONE);
                }
            });

        }

        @Override
        public void onDownloadError(String error) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    ToastMaker.show(context, context.getResources().getString(R.string.try_later), Toast.LENGTH_SHORT);

                    textView.setVisibility(View.GONE);
                }
            });

        }
    }

    @Override
    public PackageAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_package, viewGroup, false);
        int packageSize = (int) (SizeManager.getScreenWidth() * 0.5);
        v.setLayoutParams(new RecyclerView.LayoutParams(
                packageSize,
                packageSize));

//        int padding = (int) (SizeManager.getScreenWidth() * 0.01 );
//
//        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) v.getLayoutParams();
//        params.leftMargin = padding * 2;
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PackageAdapter.ViewHolder viewHolder, int i) {


        int onePercent = (int) (SizeManager.getScreenWidth() * 0.013);

        if (i % 2 == 0) {
            viewHolder.imageView.setPadding(onePercent, 0, 0, 0);
            UiUtil.setLeftMargin(viewHolder.packagePrice, onePercent);
            viewHolder.price.setPadding(onePercent, 0, 0, 0);

        } else {

            viewHolder.imageView.setPadding(0, 0, onePercent, 0);


        }


        int id = packageObjects.get(i).getId();
        String imagePath = "file://" + context.getFilesDir().getPath() + "/package_" + id + "_" + "front" + ".png";
        Picasso.with(context).load(imagePath).fit().into(viewHolder.imageView);
        User myUser = Tools.getCachedUser(context);

        if ((myUser != null && myUser.isPackagePurchased(id))) {
            viewHolder.price.setText(Tools.numeralStringToPersianDigits("0"));

        }
        File file = new File(context.getFilesDir().getPath() + "/Packages/package_" + id + "/");
        if (!file.exists()) {
            viewHolder.packagePrice.setVisibility(View.VISIBLE);
            viewHolder.price.setVisibility(View.VISIBLE);
            viewHolder.price.setText(Tools.numeralStringToPersianDigits(packageObjects.get(i).getPrice() + ""));

            if ((myUser != null && myUser.isPackagePurchased(id))) {
                viewHolder.price.setText(Tools.numeralStringToPersianDigits("0"));

            }
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);

            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            viewHolder.imageView.setColorFilter(filter);

        }

        if (!(new File(context.getFilesDir().getPath() + "/package_" + id + "_" + "front" + ".png").exists())) {
            viewHolder.packagePrice.setVisibility(View.GONE);
            viewHolder.price.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        if (packageObjects == null)
            return 0;
        return packageObjects.size();
    }

    public void addPackage(PackageObject packageObject) {
        packageObjects.add(packageObject);
        notifyDataSetChanged();

    }
}
