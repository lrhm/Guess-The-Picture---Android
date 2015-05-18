package ir.treeco.aftabe;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import ir.treeco.aftabe.View.Fragment.PackageFragment;
import ir.treeco.aftabe.utils.LengthManager;

class LevelsGridViewAdapter extends BaseAdapter {
    class Tag {
        ImageView frame;
        ImageView thumbnail;
    }

    private final int[] levelIDs;
    private final PackageFragment fragment;
    LayoutInflater inflater;

    public LevelsGridViewAdapter(int[] levelIDs, PackageFragment fragment) {
        this.levelIDs = levelIDs;
        this.fragment = fragment;
    }

    @Override
    public int getCount() {
        return LengthManager.getPageLevelCount();
    }

    @Override
    public View getView(int i, View frameLayout, ViewGroup viewGroup) {
        if (frameLayout == null) {
            if (inflater == null)
                inflater = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            frameLayout = inflater.inflate(R.layout.view_level_thumbnail, viewGroup, false);

            frameLayout.setLayoutParams(new GridView.LayoutParams(LengthManager.getLevelFrameWidth(), LengthManager.getLevelFrameHeight()));

            Tag tag = new Tag();

            tag.thumbnail = (ImageView) frameLayout.findViewById(R.id.thumbnail);
            tag.frame = (ImageView) frameLayout.findViewById(R.id.frame);

            int myPadding = LengthManager.getLevelThumbnailPadding();
            tag.thumbnail.setPadding(myPadding, myPadding, myPadding, myPadding);

            frameLayout.setTag(tag);
        }

        Tag tag = (Tag) frameLayout.getTag();

        tag.thumbnail = (ImageView) frameLayout.findViewById(R.id.thumbnail);
        tag.frame = (ImageView) frameLayout.findViewById(R.id.frame);

        final int levelID = levelIDs[i];

        if (levelID == -1) {
            frameLayout.setVisibility(View.INVISIBLE);
            return frameLayout;
        }

        frameLayout.setVisibility(View.VISIBLE);

        tag.thumbnail.setImageBitmap(null);

        if (fragment.getPackage().getLevel(levelID).isLocked()) {
            tag.thumbnail.setImageBitmap(null);
            tag.frame.setImageBitmap(fragment.getLevelLockedBitmap());
        } else {
            Log.e("GVAdapter", "" + levelID + ": " + fragment.getThumbnail(levelID));
            tag.thumbnail.setImageBitmap(fragment.getThumbnail(levelID));
            tag.frame.setImageBitmap(fragment.getLevelUnockedBitmap());
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
