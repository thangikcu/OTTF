package vn.poly.hailt.ottf.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import vn.poly.hailt.ottf.R;
import vn.poly.hailt.ottf.adapter.DataAdapter;
import vn.poly.hailt.ottf.common.BaseActivity;
import vn.poly.hailt.ottf.common.Constant;
import vn.poly.hailt.ottf.model.Vocabulary;

public class SelectionModeActivity extends BaseActivity implements Constant {

    private ImageView imgBack;
    private TextView tvHeart;
    private TextView tvScore;
    private FrameLayout containerMainImage;
    private Button btnCaseA;
    private Button btnCaseB;
    private Button btnCaseC;
    private Button btnCaseD;
    private GridLayout grlCase;

    private Handler handler;
    private MediaPlayer checkSound;

    private List<Vocabulary> vocabularies;

    private int currentVocabulary = 0;
    private int score = 0;
    private int heart = 4;

    private Dialog dlgGameOver;
    private ShareDialog shareDialog;

    @Override
    protected int getLayoutID() {
        return R.layout.activity_selection_mode;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        handler = new Handler();
        checkSound = new MediaPlayer();

        initData();
        initViews();
        initTextToSpeech();
        initActions();
        initVocabulary();
    }

    private void initData() {
        int idTopic = getIntent().getIntExtra("id", 1);
        DataAdapter dataAdapter = new DataAdapter(this);
        dataAdapter.createDatabase();
        dataAdapter.open();
        if (idTopic != 0) {
            vocabularies = dataAdapter.getVocabularies(idTopic);
        } else {
            vocabularies = dataAdapter.getAllVocabularies();
        }
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
        containerMainImage = findViewById(R.id.containerMainImage);
        btnCaseA = findViewById(R.id.btnCaseA);
        btnCaseB = findViewById(R.id.btnCaseB);
        btnCaseC = findViewById(R.id.btnCaseC);
        btnCaseD = findViewById(R.id.btnCaseD);
        grlCase = findViewById(R.id.grlCase);

        shareDialog = new ShareDialog(this);
    }

    private final View.OnClickListener btnGuessListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button btnGuess = ((Button) v);

            String guessValue = btnGuess.getText().toString();
            String answerValue = vocabularies.get(currentVocabulary).english;
            btnGuess.setSelected(true);
            animAndSpeakButtonAnswer();
            disableGuessButton();

