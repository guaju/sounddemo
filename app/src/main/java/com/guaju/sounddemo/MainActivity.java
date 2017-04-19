package com.guaju.sounddemo;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    public static final int STATUS_PLAYING = 2;
    private static final int STATUS_PAUSE = 0;
    private static final int STATUS_RELEASE = 1;
    private int soundId;
    private float volume;
    private float playbackSpeed=1.0f;
    private SoundPool soundPool;
    private MediaPlayer mp;
    private int play_status=-1;
    private int play;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        //不能直接播放网络音频，且如果你的音频大于1m那么不能播放，必须是小类型的音频才可以
        soundId= soundPool.load(this,R.raw.hcy,1);
//        soundId=soundPool.load(path,1);
        AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        volume = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("正在加载");
        alertDialog = builder.create();
        alertDialog.setCancelable(false);


    }


    public void play(View v){

        if (-1==play_status) {
            alertDialog.show();
            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                    if (alertDialog.isShowing()){
                    alertDialog.dismiss();
                    }
                    play = soundPool.play(soundId, volume, volume, 1, 0, playbackSpeed);
                    play_status=STATUS_PLAYING;
                }
            });
        } else if (STATUS_RELEASE==play_status&&soundPool==null){
            alertDialog.show();

            soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
            soundId= soundPool.load(this,R.raw.hcy,1);
            AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            volume = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                    if (alertDialog.isShowing()){
                        alertDialog.dismiss();
                    }
                    play = soundPool.play(soundId, volume, volume, 1, 0, playbackSpeed);
                    play_status=STATUS_PLAYING;
                }
            });
        }

        else{
            soundPool.resume(play);
            play_status=STATUS_PLAYING;
        }

    }
    public void slowdown(View v){
        if (STATUS_PLAYING==play_status&&soundPool!=null){
        soundPool.autoPause();
        playbackSpeed-=0.1f;
        soundPool.setRate(play,playbackSpeed);
        soundPool.resume(play);    }else{
            soundPool.setRate(play,playbackSpeed);
            playbackSpeed-=0.1f;
        }
    }
    public void befast(View v){
        if (STATUS_PLAYING==play_status&&soundPool!=null) {
            soundPool.autoPause();
            playbackSpeed += 0.1f;
            soundPool.setRate(play,playbackSpeed);
            soundPool.resume(play);
        }
        else{
            soundPool.setRate(play,playbackSpeed);
            playbackSpeed+=0.1f;
        }
    }
    public void stop(View v){

        if (STATUS_PLAYING==play_status&&soundPool!=null){
        soundPool.stop(play);
        soundPool.release();
        soundPool=null;
        play_status=STATUS_RELEASE;  }
    }
    public void pause(View v){
        if (STATUS_PLAYING==play_status&&soundPool!=null){
        play_status=STATUS_PAUSE;
        soundPool.pause(play);         }

    }

}
