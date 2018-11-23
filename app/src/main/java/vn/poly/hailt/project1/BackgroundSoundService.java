package vn.poly.hailt.project1;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class BackgroundSoundService extends Service {

    private MediaPlayer media;
    private Timer timer;

    public BackgroundSoundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        media = MediaPlayer.create(getApplicationContext(), R.raw.bg_sound);
        media.setLooping(true);
    }

    @Override
    public void onDestroy() {
        timer.cancel();
        timer.purge();

        media.stop();
        media.release();
        stopSelf();
        super.onDestroy();

    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        startFadeIn();
        media.seekTo(new Random().nextInt(media.getDuration()));
        media.start();
    }

    float volume = 0;

    private void startFadeIn() {
        final int FADE_DURATION = 1000; //The duration of the fade
        //The amount of time between volume changes. The smaller this is, the smoother the fade
        final int FADE_INTERVAL = 250;
        final int MAX_VOLUME = 1; //The volume will increase from 0 to 1
        int numberOfSteps = FADE_DURATION / FADE_INTERVAL; //Calculate the number of fade steps
        //Calculate by how much the volume changes each step
        final float deltaVolume = MAX_VOLUME / (float) numberOfSteps;

        //Create a new Timer and Timer task to run the fading outside the main UI thread
        timer = new Timer(true);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                fadeInStep(deltaVolume); //Do a fade step
                //Cancel and Purge the Timer if the desired volume has been reached
                if (volume >= 1f) {
                    timer.cancel();
                    timer.purge();
                }
            }
        };

        timer.schedule(timerTask, 0, FADE_INTERVAL);
    }

    private void fadeInStep(float deltaVolume) {
        media.setVolume(volume, volume);
        volume += deltaVolume;
    }
}
