package vn.poly.hailt.project1.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import vn.poly.hailt.project1.Constant;
import vn.poly.hailt.project1.R;
import vn.poly.hailt.project1.adapter.DataAdapter;
import vn.poly.hailt.project1.model.Vocabulary;

public class PlayActivity extends AppCompatActivity implements Constant {

    private ImageView imgBack;

    private TextView tvHeart;
    private TextView tvScore;
    private CardView cvImage;
    private ImageView imgThing;
    private TextView tvVietnamese;
    private Button btnCaseA;
    private Button btnCaseB;
    private Button btnCaseC;
    private Button btnCaseD;
    private GridLayout grlCase;

    private Handler handler;
    private TextToSpeech tts;

    private List<Vocabulary> vocabularies;

    private int currentVocabulary = 0;
    private int score = 0;
    private int heart = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        handler = new Handler();

        initData();
        initViews();
        initTTS();
        initActions();
        initVocabulary();

        for (int button = 0; button < grlCase.getChildCount(); button++) {

            Button btnGuess = (Button) grlCase.getChildAt(button);
            btnGuess.setAllCaps(false);
            btnGuess.setTextColor(getResources().getColor(R.color.colorTextSecondary));
            btnGuess.setTextSize(16);
            btnGuess.setOnClickListener(btnGuessListener);
        }

    }

    private void initData() {
        DataAdapter dataAdapter = new DataAdapter(this);
        dataAdapter.createDatabase();
        dataAdapter.open();
        vocabularies = dataAdapter.getData();
        dataAdapter.close();
        Collections.shuffle(vocabularies);
    }

    private void initViews() {
        View iclHeader = findViewById(R.id.iclHeader);
        imgBack = iclHeader.findViewById(R.id.imgBack);
        TextView tvHeader = iclHeader.findViewById(R.id.tvHeader);
        tvHeader.setText(getIntent().getStringExtra("topic"));

        tvHeart = findViewById(R.id.tvHeart);
        tvScore = findViewById(R.id.tvScore);
        cvImage = findViewById(R.id.cvImage);
        imgThing = findViewById(R.id.imgThing);
        tvVietnamese = findViewById(R.id.tvVietnamese);
        btnCaseA = findViewById(R.id.btnCaseA);
        btnCaseB = findViewById(R.id.btnCaseB);
        btnCaseC = findViewById(R.id.btnCaseC);
        btnCaseD = findViewById(R.id.btnCaseD);
        grlCase = findViewById(R.id.grlCase);
    }

    private View.OnClickListener btnGuessListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button btnGuess = ((Button) v);
            String guessValue = btnGuess.getText().toString();
            String answerValue = vocabularies.get(currentVocabulary).english;
            animateAndSpeakButtonAnswer();

            if (guessValue.equals(answerValue)) {

                soundEffect(R.raw.correct_sound);
                ++score;
                changeScore();

            } else {

                soundEffect(R.raw.incorrect_sound);
                --heart;
                changeHeart();

                if (heart == 0) {
                    if (score > restoreHighScore()) {
                        saveHighScore(score);
                    }
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showGameOverDialog();
                        }
                    }, 2000);
                    return;
                }
            }

            if (currentVocabulary == vocabularies.size() - 1) {
                if (score > restoreHighScore()) {
                    saveHighScore(score);
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showGameOverDialog();
                    }
                }, 2000);
            } else {
                ++currentVocabulary;
                disableButtonGuess();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        List<View> views = Arrays.asList(cvImage, imgThing, tvVietnamese, grlCase);
                        nextVocabulary(views);
                    }
                }, 3000);

            }
        }
    };


    private void initActions() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            }
        });

    }

    private void initTTS() {
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.UK);
                }
            }
        });
    }

    private void initVocabulary() {
        Vocabulary vocabulary = vocabularies.get(currentVocabulary);

        Glide.with(this).load(vocabulary.imageLink).into(imgThing);
        tvVietnamese.setText(vocabulary.vietnamese);
        btnCaseA.setText(vocabulary.caseA);
        btnCaseB.setText(vocabulary.caseB);
        btnCaseC.setText(vocabulary.caseC);
        btnCaseD.setText(vocabulary.caseD);
    }

    private void nextVocabulary(List<View> views) {
        for (int i = 0; i < views.size(); i++) {
            ObjectAnimator objAnimFadeOut = ObjectAnimator.ofFloat(views.get(i), "alpha", 1f, 0f);
            objAnimFadeOut.setDuration(500);
            ObjectAnimator objAnimFadeIn = ObjectAnimator.ofFloat(views.get(i), "alpha", 0f, 1f);
            objAnimFadeIn.setDuration(500);
            AnimatorSet anim = new AnimatorSet();
            anim.play(objAnimFadeOut).before(objAnimFadeIn);
            anim.start();

            objAnimFadeOut.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    initVocabulary();
                    enableButtonGuess();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }

    }

    private void disableButtonGuess() {
        for (int button = 0; button < grlCase.getChildCount(); button++) {
            Button btnGuess = (Button) grlCase.getChildAt(button);
            btnGuess.setEnabled(false);
        }
    }

    private void enableButtonGuess() {
        for (int button = 0; button < grlCase.getChildCount(); button++) {
            Button btnGuess = (Button) grlCase.getChildAt(button);
            btnGuess.setEnabled(true);
            btnGuess.setBackgroundResource(R.drawable.btn_guess);
        }
    }

    private void animateAndSpeakButtonAnswer() {
        for (int button = 0; button < grlCase.getChildCount(); button++) {
            final Button btnGuess = (Button) grlCase.getChildAt(button);
            if (btnGuess.getText().toString().equals(vocabularies.get(currentVocabulary).english)) {
                ObjectAnimator anim = ObjectAnimator.ofInt(btnGuess, "backgroundResource",
                        R.drawable.btn_answer, R.drawable.btn_guess, R.drawable.btn_answer);
                anim.setDuration(400);
                anim.setInterpolator(new LinearInterpolator());
                anim.setRepeatMode(ValueAnimator.RESTART);
                anim.setRepeatCount(2);
                anim.start();
                anim.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        tts.speak(btnGuess.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

            }

        }
    }

    private void changeHeart() {
        Animation zoom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in_and_out);
        tvHeart.startAnimation(zoom);
        tvHeart.setText(String.valueOf(heart));
    }

    private void changeScore() {
        Animation zoom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in_and_out);
        tvScore.startAnimation(zoom);
        tvScore.setText(String.valueOf(score));
    }

    private void soundEffect(int sound) {
        MediaPlayer checkSound = MediaPlayer.create(getApplicationContext(), sound);
        checkSound.start();
    }

    private void showGameOverDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_game_over);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        TextView tvYourScore = dialog.findViewById(R.id.tvYourScore);
        TextView tvHighScore = dialog.findViewById(R.id.tvHighScore);
        Button btnReplay = dialog.findViewById(R.id.btnReplay);
//        Button btnShare = dialog.findViewById(R.id.btnShare);
        Button btnChooseTopic = dialog.findViewById(R.id.btnChooseTopic);

        tvYourScore.setText(getString(R.string.your_score, score));
        tvHighScore.setText(getString(R.string.high_score, restoreHighScore()));

        btnReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentVocabulary = 0;
                score = 0;
                tvScore.setText(String.valueOf(score));
                heart = 4;
                tvHeart.setText(String.valueOf(heart));
                Collections.shuffle(vocabularies);
                initVocabulary();
                enableButtonGuess();
                dialog.dismiss();
            }
        });

        btnChooseTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            }
        });

        dialog.show();
    }

    private void saveHighScore(int highScore) {
        SharedPreferences pref = getSharedPreferences(PREF_HIGH_SCORE, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(HIGH_SCORE, highScore);
        editor.apply();
    }

    private int restoreHighScore() {
        return getSharedPreferences(PREF_HIGH_SCORE, MODE_PRIVATE)
                .getInt(HIGH_SCORE, 0);
    }

    @Override
    protected void onPause() {
        tts.stop();
        tts.shutdown();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

}
