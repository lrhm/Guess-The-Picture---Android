package ir.treeco.aftabe2.Adapter;

import android.content.Context;
import android.media.MediaPlayer;

import ir.treeco.aftabe2.R;

/**
 * Created by al on 5/20/16.
 */
public class MediaAdapter {

    private static MediaAdapter instance;
    private static Object lock = new Object();

    public static MediaAdapter getInstance(Context context) {
        synchronized (lock) {
            if (instance == null)
                instance = new MediaAdapter(context);
            return instance;
        }
    }

    private Context mContext;

    private MediaAdapter(Context context) {
        mContext = context;
    }

    public void playButtonSound() {
        playAudio(R.raw.sound_button);
    }

    public void playCorrectSound() {
        playAudio(R.raw.sound_correct);

    }

    public void playLoseSound() {
        playAudio(R.raw.sound_lose);

    }

    public void playPurchaseSound() {
        playAudio(R.raw.sound_purchase);
    }

    private void playAudio(int rawId) {
        MediaPlayer mediaPlayer = MediaPlayer.create(mContext, rawId);
        mediaPlayer.start();

    }
}
