package ir.treeco.aftabe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import ir.treeco.aftabe.packages.Package;
import ir.treeco.aftabe.utils.ImageManager;
import ir.treeco.aftabe.utils.LengthManager;

/**
 * Created by hamed on 9/2/14.
 */
class LevelsGridViewAdapter extends BaseAdapter {
    private final int[] levelIDs;
    private final Package mPackage;

    public LevelsGridViewAdapter(int[] levelIDs, Package mPackage) {
        this.levelIDs = levelIDs;
        this.mPackage = mPackage;
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
            frameLayout = (FrameLayout) inflater.inflate(R.layout.level_thumbnail, null);
            //frameLayout.setLayoutParams(new GridView.LayoutParams((int) heightManager.getLevelThumbnailSize(), (int) heightManager.getLevelThumbnailSize()));
            imageView = (ImageView) frameLayout.findViewById(R.id.thumbnailImageView);
            //int myPadding = (int) heightManager.convertLength(1);
            //imageView.setPadding(myPadding, myPadding, myPadding, myPadding);
            final ImageView cageView = (ImageView) frameLayout.findViewById(R.id.cageImageView);
            cageView.setImageResource(R.drawable.cage);
            //cageView.setImageBitmap(ImageManager.loadImageFromResource(BoxActivity.this, R.drawable.cage, (int) heightManager.getLevelThumbnailSize(), (int) heightManager.getLevelThumbnailSize()));
        } else {
            frameLayout = (FrameLayout) oldFrameLayout;
            imageView = (ImageView) frameLayout.findViewById(R.id.thumbnailImageView);
        }


        final int levelID = levelIDs[i];

        if (levelID != -1) {
            try {
                imageView.setImageBitmap(ImageManager.loadImageFromInputStream(mPackage.getLevel(levelID).getImage(), 100, 100));
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    /*public void killThreads() {
        for (int i: levelIDs)
            if (i != -1)
                ThumbnailCreator.cancelThread(i);
    }*/
}
