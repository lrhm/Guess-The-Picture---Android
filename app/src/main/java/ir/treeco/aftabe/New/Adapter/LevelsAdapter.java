package ir.treeco.aftabe.New.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.New.View.Fragment.GameActivity;
import ir.treeco.aftabe.R;

public class LevelsAdapter extends RecyclerView.Adapter<LevelsAdapter.ViewHolder> {
    Context context;
    int page;
    int packageNumber;
//    Bitmap levelLocked;
//    Bitmap levelUnlocked;

    public LevelsAdapter(Context context, int packageNumber, int page) {
        this.context = context;
        this.page = page;
        this.packageNumber = packageNumber;
//        this.levelLocked = levelLocked;
//        this.levelUnlocked = levelUnlocked;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        ImageView frame;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) itemView.findViewById(R.id.itemLevel);
            frame = (ImageView) itemView.findViewById(R.id.itemLevel_frame);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, GameActivity.class);
            int a = MainApplication.downloadedObject.getDownloaded().get(packageNumber).getLevels().get(page * 16  + getAdapterPosition()).getId();
            intent.putExtra("id", a);
            intent.putExtra("packageNumber", packageNumber);
            context.startActivity(intent);

        }
    }

    @Override
    public LevelsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.new_item_levels, viewGroup, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(LevelsAdapter.ViewHolder viewHolder, int i) {
        int b = page * 16 + i;
//        if (MainActivity.downlodedObject.getDownloaded().get(packageNumber).getLevels().get(b).isResolved()) { //todo getLevels().get(i) ehtemalan in bayad ba page * 16 jam she

            String a = "file://" + context.getFilesDir().getPath() + "/Downloaded/"
                    + MainApplication.downloadedObject.getDownloaded().get(packageNumber).getId()
                    + "_" + MainApplication.downloadedObject.getDownloaded().get(packageNumber).getLevels().get(b).getResources();
//            Log.e("tes", a);
        Picasso.with(context).load(a).into(viewHolder.imageView);
        Picasso.with(context).load(R.drawable.level_unlocked).into(viewHolder.frame);
//            viewHolder.frame.setImageBitmap(levelUnlocked);
//        } else {
//            viewHolder.imageView.setImageBitmap(null);
//            viewHolder.frame.setImageBitmap(levelLocked);
//        }
    }

    @Override
    public int getItemCount() {
        if (((MainApplication.downloadedObject.getDownloaded().get(packageNumber).getLevels().size() - (page * 16)) / 16) >= 1) { //todo chek for 5 - 16 - 20 - 32 - 40
            return 16;
        } else
            return (MainApplication.downloadedObject.getDownloaded().get(packageNumber).getLevels().size() - (page * 16)) % 16;
    }
}
