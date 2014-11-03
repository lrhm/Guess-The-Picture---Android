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
        ImageView imageView;

        if (oldFrameLayout == null) {
            frameLayout = (FrameLayout) inflater.inflate(R.layout.view_level_thumbnail, null);
            frameLayout.setLayoutParams(new GridView.LayoutParams(LengthManager.getLevelFrameWidth(), LengthManager.getLevelFrameHeight()));
            imageView = (ImageView) frameLayout.findViewById(R.id.thumbnail);
            int myPadding = LengthManager.getLevelThumbnailPadding();
            imageView.setPadding(myPadding, myPadding, myPadding, myPadding);

            final ImageView frame = (ImageView) frameLayout.findViewById(R.id.frame);
            //frame.setImageBitmap(ImageManager.loadImageFromResource(viewGroup.getContext(), R.drawable.level_unlocked, LengthManager.getLevelFrameWidth(), LengthManager.getLevelFrameHeight()));
            frame.setImageBitmap(fragment.getLevelUnockedBitmap());
        } else {
            frameLayout = (FrameLayout) oldFrameLayout;
            imageView = (ImageView) frameLayout.findViewById(R.id.thumbnail);
        }


        final int levelID = levelIDs[i];

        if (levelID != -1) {
            imageView.setImageBitmap(fragment.getThumbnail(levelID));
/*
            try {
                //imageView.setImageBitmap(ImageManager.loadImageFromInputStream(mPackage.getLevel(levelID).getImage(), LengthManager.getLevelFrameWidth(), LengthManager.getLevelFrameHeight()));
            } catch (Exception e) {
                e.printStackTrace();
            }
*/
            /*if (LevelDataOrganizer.getLevel(levelID).isLocked(preferences))
                imageView.setImageBitmap(ImageManager.loadImageFromResource(BoxActivity.this, R.drawable.lock, (int) heightManager.getLevelThumbnailSize(), (int) heightManager.getLevelThumbnailSize()));
            else {
                imageView.setImageResource(android.R.color.transparent);
                LevelDataOrganizer.getLevel(levelID).createAndReplaceThumbnail(BoxActivity.this, (int) heightManager.getLevelThumbnailSize(), imageView);
            }*/

            // TODO add level image
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
