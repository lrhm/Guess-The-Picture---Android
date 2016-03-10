package ir.treeco.aftabe.Adapter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.Object.PackageObject;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.LengthManager;
import ir.treeco.aftabe.Util.Tools;
import ir.treeco.aftabe.View.Activity.MainActivity;
import ir.treeco.aftabe.View.Fragment.PackageFragment;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.ViewHolder> {
    private PackageObject[] packageObjects;
    private Activity context;
    private Tools tools;
    private LengthManager lengthManager;

    public PackageAdapter(Activity context, PackageObject[] packageObjects) {
        this.context = context;
        this.packageObjects = packageObjects;
        tools = new Tools(context);
        lengthManager = ((MainApplication) context.getApplicationContext()).getLengthManager();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) itemView.findViewById(R.id.itemPackage);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            File file = new File(context.getFilesDir().getPath() + "/p_" + packageObjects[getAdapterPosition()].getId() + ".zip");
            //todo chack md5

            if (!file.exists()) {
                tools.downloadPackage(
                        packageObjects[getAdapterPosition()].getUrl(),
                        context.getFilesDir().getPath(),
                        packageObjects[getAdapterPosition()].getId(),
                        packageObjects[getAdapterPosition()].getName());
            } else {
                Bundle bundle = new Bundle();
                bundle.putInt("id", packageObjects[getAdapterPosition()].getId());

                PackageFragment packageFragment = new PackageFragment();
                packageFragment.setArguments(bundle);

                FragmentTransaction transaction =  ((MainActivity)context).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, packageFragment, "LevelsActivityNew1");
                transaction.addToBackStack(null);
                transaction.commit();
            }
        }
    }

    @Override
    public PackageAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_package, viewGroup, false);
        v.setLayoutParams(new RecyclerView.LayoutParams(
                lengthManager.getPackageIconSize(),
                lengthManager.getPackageIconSize()));
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PackageAdapter.ViewHolder viewHolder, int i) {

        String imagePath = "file://" + context.getFilesDir().getPath() + "/" + "p_" + packageObjects[i].getId() + "_front" + ".png";
        Picasso.with(context).load(imagePath).into(viewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        if (packageObjects == null)
            return 0;
        return packageObjects.length;
    }
}
