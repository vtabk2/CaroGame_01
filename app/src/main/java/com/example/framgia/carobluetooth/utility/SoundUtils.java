package com.example.framgia.carobluetooth.utility;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.annotation.RawRes;

/**
 * Created by framgia on 12/08/2016.
 */
public class SoundUtils {
    private static MediaPlayer mMediaPlayer = new MediaPlayer();

    public static void playSound(Context context, @RawRes int resId, boolean isLooping) {
        stopSound();
        mMediaPlayer.reset();
        mMediaPlayer = MediaPlayer.create(context, resId);
        mMediaPlayer.setLooping(isLooping);
        mMediaPlayer.start();
    }

    public static void stopSound() {
        if (mMediaPlayer == null) return;
        if (mMediaPlayer.isPlaying()) mMediaPlayer.stop();
    }
}
