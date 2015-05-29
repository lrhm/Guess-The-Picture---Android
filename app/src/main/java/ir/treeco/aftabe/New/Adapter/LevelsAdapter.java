package ir.treeco.aftabe.New.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import ir.treeco.aftabe.New.Object.PackageObject;
import ir.treeco.aftabe.New.View.Activity.GameActivity;
import ir.treeco.aftabe.R;

public class LevelsAdapter extends RecyclerView.Adapter<LevelsAdapter.ViewHolder>{
    private ArrayList<PackageObject> packageObjects;
    Context context;

    public LevelsAdapter(Context context) {
        this.context = context;

        packageObjects = new ArrayList<>();
        for (int i = 0; i < 16 ; i ++) {
            PackageObject packageObject = new PackageObject();
            packageObject.setName("a" + i);
            packageObjects.add(packageObject);
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) itemView.findViewById(R.id.itemLevel);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent myIntent = new Intent(context, GameActivity.class);
            context.startActivity(myIntent);

        }
    }

    @Override
    public LevelsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.new_item_levels, viewGroup, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(LevelsAdapter.ViewHolder viewHolder, int i) {


    }

    @Override
    public int getItemCount() {
        return packageObjects.size();
    }

}
