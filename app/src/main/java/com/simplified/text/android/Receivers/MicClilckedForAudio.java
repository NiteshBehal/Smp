package com.simplified.text.android.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.IOException;

public class MicClilckedForAudio extends BroadcastReceiver {
    private MediaPlayer mediaPlayer;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!TextUtils.isEmpty(intent.getStringExtra("mp3Url"))) {
//            Toast.makeText(context, "Loading audio please wait...",Toast.LENGTH_SHORT).show();
            String mp3Url = "https://api.pearson.com" + intent.getStringExtra("mp3Url");
            try {
                playAudio(mp3Url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void playAudio(String mp3Url) throws IOException {
        killMediaPlayer();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(mp3Url);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.stop();
                killMediaPlayer();

            }
        });
        mediaPlayer.prepare();
        mediaPlayer.start();

    }

    private void killMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
