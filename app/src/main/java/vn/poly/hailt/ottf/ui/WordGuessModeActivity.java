package vn.poly.hailt.ottf.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayout;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import vn.poly.hailt.ottf.R;
import vn.poly.hailt.ottf.adapter.DataAdapter;
import vn.poly.hailt.ottf.common.BaseActivity;
import vn.poly.hailt.ottf.common.Constant;
import vn.poly.hailt.ottf.model.Vocabulary;

public class WordGuessModeActivity extends BaseActivity implements Constant {

    private ImageView imgBack;

    private TextView tvHeart;
    private TextView tvScore;
    private FrameLayout containerMainImage;

    private FlexboxLayout flbAnswerArea;
    private FlexboxLayout flbGuessArea;

    private Handler handler;
    private MediaPlayer checkSound;

    private List<Vocabulary> vocabularies;

    private SparseIntArray sparseIntArray;

    private String vocabulary;
    private int currentVocabulary = 0;
    private String guess = "";
    private int touchCount = 0;
    private int score = 0;
    private int heart = 1;

    @Override
    protected int getLayoutID() {
        return R.layout.activity_word_guess_mode;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        handler = new Handler();
        checkSound = new MediaPlayer();
        sparseIntArray = new SparseIntArray();
        initData();
        initViews();
        initTextToSpeech();
        initActions();

        initPlayArea();
    }

    private void initViews() {
        View iclHeader = findViewById(R.id.iclHeader);
        imgBack = iclHeader.findViewById(R.id.imgBack);
        TextView tvHeader = iclHeader.findViewById(R.id.tvHeader);
        tvHeader.setText(getIntent().getStringExtra("topic"));

        tvHeart = findViewById(R.id.tvHeart);
        tvScore = findViewById(R.id.tvScore);

        containerMainImage = findViewById(R.id.containerMainImage);

        flbAnswerArea = findViewById(R.id.flbAnswerArea);
        flbGuessArea = findViewById(R.id.flbGuessArea);
    }

