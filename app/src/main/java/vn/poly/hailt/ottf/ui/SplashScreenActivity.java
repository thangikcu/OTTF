package vn.poly.hailt.ottf.ui;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import vn.poly.hailt.ottf.R;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ImageView imgSpeech = findViewById(R.id.imgSpeech);
        ObjectAnimator animator = ObjectAnimator.ofFloat(imgSpeech, "alpha", 0f, 1f);
        animator.setDuration(1500);
        animator.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                finish();
            }
        }, 3000);
    }

    @Override
    public void onBackPressed() {

    }

}