            if (guessValue.equals(answerValue)) {

                playSoundEffect(checkSound, R.raw.correct_sound);
                ++score;
                changeScore(tvScore, score);

            } else {

                playSoundEffect(checkSound, R.raw.incorrect_sound);
                --heart;
                changeHeart(tvHeart, heart);

                if (heart == 0) {
                    if (score > restoreHighScore()) {
                        saveHighScore(score);
                    }
                    handler.postDelayed(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void run() {
                            showGameOverDialog(getString(R.string.game_over));
                        }
                    }, 1800);
                    return;
                }
            }

            if (currentVocabulary == vocabularies.size() - 1) {
                if (score > restoreHighScore()) {
                    saveHighScore(score);
                }
                handler.postDelayed(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void run() {
                        showGameOverDialog(getString(R.string.victory));
                    }
                }, 1800);
            } else {
                ++currentVocabulary;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        List<ViewGroup> views = Arrays.asList(containerMainImage, grlCase);
                        nextVocabulary(views);
                    }
                }, 1800);

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

        for (int button = 0; button < grlCase.getChildCount(); button++) {

            Button btnGuess = (Button) grlCase.getChildAt(button);
            btnGuess.setAllCaps(false);
            btnGuess.setTextColor(getResources().getColor(R.color.colorTextPrimary));
            btnGuess.setTextSize(TEXT_DEFAULT_SIZE);
            btnGuess.setOnClickListener(btnGuessListener);
        }
    }

    private void initVocabulary() {
        if (vocabularies.size() > 0) {
            Vocabulary vocabulary = vocabularies.get(currentVocabulary);

            tvEnglishInMainImage(containerMainImage, vocabulary.vietnamese);

            String caseA = vocabulary.caseA;
            String caseB = vocabulary.caseB;
            String caseC = vocabulary.caseC;
            String caseD = vocabulary.caseD;

            if (caseA.length() > MAX_NUMBER_LETTER_BUTTON) {
                btnCaseA.setTextSize(TEXT_SMALL_SIZE);
            } else if (caseB.length() > MAX_NUMBER_LETTER_BUTTON) {
                btnCaseB.setTextSize(TEXT_SMALL_SIZE);
            } else if (caseC.length() > MAX_NUMBER_LETTER_BUTTON) {
                btnCaseC.setTextSize(TEXT_SMALL_SIZE);
            } else if (caseD.length() > MAX_NUMBER_LETTER_BUTTON) {
                btnCaseD.setTextSize(TEXT_SMALL_SIZE);
            } else {
                for (int button = 0; button < grlCase.getChildCount(); button++) {
                    Button btnGuess = (Button) grlCase.getChildAt(button);
                    btnGuess.setTextSize(TEXT_DEFAULT_SIZE);
                }
            }

            btnCaseA.setText(caseA);
            btnCaseB.setText(caseB);
            btnCaseC.setText(caseC);
            btnCaseD.setText(caseD);

        }
    }

    private void nextVocabulary(List<ViewGroup> views) {
        for (int i = 0; i < views.size(); i++) {
            ObjectAnimator objAnimFadeOut = ObjectAnimator.ofFloat(views.get(i), "alpha", 1f, 0f);
            objAnimFadeOut.setDuration(400);
            ObjectAnimator objAnimFadeIn = ObjectAnimator.ofFloat(views.get(i), "alpha", 0f, 1f);
            objAnimFadeIn.setDuration(400);
            AnimatorSet anim = new AnimatorSet();
            anim.play(objAnimFadeOut).before(objAnimFadeIn);
            anim.start();

            objAnimFadeOut.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    containerMainImage.removeAllViews();
                    initVocabulary();
                    enableGuessButton();
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

    private void disableGuessButton() {
        for (int button = 0; button < grlCase.getChildCount(); button++) {
            Button btnGuess = (Button) grlCase.getChildAt(button);
            btnGuess.setEnabled(false);
        }
    }

    private void enableGuessButton() {
        for (int button = 0; button < grlCase.getChildCount(); button++) {
            Button btnGuess = (Button) grlCase.getChildAt(button);
            btnGuess.setEnabled(true);
            btnGuess.setSelected(false);
            btnGuess.setTextColor(getResources().getColor(R.color.colorTextPrimary));
            btnGuess.setBackgroundResource(R.drawable.bg_btn_case_selector);
        }
    }

    private void animAndSpeakButtonAnswer() {
        for (int button = 0; button < grlCase.getChildCount(); button++) {
            final Button btnGuess = (Button) grlCase.getChildAt(button);

            String guess = btnGuess.getText().toString();
            final String answer = vocabularies.get(currentVocabulary).english;

            if (guess.equals(answer)) {
                btnGuess.setTextColor(Color.YELLOW);
                ObjectAnimator anim = ObjectAnimator.ofInt(btnGuess, "backgroundResource",
                        R.drawable.bg_btn_case_pressed, R.drawable.bg_btn_case, R.drawable.bg_btn_case_pressed);
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
                        speakText(answer);
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showGameOverDialog(String title) {
        dlgGameOver = new Dialog(this);
        dlgGameOver.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlgGameOver.setContentView(R.layout.dialog_game_over);
        dlgGameOver.setCancelable(false);
        dlgGameOver.setCanceledOnTouchOutside(false);
        Objects.requireNonNull(dlgGameOver.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dlgGameOver.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        TextView tvTitle = dlgGameOver.findViewById(R.id.tvTitle);
        TextView tvYourScore = dlgGameOver.findViewById(R.id.tvYourScore);
        TextView tvHighScore = dlgGameOver.findViewById(R.id.tvHighScore);
        Button btnReplay = dlgGameOver.findViewById(R.id.btnReplay);
        Button btnShare = dlgGameOver.findViewById(R.id.btnShare);
        Button btnChooseTopic = dlgGameOver.findViewById(R.id.btnChooseTopic);

        tvTitle.setText(title);
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
                containerMainImage.removeAllViews();
                Collections.shuffle(vocabularies);
                initVocabulary();
                enableGuessButton();
                dlgGameOver.dismiss();
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        shareScreenshot();
                    }
                }, 100);
            }
        });

        btnChooseTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlgGameOver.dismiss();
                finish();
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            }
        });

        dlgGameOver.show();
    }

    private void saveHighScore(int highScore) {
        SharedPreferences pref = getSharedPreferences(PREF_H_SCORE_SELECTION_MODE, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(H_SCORE_SELECTION_MODE, highScore);
        editor.apply();
    }

    private int restoreHighScore() {
        return getSharedPreferences(PREF_H_SCORE_SELECTION_MODE, MODE_PRIVATE)
                .getInt(H_SCORE_SELECTION_MODE, 0);
    }

    private Bitmap rootView() {
        View v = getWindow().getDecorView().getRootView();
        v.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);
        return bitmap;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private Bitmap dialogView() {
        View v = Objects.requireNonNull(dlgGameOver.getWindow()).getDecorView().getRootView();
        v.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);
        return bitmap;
    }

    private Bitmap takeScreenshot(Bitmap bmp1, Bitmap bmp2) {
        Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
        Canvas canvas = new Canvas(bmOverlay);
        canvas.drawBitmap(bmp1, new Matrix(), null);
        canvas.drawARGB(100, 0, 0, 0);
        int centerX = (canvas.getWidth() - bmp2.getWidth()) / 2;
        int centerY = (canvas.getHeight() - bmp2.getHeight()) / 2;
        canvas.drawBitmap(bmp2, centerX, centerY, null);
        return bmOverlay;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void shareScreenshot() {
        if (ShareDialog.canShow(ShareLinkContent.class)) {

            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(takeScreenshot(rootView(), dialogView()))
                    .build();
            SharePhotoContent content = new SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .build();
            shareDialog.show(content);
        }
    }

}