package ir.treeco.aftabe;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
        View multimediaView;
        switch (multimedia[position].getType()) {
            case IMAGE: {
                Bitmap bitmap;
                try {
                    InputStream is = new FileInputStream(file);
                    bitmap = ImageManager.loadImageFromInputStream(is, LengthManager.getLevelImageWidth(), LengthManager.getLevelImageHeight());
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                ImageView imageView = new ImageView(container.getContext());
                imageView.setImageBitmap(bitmap);
                multimediaView = imageView;
                break;
            }
            case VIDEO: {
                RelativeLayout relativeLayout = new RelativeLayout(container.getContext());

                final VideoView videoView = new VideoView(container.getContext());
                videoView.setVideoPath(file.getPath());
                videoView.seekTo(1);

                {
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

                    relativeLayout.addView(videoView, layoutParams);
                }

                final ImageView imageView = new ImageView(container.getContext());
                imageView.setImageBitmap(ImageManager.loadImageFromResource(container.getContext(), R.drawable.play_button, LengthManager.getVideoPlayButtonSize(), LengthManager.getVideoPlayButtonSize()));
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        videoView.start();
                        imageView.setVisibility(View.INVISIBLE);
                    }
                });

                videoView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        videoView.pause();
                        videoView.seekTo(1);
                        imageView.setVisibility(View.VISIBLE);
                        return true;
                    }
                });

                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        videoView.seekTo(1);
                        imageView.setVisibility(View.VISIBLE);
                    }
                });

                {
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LengthManager.getVideoPlayButtonSize(), LengthManager.getVideoPlayButtonSize());
                    layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

                    relativeLayout.addView(imageView, layoutParams);
                }

                multimediaView = relativeLayout;
                break;
            }
            default: {
                throw new RuntimeException("Unsupported Multimedia Type");
            }
        }
        container.addView(multimediaView);
        return multimediaView;
    }

    @Override
    synchronized public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
