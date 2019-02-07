package vn.poly.hailt.ottf.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayout;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import vn.poly.hailt.ottf.R;
import vn.poly.hailt.ottf.adapter.DataAdapter;
import vn.poly.hailt.ottf.common.Constant;
import vn.poly.hailt.ottf.model.Vocabulary;

public class WordGuessModeActivity extends AppCompatActivity implements Constant {

    private ImageView imgBack;
    private TextView tvHeader;

    private ImageView imgThing;
    private TextView tvVietnamese;

    private FlexboxLayout flbAnswerArea;
    private FlexboxLayout flbGuessArea;

    private Handler handler;
    private MediaPlayer checkSound;

    private List<Vocabulary> vocabularies;

    private SparseIntArray sparseIntArray;

    private String vocabulary;
    private int index = 0;
    private String guess = "";
    private int touchCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_guess_mode);

        handler = new Handler();
        checkSound = new MediaPlayer();
        sparseIntArray = new SparseIntArray();
        initData();
        initViews();
        initActions();

        index = getSharedPreferences(PREF_QUESTION_NUMBER, MODE_PRIVATE).getInt(QUESTION_NUMBER, 0);

        initPlayArea();
    }

    private void initViews() {
        View iclHeader = findViewById(R.id.iclHeader);
        imgBack = iclHeader.findViewById(R.id.imgBack);
        tvHeader = iclHeader.findViewById(R.id.tvHeader);

        imgThing = findViewById(R.id.imgThing);
        tvVietnamese = findViewById(R.id.tvVietnamese);

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
        DataAdapter dataAdapter = new DataAdapter(this);
        dataAdapter.createDatabase();
        dataAdapter.open();
        vocabularies = dataAdapter.getAllVocabularies();
        dataAdapter.close();
    }

    private void initPlayArea() {

        tvHeader.setText(getString(R.string.question_number, index + 1));
        vocabulary = vocabularies.get(index).english;
        vocabulary = vocabulary.replace(" ", "");

        Glide.with(this).load(vocabularies.get(index).imageLink).into(imgThing);
        tvVietnamese.setText(vocabularies.get(index).vietnamese);

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
                    button.setBackgroundResource(R.drawable.btn_letter_answer);
                    button.setTextColor(getResources().getColor(R.color.colorPrimary));
                    button.setTypeface(button.getTypeface(), Typeface.BOLD);
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
                            soundEffect(R.raw.correct_sound);
                            animTeeter().setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                    flbGuessArea.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            showAnswerDialog();
                                        }
                                    }, 200);
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
                            soundEffect(R.raw.incorrect_sound);
                            animTeeter().setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    for (int i = 0; i < vocabulary.length(); i++) {
                                        Button btnAnswer = (Button) flbAnswerArea.getChildAt(i);
                                        btnAnswer.setTextColor(getResources().getColor(R.color.colorPrimary));
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

    private void showAnswerDialog() {
        final Dialog dlgShowAnswer = new Dialog(this, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        dlgShowAnswer.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlgShowAnswer.setContentView(R.layout.dialog_show_answer);
        dlgShowAnswer.getWindow().getAttributes().windowAnimations = R.style.FullScreenDialogAnimation;

        TextView tvCongratulation = dlgShowAnswer.findViewById(R.id.tvCongratulation);
        TextView tvAnswer = dlgShowAnswer.findViewById(R.id.tvAnswer);
        final Button btnNext = dlgShowAnswer.findViewById(R.id.btnNext);

        tvCongratulation.setText(generateCongratulation());
        tvAnswer.setText(vocabularies.get(index).english);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                btnNext.setVisibility(View.VISIBLE);
            }
        }, 1500);
        index += 1;
        touchCount = 0;
        guess = "";

        flbAnswerArea.removeAllViews();
        flbGuessArea.removeAllViews();

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

        dlgShowAnswer.show();
    }

    private String generateCongratulation() {
        String[] arrCon = getResources().getStringArray(R.array.congratulation_array);
        return arrCon[new Random().nextInt(arrCon.length)];
    }

    private void soundEffect(int sound) {
        checkSound.reset();
        checkSound = MediaPlayer.create(getApplicationContext(), sound);
        checkSound.start();
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

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences pref = getSharedPreferences(PREF_QUESTION_NUMBER, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(QUESTION_NUMBER, index);
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }
}
