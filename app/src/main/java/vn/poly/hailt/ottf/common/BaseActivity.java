package vn.poly.hailt.ottf.common;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.Locale;
import java.util.Random;

import vn.poly.hailt.ottf.R;
import vn.poly.hailt.ottf.util.KeyboardVisibilityEvent;

public abstract class BaseActivity extends AppCompatActivity implements KeyboardVisibilityEvent.KeyboardVisibilityEventListener {

    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutID());
        onCreateActivity(savedInstanceState);

        KeyboardVisibilityEvent.setEventListener(this, this);
    }

    protected abstract int getLayoutID();

    protected abstract void onCreateActivity(Bundle savedInstanceState);


    @Override
    public void onVisibilityChanged(boolean isOpen) {
//        Toast.makeText(this, "keyboard show: " + isOpen, Toast.LENGTH_SHORT).show();
        // TODO: 09/03/2019 override if need
    }

    public void initTextToSpeech() {
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                    textToSpeech.setSpeechRate(.8f);
                }
            }
        });
    }

    public void speakText(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    public int randomColor() {
        int[] colors = getResources().getIntArray(R.array.colors);
        return colors[new Random().nextInt(colors.length)];
    }

    public void tvEnglishInMainImage(ViewGroup containerMainImage, String vietnamese) {
        TextView textView = new TextView(this);
        textView.setText(vietnamese);
        textView.setTextColor(randomColor());
        textView.setTextSize(32);
        textView.setPadding(8, 8, 8, 8);
        textView.setGravity(Gravity.CENTER);
        containerMainImage.addView(textView);
    }

    public void playSoundEffect(MediaPlayer mediaPlayer, int sound) {
        mediaPlayer.reset();
        mediaPlayer = MediaPlayer.create(getApplicationContext(), sound);
        mediaPlayer.start();
    }

    public void changeHeart(TextView tvHeart, int heart) {
        Animation zoom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in_and_out);
        tvHeart.startAnimation(zoom);
        tvHeart.setText(String.valueOf(heart));
    }

    public void changeScore(TextView tvScore, int score) {
        Animation zoom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in_and_out);
        tvScore.startAnimation(zoom);
        tvScore.setText(String.valueOf(score));
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

}
