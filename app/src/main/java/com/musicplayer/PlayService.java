package com.musicplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

public class PlayService extends Service {
    public static final String COMMAND_KEY = "COMMAND";
    public static final int NO_COMMAND = 0;
    public static final int PREV_COMMAND = 1;
    public static final int PLAY_COMMAND = 2;
    //public static final int PAUSE_COMMAND = 3;
    public static final int NEXT_COMMAND = 3;
    private static final String TAG = "PlayService";
    private MediaPlayer mMediaPlayer;
    private IBinder mBinder = new LocalBinder();
    private int totalDuration;
    private ArrayList<Song> songList = new ArrayList<>();
    private int mSize;
    private int mCurrentPosition;
    private int mMyId = 1442;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "intent received " + intent.getIntExtra(COMMAND_KEY, NO_COMMAND));
        switch (intent.getIntExtra(COMMAND_KEY, NO_COMMAND)) {
            case PREV_COMMAND:
                if (mCurrentPosition == 0) {
                    mCurrentPosition = mSize;
                }
                mCurrentPosition = mCurrentPosition - 1;
                playMusic();
                break;
            case PLAY_COMMAND:
                if(mMediaPlayer.isPlaying())
                    stopMusic();
                else
                    playMusic();
                break;
//            case PAUSE_COMMAND:
//                stopMusic();
//                break;
            case NEXT_COMMAND:
                if (mCurrentPosition == mSize - 1) {
                    mCurrentPosition = -1;
                }
                mCurrentPosition = mCurrentPosition + 1;
                playMusic();
                break;
        }


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {
        if (mMediaPlayer != null) mMediaPlayer.release();
    }

    public void playMusic() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }

        Song song = songList.get(mCurrentPosition);
        mMediaPlayer.reset();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(song.songData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mMediaPlayer.prepare();
            totalDuration = mMediaPlayer.getDuration();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();
        setNotification();

    }
    public void pauseMusic(){
        mMediaPlayer.pause();
    }

    public void stopMusic() {
        mMediaPlayer.stop();
    }

    public int getTotalDuration() {
        return totalDuration;
    }

    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    public void getSongPosition(int curentPositon) {
        mMediaPlayer.seekTo(curentPositon);
    }

    public void resetSongPosition() {
        mMediaPlayer.seekTo(0);
    }

    public class LocalBinder extends Binder {
        public PlayService getService() {
            return PlayService.this;
        }
    }

    private void setNotification() {
        Song song = songList.get(mCurrentPosition);

        Intent prevIntent = new Intent(this, PlayService.class);
        prevIntent.putExtra(COMMAND_KEY, PREV_COMMAND);
        PendingIntent prevPendingIntent = PendingIntent.getService(this, 1, prevIntent, PendingIntent.FLAG_ONE_SHOT);

        Intent playIntent = new Intent(this, PlayService.class);
        playIntent.putExtra(COMMAND_KEY, PLAY_COMMAND);
        PendingIntent playPendingIntent = PendingIntent.getService(this, 2, playIntent, PendingIntent.FLAG_ONE_SHOT);

//        Intent pauseIntent = new Intent(this, PlayService.class);
//        pauseIntent.putExtra(COMMAND_KEY, PAUSE_COMMAND);
//        PendingIntent pausePendingIntent = PendingIntent.getService(this, 3, pauseIntent, PendingIntent.FLAG_ONE_SHOT);

        Intent nextIntent = new Intent(this, PlayService.class);
        nextIntent.putExtra(COMMAND_KEY, NEXT_COMMAND);
        PendingIntent nextPendingIntent = PendingIntent.getService(this, 4, nextIntent, PendingIntent.FLAG_ONE_SHOT);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.mediaplayer)
                .addAction(R.drawable.playprevious1, "Previous", prevPendingIntent)
                .addAction(R.drawable.playsmall, "Play", playPendingIntent)
                .addAction(R.drawable.playnext1, "Next", nextPendingIntent)
                .setContentTitle(song.title)
                .setContentText(song.desc)
                .setPriority(2)
                .setStyle(new NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(1)).build();
        startForeground(mMyId, notification);
    }

    public void setSongs(ArrayList<Song> songList) {
        this.songList = songList;
        mSize = songList.size();
    }

    public void setCurrentPosition(int curentPosition) {
        this.mCurrentPosition = curentPosition;
    }
}
