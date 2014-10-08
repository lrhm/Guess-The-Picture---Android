package ir.treeco.aftabe;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;
import ir.treeco.aftabe.mutlimedia.Multimedia;
import ir.treeco.aftabe.utils.ImageManager;
import ir.treeco.aftabe.utils.LengthManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by hamed on 10/8/14.
 */
public class MultimediaAdapter extends PagerAdapter {
    private final LevelFragment fragment;
    private final LayoutInflater inflater;
    private final Multimedia[] multimedia;

    public MultimediaAdapter(LevelFragment fragment) {
        this.fragment = fragment;
        this.inflater = (LayoutInflater) fragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.multimedia = fragment.getMultimedia();
    }

    @Override
    public int getCount() {
        return multimedia.length;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        File file = new File(fragment.getActivity().getCacheDir(), "mm_" + position);
        switch (multimedia[position].getType()) {
            case IMAGE:
                Bitmap bitmap;
                try {
                    InputStream is = new FileInputStream(file);
                    bitmap = ImageManager.loadImageFromInputStream(is, LengthManager.getLevelImageWidth(), LengthManager.getLevelImageHeight());
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                ImageView imageView = new ImageView(container.getContext());
                imageView.setImageBitmap(bitmap);
                container.addView(imageView);
                return imageView;
            case VIDEO:
                VideoView videoView = new VideoView(container.getContext());
                videoView.setVideoPath(file.getPath());
                videoView.start();
                container.addView(videoView);
                return videoView;
            default:
                throw new RuntimeException("Unsupported Multimedia Type");
        }
    }

    @Override
    synchronized public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
