package vn.poly.hailt.ottf.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseIntArray;
import android.view.KeyEvent;
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

    private ImageView imgBack;
    private TextView tvHeader;

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
                    button.setTypeface(Typeface.DEFAULT_BOLD);
                    button.setTextColor(getResources().getColor(R.color.colorPrimary));
                    flbAnswerArea.addView(button);
                }

                for (int i = 0; i < 16; i++) {
                    Button button = new Button(WordGuessModeActivity.this);
                    button.setText(lettersList.get(i));
                    button.setLayoutParams(lp);
                    button.setAllCaps(true);
                    button.setTextColor(getResources().getColor(R.color.colorTextPrimary));
                    button.setBackgroundResource(R.drawable.btn_letter);
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
                        showAnswerDialog();
                    } else {
                        for (int i = 0; i < vocabulary.length(); i++) {
                            Button btnAnswer = (Button) flbAnswerArea.getChildAt(i);
                            btnAnswer.setTextColor(Color.RED);
                        }
                        Toast.makeText(WordGuessModeActivity.this, "Câu trả lời của bạn chưa đúng", Toast.LENGTH_SHORT).show();
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
        tvAnswer.setText(vocabulary);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                btnNext.setVisibility(View.VISIBLE);
            }
        }, 1200);
        index += 1;
        guess = "";

        flbAnswerArea.removeAllViews();
        flbGuessArea.removeAllViews();


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlgShowAnswer.dismiss();
                initPlayArea();
            }
        });

        dlgShowAnswer.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dlgShowAnswer.dismiss();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }
}
