package ir.treeco.aftabe2.Adapter;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.HashMap;

import ir.treeco.aftabe2.R;

/**
 * Created by al on 5/20/16.
 */
public class MediaAdapter {

    private static MediaAdapter instance;
    private static Object lock = new Object();

    HashMap<Integer, MediaPlayer> map;

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
        map = new HashMap<>();
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

    public void playBomb() {

        int rawId = R.raw.bomb;

        MediaPlayer mediaPlayer = map.get(rawId);
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(mContext, rawId);
            map.put(rawId, mediaPlayer);
        }

        mediaPlayer.setLooping(true);
        mediaPlayer.start();

    }

    public boolean isBombPlaying() {
        int rawId = R.raw.bomb;
        MediaPlayer mediaPlayer = map.get(rawId);
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    public void pauseBomb() {
        int rawId = R.raw.bomb;

        MediaPlayer mediaPlayer = map.get(rawId);
        if (mediaPlayer != null) {
            mediaPlayer.stop();

        }
    }

    public void playEnemyCorrect() {

        playAudio(R.raw.enemy_correct);
    }


    public void playPurchaseSound() {
        playAudio(R.raw.sound_purchase);
    }

    private void playAudio(int rawId) {


        MediaPlayer mediaPlayer = map.get(rawId);
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(mContext, rawId);
            map.put(rawId, mediaPlayer);
        }
        mediaPlayer.start();
    }


    public void free() {

        for (MediaPlayer mediaPlayer : map.values()) {
            if (mediaPlayer.isPlaying())
                mediaPlayer.stop();
            mediaPlayer.release();

        }
        map.clear();

    }
}
