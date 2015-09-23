package ir.treeco.aftabe.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.Object.Level;
import ir.treeco.aftabe.View.Activity.MainActivity;
import ir.treeco.aftabe.View.Fragment.GameFragment;
import ir.treeco.aftabe.R;

public class LevelsAdapter extends RecyclerView.Adapter<LevelsAdapter.ViewHolder> {
    private Context context;
    private int page;
    private int packageId;
    private Level[] levels;

    public LevelsAdapter(Context context, int packageId, int page, Level[] levels) {
        this.context = context;
        this.page = page;
        this.packageId = packageId;
        this.levels = levels;
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
            int levelPosition = page * 16 + getAdapterPosition();
            if (levelPosition == 0 || levels[levelPosition].isResolved() || levels[levelPosition - 1].isResolved()) {
                Bundle bundle = new Bundle();
                int levelID = levels[page * 16 + getAdapterPosition()].getId();
                bundle.putInt("LevelId", levelID);
                bundle.putInt("id", packageId);

                GameFragment gameFragment = new GameFragment();
                gameFragment.setArguments(bundle);

                FragmentTransaction transaction = ((MainActivity) context).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, gameFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        }
    }

    @Override
    public LevelsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_levels, viewGroup, false);
        v.setLayoutParams(new RecyclerView.LayoutParams(
                MainApplication.lengthManager.getLevelFrameWidth(),
                MainApplication.lengthManager.getLevelFrameHeight()));
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(LevelsAdapter.ViewHolder viewHolder, int position) {

        int myPadding = MainApplication.lengthManager.getLevelThumbnailPadding();
        viewHolder.imageView.setPadding(myPadding, myPadding, myPadding, myPadding);

        int levelPosition = page * 16 + position;
        if (levelPosition == 0 || levels[levelPosition].isResolved() || levels[levelPosition - 1].isResolved()) { //todo getLevels().get(i) ehtemalan in bayad ba page * 16 jam she

            String imagePath = "file://" + context.getFilesDir().getPath() + "/Downloaded/"
                    + packageId + "_" +levels[levelPosition].getResources();

            String frame = "file://" + context.getFilesDir().getPath() + "/Downloaded/"
                    + packageId + "_levelUnlocked.png";

            Picasso.with(context).load(imagePath).into(viewHolder.imageView);
            Picasso.with(context).load(frame).into(viewHolder.frame);

            viewHolder.imageView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.imageView.setVisibility(View.GONE);
            String frame = "file://" + context.getFilesDir().getPath() + "/Downloaded/"
                    + packageId + "_levelLocked.png";

            Picasso.with(context).load(frame).into(viewHolder.frame);
        }
    }

    @Override
    public int getItemCount() {
        if (((levels.length - (page * 16)) / 16) >= 1) { //todo test for 5 - 16 - 20 - 32 - 40
            return 16;
        } else
            return (levels.length - (page * 16)) % 16;
    }
}
