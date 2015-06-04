package ir.treeco.aftabe.New.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ir.treeco.aftabe.New.View.Activity.GameActivity;
import ir.treeco.aftabe.New.View.Activity.MainActivity;
import ir.treeco.aftabe.R;

public class LevelsAdapter extends RecyclerView.Adapter<LevelsAdapter.ViewHolder> {
    Context context;
    int page;
    int packageNumber;

    public LevelsAdapter(Context context, int packageNumber, int page) {
        this.context = context;
        this.page = page;
        this.packageNumber = packageNumber;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView textView;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) itemView.findViewById(R.id.itemLevel);
            textView = (TextView) itemView.findViewById(R.id.itemLevelText);

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
        if (MainActivity.downlodedObject.getDownloaded().get(packageNumber).getLevels().get(i).isResolved()) {
            viewHolder.textView.setText("" + MainActivity.downlodedObject.getDownloaded().get(packageNumber).getLevels().get(i).getJavab());
            String a = "file://" + context.getFilesDir().getPath() + "/Downloaded/"
                    + MainActivity.downlodedObject.getDownloaded().get(packageNumber).getId()
                    + "_" + MainActivity.downlodedObject.getDownloaded().get(packageNumber).getLevels().get(i).getResources();
            Log.e("tes", a);
            Picasso.with(context).load(a).into(viewHolder.imageView);
        } else Picasso.with(context).load(R.drawable.level_locked).into(viewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        if ( ( (MainActivity.downlodedObject.getDownloaded().get(packageNumber).getLevels().size() - (page * 16) ) / 16) >= 1) { //todo chek for 5 - 16 - 20 - 32 - 40
            return 16;
        } else
            return (MainActivity.downlodedObject.getDownloaded().get(packageNumber).getLevels().size() - (page * 16) ) % 16;
    }
}