    private void initActions() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            }
        });

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

    private void initPlayArea() {

        vocabulary = vocabularies.get(currentVocabulary).english;
        vocabulary = vocabulary.replace(" ", "");

        tvEnglishInMainImage(containerMainImage, vocabularies.get(currentVocabulary).vietnamese);

        String[] letters = vocabulary.split("");
        final List<String> lettersList = new LinkedList<>(Arrays.asList(letters));
        lettersList.remove(0);

        Random r = new Random();
        String alphabet = "qwertyuiopasdfghjklzxcvbnm";

        for (int i = 0; i < 16 - vocabulary.length(); i++) {
            String letter = String.valueOf(alphabet.charAt(r.nextInt(alphabet.length())));
            lettersList.add(letter);
        }

        Collections.shuffle(lettersList);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                int edgeOfButtonAnswer = flbAnswerArea.getWidth() / 8 - 8;
                LinearLayout.LayoutParams lpGuess = new LinearLayout.LayoutParams(edgeOfButtonAnswer, edgeOfButtonAnswer);
                lpGuess.setMargins(4, 4, 4, 4);

                int edgeOfButtonGuess = flbGuessArea.getWidth() / 8 - 16;
                LinearLayout.LayoutParams lpAnswer = new LinearLayout.LayoutParams(edgeOfButtonGuess, edgeOfButtonGuess);
                lpAnswer.setMargins(4, 4, 4, 4);

                for (int i = 0; i < vocabulary.length(); i++) {
                    Button button = new Button(WordGuessModeActivity.this);

                    button.setLayoutParams(lpAnswer);
                    button.setAllCaps(true);
                    button.setBackgroundResource(R.drawable.btn_letter_answer);
                    button.setTextColor(getResources().getColor(R.color.colorPrimary));
                    button.setTypeface(button.getTypeface(), Typeface.BOLD);
                    button.setPadding(0, 0, 0, 0);
                    flbAnswerArea.addView(button);
                }

                for (int i = 0; i < 16; i++) {
                    Button button = new Button(WordGuessModeActivity.this);
                    button.setText(lettersList.get(i));
                    button.setLayoutParams(lpGuess);
                    button.setAllCaps(true);
                    button.setTextColor(getResources().getColor(R.color.colorTextPrimary));
                    button.setBackgroundResource(R.drawable.btn_letter_guess);
                    button.setTypeface(button.getTypeface(), Typeface.BOLD);
                    button.setPadding(0, 0, 0, 0);
                    flbGuessArea.addView(button);
                }

                for (int child = 0; child < flbGuessArea.getChildCount(); child++) {

                    Button btnGuess = (Button) flbGuessArea.getChildAt(child);
                    btnGuess.setOnClickListener(btnGuessListener);

                }

                for (int child = 0; child < flbAnswerArea.getChildCount(); child++) {

                    Button btnGuess = (Button) flbAnswerArea.getChildAt(child);
                    btnGuess.setOnClickListener(btnAnswerListener);

                }
            }
        }, 100);
    }

    private View.OnClickListener btnGuessListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button btnGuess = ((Button) v);

            if (touchCount < vocabulary.length()) {
                String letter = btnGuess.getText().toString();

                for (int i = 0; i < vocabulary.length(); i++) {

                    Button btnAnswer = (Button) flbAnswerArea.getChildAt(i);
                    int indexOfButtonGuess = flbGuessArea.indexOfChild(btnGuess);

                    if (btnAnswer.getText().equals("")) {
                        btnAnswer.setText(letter);
                        btnGuess.setVisibility(View.INVISIBLE);
                        sparseIntArray.put(i, indexOfButtonGuess);
                        touchCount += 1;
                        break;
                    }
                }

                Button btnAnswerFinal = (Button) flbAnswerArea.getChildAt(vocabulary.length() - 1);

                if (!btnAnswerFinal.getText().equals("")) {
                    for (int i = 0; i < vocabulary.length(); i++) {
                        Button btnAnswer = (Button) flbAnswerArea.getChildAt(i);
                        String text = btnAnswer.getText().toString();
                        guess = String.valueOf(new StringBuilder().append(guess).append(text));
                    }

                    if (touchCount == vocabulary.length()) {
                        if (guess.equalsIgnoreCase(vocabulary)) {
                            playSoundEffect(checkSound, R.raw.correct_sound);
                            score++;
                            changeScore(tvScore, score);

                            animTeeter().setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                    flbGuessArea.setVisibility(View.INVISIBLE);
                                    disableAnswerButton();
                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    speakText(vocabularies.get(currentVocabulary).english);
                                    if (currentVocabulary == vocabularies.size() - 1) {
                                        if (score > restoreHighScore()) {
                                            saveHighScore(score);
                                        }
                                        handler.postDelayed(new Runnable() {
                                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                            @Override
                                            public void run() {
                                                showAnswerDialog(getString(R.string.victory));
                                            }
                                        }, 200);
                                    } else {
                                        handler.postDelayed(new Runnable() {
                                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                            @Override
                                            public void run() {
                                                showAnswerDialog(generateCongratulation());
                                            }
                                        }, 200);
                                    }
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });

                        } else {
                            for (int i = 0; i < vocabulary.length(); i++) {
                                Button btnAnswer = (Button) flbAnswerArea.getChildAt(i);
                                btnAnswer.setTextColor(Color.RED);
                            }
                            playSoundEffect(checkSound, R.raw.incorrect_sound);
                            heart--;
                            changeHeart(tvHeart, heart);
                            animTeeter().setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    if (heart == 0) {
                                        if (score > restoreHighScore()) {
                                            saveHighScore(score);
                                            showAnswerDialog(getString(R.string.high_score_title));
                                            return;
                                        }
                                        speakText(vocabularies.get(currentVocabulary).english);
                                        showAnswerDialog(generateGameOverTitle());

                                    } else {
                                        for (int i = 0; i < vocabulary.length(); i++) {
                                            Button btnAnswer = (Button) flbAnswerArea.getChildAt(i);
                                            btnAnswer.setTextColor(getResources().getColor(R.color.colorPrimary));
                                        }
                                    }
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                            Toast.makeText(WordGuessModeActivity.this,
                                    getString(R.string.noti_wrong_answer), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }
    };

    private View.OnClickListener btnAnswerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button btnAnswer = (Button) v;

            int indexOfButtonGuess = sparseIntArray.get(flbAnswerArea.indexOfChild(btnAnswer));

            if (!btnAnswer.getText().equals("")) {
                guess = "";
                touchCount -= 1;

                btnAnswer.setText("");
                btnAnswer.setTextColor(getResources().getColor(R.color.colorPrimary));

                Button btnGuess = (Button) flbGuessArea.getChildAt(indexOfButtonGuess);
                btnGuess.setVisibility(View.VISIBLE);
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showAnswerDialog(String congratulation) {
        final Dialog dlgShowAnswer = new Dialog(this, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        dlgShowAnswer.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlgShowAnswer.setContentView(R.layout.dialog_show_answer);
        Objects.requireNonNull(dlgShowAnswer.getWindow()).getAttributes().windowAnimations = R.style.FullScreenDialogAnimation;

        TextView tvCongratulation = dlgShowAnswer.findViewById(R.id.tvCongratulation);
        TextView tvAnswer = dlgShowAnswer.findViewById(R.id.tvAnswer);
        final Button btnNext = dlgShowAnswer.findViewById(R.id.btnNext);
        TextView tvYourScore = dlgShowAnswer.findViewById(R.id.tvYourScore);
        TextView tvHighScore = dlgShowAnswer.findViewById(R.id.tvHighScore);
        Button btnReplay = dlgShowAnswer.findViewById(R.id.btnReplay);
        Button btnShare = dlgShowAnswer.findViewById(R.id.btnShare);
        Button btnChooseTopic = dlgShowAnswer.findViewById(R.id.btnChooseTopic);

        tvAnswer.setText(vocabularies.get(currentVocabulary).english);

        if (heart == 0 || currentVocabulary == vocabularies.size() - 1) {
            tvYourScore.setVisibility(View.VISIBLE);
            tvYourScore.setText(getString(R.string.your_score, score));
            tvHighScore.setVisibility(View.VISIBLE);
            tvHighScore.setText(getString(R.string.high_score, restoreHighScore()));
            btnReplay.setVisibility(View.VISIBLE);
            btnShare.setVisibility(View.VISIBLE);
            btnChooseTopic.setVisibility(View.VISIBLE);
            dlgShowAnswer.setCancelable(false);
            dlgShowAnswer.setCanceledOnTouchOutside(false);
            tvCongratulation.setText(congratulation);

            btnReplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentVocabulary = 0;
                    touchCount = 0;
                    guess = "";
                    score = 0;
                    tvScore.setText(String.valueOf(score));
                    heart = 1;
                    tvHeart.setText(String.valueOf(heart));
                    removePlayArea();
                    Collections.shuffle(vocabularies);
                    dlgShowAnswer.dismiss();
                    initPlayArea();
                    flbGuessArea.setVisibility(View.VISIBLE);
                }
            });

//            btnShare.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        shareScreenshot();
//                    }
//                }, 100);
//            }
//        });

            btnChooseTopic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dlgShowAnswer.dismiss();
                    finish();
                    overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                }
            });

        } else {
            tvCongratulation.setText(congratulation);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    btnNext.setVisibility(View.VISIBLE);
                }
            }, 1500);

            currentVocabulary += 1;
            touchCount = 0;
            guess = "";

            removePlayArea();

            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dlgShowAnswer.dismiss();
                    flbGuessArea.setVisibility(View.VISIBLE);
                    initPlayArea();
                }
            });

            dlgShowAnswer.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        dlgShowAnswer.dismiss();
                        flbGuessArea.setVisibility(View.VISIBLE);
                        initPlayArea();
                    }
                    return true;
                }
            });
        }

        dlgShowAnswer.show();
    }

    private String generateCongratulation() {
        String[] arrCon = getResources().getStringArray(R.array.congratulation_array);
        return arrCon[new Random().nextInt(arrCon.length)];
    }

    private String generateGameOverTitle() {
        String[] arrCon = getResources().getStringArray(R.array.game_over_title_array);
        return arrCon[new Random().nextInt(arrCon.length)];
    }

    private RotateAnimation animTeeter() {

        RotateAnimation rotate = new RotateAnimation(10, -10, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(200);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setRepeatMode(Animation.REVERSE);
        rotate.setRepeatCount(2);

        for (int child = 0; child < flbAnswerArea.getChildCount(); child++) {

            Button btnGuess = (Button) flbAnswerArea.getChildAt(child);
            btnGuess.startAnimation(rotate);

        }
        return rotate;
    }

    private void removePlayArea() {
        flbAnswerArea.removeAllViews();
        flbGuessArea.removeAllViews();
        containerMainImage.removeAllViews();
    }

    private void disableAnswerButton() {
        for (int button = 0; button < flbAnswerArea.getChildCount(); button++) {
            Button btnGuess = (Button) flbAnswerArea.getChildAt(button);
            btnGuess.setEnabled(false);
        }
    }

    private void saveHighScore(int highScore) {
        SharedPreferences pref = getSharedPreferences(PREF_H_SCORE_WORD_GUESS_MODE, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(H_SCORE_WORD_GUESS_MODE, highScore);
        editor.apply();
    }

    private int restoreHighScore() {
        return getSharedPreferences(PREF_H_SCORE_WORD_GUESS_MODE, MODE_PRIVATE)
                .getInt(H_SCORE_WORD_GUESS_MODE, 0);
    }

    @Override
    protected void onDestroy() {
        checkSound.stop();
        checkSound.release();
        super.onDestroy();
    }
}
