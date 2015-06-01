package ir.treeco.aftabe.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v4.view.PagerAdapter;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import ir.treeco.aftabe.R;
import ir.treeco.aftabe.View.Fragment.LevelFragment;
import ir.treeco.aftabe.View.Toast.ToastMaker;
import ir.treeco.aftabe.mutlimedia.Multimedia;
import ir.treeco.aftabe.packages.Level;
import ir.treeco.aftabe.utils.ImageManager;
import ir.treeco.aftabe.utils.LengthManager;

/**
 * Created by hamed on 10/8/14.
 */
public class MultimediaAdapter extends PagerAdapter {
    private static final String PLAY_TEXT = "►";
    private static final String STOP_TEXT = "■";
    private final LevelFragment fragment;
    private final LayoutInflater inflater;
    private final Multimedia[] multimedia;
    private final Level level;

    public MultimediaAdapter(LevelFragment fragment) {
        this.fragment = fragment;
        this.inflater = (LayoutInflater) fragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.multimedia = fragment.getMultimedia();
        this.level = fragment.getLevel();
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
    public Object instantiateItem(final ViewGroup container, final int position) {
        File file = new File(fragment.getActivity().getCacheDir(), "mm_" + position);
        View multimediaView;
        if (level.isResourceUnlocked(position)) {
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
                case AUDIO: {
                    RelativeLayout relativeLayout = new RelativeLayout(container.getContext());

                    ImageView imageView = new ImageView(container.getContext());
                    imageView.setImageBitmap(ImageManager.loadImageFromResource(container.getContext(), R.drawable.music_background, LengthManager.getLevelImageWidth(), LengthManager.getLevelImageHeight()));
                    
                    relativeLayout.addView(imageView);
                    
                    final TextView textView = new TextView(container.getContext());
                    textView.setGravity(Gravity.CENTER);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, LengthManager.getPlayStopButtonFontSize());
                    textView.setTextColor(Color.argb(200, 200, 200, 200));
                    textView.setText(PLAY_TEXT);

                    {
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LengthManager.getVideoPlayButtonSize(), LengthManager.getVideoPlayButtonSize());
                        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

                        relativeLayout.addView(textView, layoutParams);
                    }

                    final MediaPlayer mediaPlayer;

                    try {
                        FileInputStream fileInputStream = new FileInputStream(file);
                        FileDescriptor fileDescriptor = fileInputStream.getFD();
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setDataSource(fileDescriptor);
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            textView.setText(PLAY_TEXT);
                        }
                    });

                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mediaPlayer.isPlaying()) {
                                mediaPlayer.pause();
                                textView.setText(PLAY_TEXT);
                            } else {
                                mediaPlayer.seekTo(0);
                                mediaPlayer.start();
                                textView.setText(STOP_TEXT);
                            }
                        }
                    });

                    multimediaView = relativeLayout;
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
        } else {
            ImageView imageView = new ImageView(container.getContext());
            imageView.setImageBitmap(ImageManager.loadImageFromResource(container.getContext(), R.drawable.lock, LengthManager.getResourceLockWidth(), -1));
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setBackgroundColor(Color.parseColor("#FF9900"));

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (position > 0 && !level.isResourceUnlocked(position - 1)) {
                        ToastMaker.show(container.getContext(), "اول باید قبلی‌ها رو باز کنی!", Toast.LENGTH_SHORT);
                        return;
                    }
                    level.unlockResource(position);
                    notifyDataSetChanged();
                }
            });

            multimediaView = imageView;
        }
        container.addView(multimediaView);
        return multimediaView;
    }

    @Override
    synchronized public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
