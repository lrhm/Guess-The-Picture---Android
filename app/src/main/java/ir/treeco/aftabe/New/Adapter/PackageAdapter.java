package ir.treeco.aftabe.New.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import ir.treeco.aftabe.New.Object.PackageObject;
import ir.treeco.aftabe.R;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.ViewHolder>{
    private ArrayList<PackageObject> packageObjects;

    public PackageAdapter() {
        packageObjects = new ArrayList<>();
        for (int i = 0; i < 5 ; i ++) {
            PackageObject packageObject = new PackageObject();
            packageObject.setName("a" + i);
            packageObjects.add(packageObject);
        }

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

        }
    }

    @Override
    public PackageAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.new_item_package, viewGroup, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(PackageAdapter.ViewHolder viewHolder, int i) {


    }

    @Override
    public int getItemCount() {
        return packageObjects.size();
    }

}
