package ir.treeco.aftabe.Adapter;

import android.app.Activity;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import ir.treeco.aftabe.API.AftabeAPIAdapter;
import ir.treeco.aftabe.API.OnPackageBuyListener;
import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.Object.PackageObject;
import ir.treeco.aftabe.Object.StoreItemHolder;
import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.LengthManager;
import ir.treeco.aftabe.Util.PackageTools;
import ir.treeco.aftabe.Util.SizeConverter;
import ir.treeco.aftabe.Util.SizeManager;
import ir.treeco.aftabe.Util.Tools;
import ir.treeco.aftabe.View.Activity.MainActivity;
import ir.treeco.aftabe.View.Custom.ToastMaker;
import ir.treeco.aftabe.View.Dialog.CustomAlertDialog;
import ir.treeco.aftabe.View.Dialog.DialogAdapter;
import ir.treeco.aftabe.View.Fragment.PackageFragment;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.ViewHolder> {
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PackageTools.OnDownloadSuccessListener {
        ImageView imageView;
        private long lastTimeClicked = 0;
        private long timeStamp = 1000;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) itemView.findViewById(R.id.itemPackage);
            int packageSize = (int) (SizeManager.getScreenWidth() * 0.47);
            imageView.getLayoutParams().height = packageSize;
            imageView.getLayoutParams().width = packageSize;


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

                User myUser = ((MainActivity) context).getMyUser();
                if (myUser == null)
                    myUser = Tools.getCachedUser();

                if (packageObject.getPrice() == 0 || (myUser != null && myUser.getPackages() != null && myUser.getPackages().contains(id))) {
                    ToastMaker.show(context, "درحال دانلود....", Toast.LENGTH_SHORT);
                    PackageTools.getInstance(context).downloadPackage(packageObject, this);

                } else {
                    final CoinAdapter coinAdapter = ((MainActivity) context).getCoinAdapter();


                    String firstLine = String.format("%s %s", "خرید پکیج", packageObject.getName());
                    String secondLine = String.format("%s %s %s", "فقط", Tools.numeralStringToPersianDigits(packageObject.getPrice() + ""), "سکه");
                    String msg = String.format("%s\n%s", firstLine, secondLine);
                    new CustomAlertDialog(context, msg, "باشه", new View.OnClickListener() {
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
                    }, "نمیخرم", null).show();

                }

            } else {
                Bundle bundle = new Bundle();
                bundle.putInt("id", id);

                PackageFragment packageFragment = new PackageFragment();
                packageFragment.setArguments(bundle);

                FragmentTransaction transaction = ((MainActivity) context).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, packageFragment, "LevelsActivityNew1");
                transaction.addToBackStack(null);
                transaction.commit();
            }
        }

        @Override
        public void onDownload(PackageObject packageObject) {

            ToastMaker.show(context, "دانلود شد", Toast.LENGTH_SHORT);


            ColorMatrix matrix = new ColorMatrix();

            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            imageView.setColorFilter(filter);


        }
    }

    @Override
    public PackageAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_package, viewGroup, false);
        int packageSize = (int) (SizeManager.getScreenWidth() * 0.47);
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

        int id = packageObjects.get(i).getId();
        String imagePath = "file://" + context.getFilesDir().getPath() + "/package_" + id + "_" + "front" + ".png";
        Picasso.with(context).load(imagePath).fit().into(viewHolder.imageView);


        File file = new File(context.getFilesDir().getPath() + "/Packages/package_" + id + "/");
        if (!file.exists()) {

            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);

            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            viewHolder.imageView.setColorFilter(filter);

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
