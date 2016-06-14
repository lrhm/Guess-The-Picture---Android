package ir.treeco.aftabe2.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import ir.treeco.aftabe2.MainApplication;
import ir.treeco.aftabe2.Object.Level;
import ir.treeco.aftabe2.Util.LengthManager;
import ir.treeco.aftabe2.Util.SizeManager;
import ir.treeco.aftabe2.Util.UiUtil;
import ir.treeco.aftabe2.View.Activity.MainActivity;
import ir.treeco.aftabe2.View.Fragment.GameFragment;
import ir.treeco.aftabe2.R;

public class LevelsAdapter extends RecyclerView.Adapter<LevelsAdapter.ViewHolder> {
    public static final String OFFLINE_GAME_FRAGMENT_TAG = "Offline_Game_Fragment";
    private Context context;
    private int page;
    private int packageId;
    private Level[] levels;
    private LengthManager lengthManager;

    public LevelsAdapter(Context context, int packageId, int page, Level[] levels) {
        this.context = context;
        this.page = page;
        this.packageId = packageId;
        this.levels = levels;
        lengthManager = ((MainApplication) context.getApplicationContext()).getLengthManager();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        ImageView frame;

        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) itemView.findViewById(R.id.itemLevel);
            frame = (ImageView) itemView.findViewById(R.id.itemLevel_frame);
            int size = (int) (SizeManager.getScreenWidth() * 0.235);
            int myPadding = lengthManager.getLevelThumbnailPadding();

            imageView.getLayoutParams().width = size - myPadding;
            imageView.getLayoutParams().height = size - myPadding;

            UiUtil.setWidth(frame, size - myPadding);
            UiUtil.setHeight(frame, size - myPadding);

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
                transaction.replace(R.id.fragment_container, gameFragment , OFFLINE_GAME_FRAGMENT_TAG);
                transaction.addToBackStack(null);
                transaction.commitAllowingStateLoss();
            }
        }
    }

    @Override
    public LevelsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_levels, viewGroup, false);

        int size = (int) (SizeManager.getScreenWidth() * 0.235);
        v.setLayoutParams(new RecyclerView.LayoutParams(size, size));
//                lengthManager.getLevelFrameWidth(),
//                lengthManager.getLevelFrameHeight()));
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(LevelsAdapter.ViewHolder viewHolder, int position) {
        int myPadding = lengthManager.getLevelThumbnailPadding();
        viewHolder.imageView.setPadding(myPadding, myPadding, myPadding, myPadding);

        int levelPosition = page * 16 + position;
        if (levelPosition == 0 || levels[levelPosition].isResolved() || levels[levelPosition - 1].isResolved()) {

            String imagePath = "file://" + context.getFilesDir().getPath() + "/Packages/package_" + packageId + "/"
                    + levels[levelPosition].getResources();

//            String frame = "file://" + context.getFilesDir().getPath() + "/Downloaded/"
//                    + packageId + "_levelUnlocked.png";

            Picasso.with(context).load(imagePath).fit().centerCrop().into(viewHolder.imageView);
            Picasso.with(context).load(R.drawable.level_unlocked).into(viewHolder.frame);

            viewHolder.imageView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.imageView.setVisibility(View.GONE);
//            String frame = "file://" + context.getFilesDir().getPath() + "/Downloaded/"
//                    + packageId + "_levelLocked.png";

            Picasso.with(context).load(R.drawable.level_locked).into(viewHolder.frame);
        }
    }

    @Override
    public int getItemCount() {
        if (((levels.length - (page * 16)) / 16) >= 1) {
            return 16;
        } else
            return (levels.length - (page * 16)) % 16;
    }
}
