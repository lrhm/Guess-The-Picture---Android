package ir.treeco.aftabe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import ir.treeco.aftabe.utils.LengthManager;

/**
 * Created by hamed on 9/2/14.
 */
class LevelsGridViewAdapter extends BaseAdapter {
    private final int[] levelIDs;
    private final PackageFragment fragment;

    public LevelsGridViewAdapter(int[] levelIDs, PackageFragment fragment) {
        this.levelIDs = levelIDs;
        this.fragment = fragment;
    }

    @Override
    public int getCount() {
        return LengthManager.getPageLevelCount();
    }

    @Override
    public View getView(int i, View oldFrameLayout, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        FrameLayout frameLayout;
        ImageView thumbnail, frame;

        if (oldFrameLayout == null) {
            frameLayout = (FrameLayout) inflater.inflate(R.layout.view_level_thumbnail, null);
            frameLayout.setLayoutParams(new GridView.LayoutParams(LengthManager.getLevelFrameWidth(), LengthManager.getLevelFrameHeight()));
            thumbnail = (ImageView) frameLayout.findViewById(R.id.thumbnail);
            int myPadding = LengthManager.getLevelThumbnailPadding();
            thumbnail.setPadding(myPadding, myPadding, myPadding, myPadding);
        } else {
            frameLayout = (FrameLayout) oldFrameLayout;
        }

        thumbnail = (ImageView) frameLayout.findViewById(R.id.thumbnail);
        frame = (ImageView) frameLayout.findViewById(R.id.frame);

        final int levelID = levelIDs[i];

        if (levelID != -1) {
            frameLayout.setVisibility(View.VISIBLE);

            if (fragment.getPackage().getLevel(levelID).isLocked()) {
                thumbnail.setImageBitmap(null);
                frame.setImageBitmap(fragment.getLevelLockedBitmap());
            } else {
                thumbnail.setImageBitmap(fragment.getThumbnail(levelID));
                frame.setImageBitmap(fragment.getLevelUnockedBitmap());
            }
        } else {
            frameLayout.setVisibility(View.INVISIBLE);
        }

        return frameLayout;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
}
