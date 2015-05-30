package ir.treeco.aftabe.New.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ir.treeco.aftabe.New.Object.PackageObject;
import ir.treeco.aftabe.New.View.Activity.LevelsActivityNew;
import ir.treeco.aftabe.R;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.ViewHolder>{
    private PackageObject[] packageObjects;
    Context context;

    public PackageAdapter(Context context, PackageObject[] packageObjects) {
        this.context = context;
        this.packageObjects = packageObjects;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView textView;
        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) itemView.findViewById(R.id.itemPackage);
            textView = (TextView) itemView.findViewById(R.id.itemPackageText);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent myIntent = new Intent(context, LevelsActivityNew.class);
            context.startActivity(myIntent);
        }
    }

    @Override
    public PackageAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.new_item_package, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PackageAdapter.ViewHolder viewHolder, int i) {
        viewHolder.textView.setText(packageObjects[i].getName());
    }

    @Override
    public int getItemCount() {
        if (packageObjects==null)
            return 0;
        return packageObjects.length;
    }
}
