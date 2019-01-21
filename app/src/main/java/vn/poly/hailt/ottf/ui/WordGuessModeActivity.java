package vn.poly.hailt.ottf.ui;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.SparseIntArray;
import android.view.View;
import android.view.Window;
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

    private View iclHeader;
    private ImageView imgBack;
    private TextView tvHeader;

    private CardView cvImage;
    private ImageView imgThing;
    private TextView tvVietnamese;

    private FlexboxLayout flbAnswerArea;
    private FlexboxLayout flbGuessArea;

    private Handler handler;

    private List<Vocabulary> vocabularies;

    private SparseIntArray sparseIntArray;

    private String vocabulary;
    private int index = 0;
    private String guess = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_guess_mode);

        handler = new Handler();
        sparseIntArray = new SparseIntArray();
        initData();
        initViews();
        initActions();

        index = getSharedPreferences(PREF_QUESTION_NUMBER, MODE_PRIVATE).getInt(QUESTION_NUMBER, 0);
        vocabulary = vocabularies.get(index).english;

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

                int edge = flbAnswerArea.getWidth() / 8 - 8;
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(edge, edge);
                lp.setMargins(4, 4, 4, 4);

                for (int i = 0; i < vocabulary.length(); i++) {
                    Button button = new Button(WordGuessModeActivity.this);

                    button.setLayoutParams(lp);
                    button.setBackgroundResource(R.drawable.btn_guess);
                    flbAnswerArea.addView(button);
                }

                for (int i = 0; i < 16; i++) {
                    Button button = new Button(WordGuessModeActivity.this);
                    button.setText(lettersList.get(i));
                    button.setLayoutParams(lp);
                    button.setAllCaps(true);
                    button.setBackgroundResource(R.drawable.btn_guess);
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

    private void initViews() {
        iclHeader = findViewById(R.id.iclHeader);
        imgBack = iclHeader.findViewById(R.id.imgBack);
        tvHeader = iclHeader.findViewById(R.id.tvHeader);

        cvImage = findViewById(R.id.cvImage);
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

    private View.OnClickListener btnGuessListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button btnGuess = ((Button) v);

            String letter = btnGuess.getText().toString();

            for (int i = 0; i < vocabulary.length(); i++) {

                Button btnAnswer = (Button) flbAnswerArea.getChildAt(i);
                int indexOfButtonGuess = flbGuessArea.indexOfChild(btnGuess);

                if (btnAnswer.getText().equals("")) {
                    btnAnswer.setText(letter);
                    btnGuess.setVisibility(View.INVISIBLE);
                    sparseIntArray.put(i, indexOfButtonGuess);
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

                if (guess.length() == vocabulary.length()) {
                    if (guess.equalsIgnoreCase(vocabulary)) {
                        Toast.makeText(WordGuessModeActivity.this, "Ting Ting!", Toast.LENGTH_SHORT).show();
                        showAnswerDialog();
                    } else {
                        Toast.makeText(WordGuessModeActivity.this, "Te````````", Toast.LENGTH_SHORT).show();
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
                btnAnswer.setText("");

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

        Button btnContinue = dlgShowAnswer.findViewById(R.id.btnContinue);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlgShowAnswer.dismiss();
            }
        });

        dlgShowAnswer.show();
    }

//    private void nextVocabulary(List<View> views) {
//        for (int i = 0; i < views.size(); i++) {
//            ObjectAnimator objAnimFadeOut = ObjectAnimator.ofFloat(views.get(i), "alpha", 1f, 0f);
//            objAnimFadeOut.setDuration(400);
//            ObjectAnimator objAnimFadeIn = ObjectAnimator.ofFloat(views.get(i), "alpha", 0f, 1f);
//            objAnimFadeIn.setDuration(400);
//            AnimatorSet anim = new AnimatorSet();
//            anim.play(objAnimFadeOut).before(objAnimFadeIn);
//            anim.start();
//
//            objAnimFadeOut.addListener(new Animator.AnimatorListener() {
//                @Override
//                public void onAnimationStart(Animator animation) {
//
//                }
//
//                @Override
//                public void onAnimationEnd(Animator animation) {
//
//                }
//
//                @Override
//                public void onAnimationCancel(Animator animation) {
//
//                }
//
//                @Override
//                public void onAnimationRepeat(Animator animation) {
//
//                }
//            });
//        }
//
//    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences pref = getSharedPreferences(PREF_QUESTION_NUMBER, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(QUESTION_NUMBER, index);
        editor.apply();
    }
}
