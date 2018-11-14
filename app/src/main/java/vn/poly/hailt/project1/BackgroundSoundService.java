package vn.poly.hailt.project1;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import java.util.Random;

public class BackgroundSoundService extends Service {

    private MediaPlayer media;

    public BackgroundSoundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
//        super.onCreate();
        media = MediaPlayer.create(getApplicationContext(), R.raw.soundtrack);
        media.setLooping(true);
    }

    @Override
    public void onDestroy() {
        media.stop();
        media.release();
        stopSelf();
        super.onDestroy();

    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        media.seekTo(new Random().nextInt(media.getDuration()));
        Log.e("TIME", String.valueOf(new Random().nextInt(media.getDuration())));
        media.start();
    }
}
