package vn.poly.hailt.ottf.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import vn.poly.hailt.ottf.R;
import vn.poly.hailt.ottf.adapter.DataAdapter;
import vn.poly.hailt.ottf.adapter.ImageAdapter;
import vn.poly.hailt.ottf.model.Vocabulary;

public class LearnActivity extends AppCompatActivity {

    private ImageView imgBack;

    private TextView tvEnglish;
    private CardView cvImage;
    private ImageView imgThing;
    private TextView tvVietnamese;
    private RecyclerView lvImage;

    private List<Vocabulary> vocabularies;

    private TextToSpeech tts;

    private int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        initViews();
        initTTS();
        initActions();
        initData();
        if (vocabularies.size() > 0)
            loadVocabulary(vocabularies.get(0));
        initRecyclerView();

    }

    private void speakVocabulary() {
        String toSpeak = vocabularies.get(index).english;
        tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
    }

    private void animateAndSpeakVocabulary(List<View> views, final Vocabulary vocabulary) {
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
                    loadVocabulary(vocabulary);
                    speakVocabulary();
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

    private void loadVocabulary(Vocabulary vocabulary) {
        if (vocabularies != null) {
            tvEnglish.setText(vocabulary.english);
            Glide.with(this).load(vocabulary.imageLink).into(imgThing);
            tvVietnamese.setText(vocabulary.vietnamese);
        }
    }

    private void initRecyclerView() {
        ImageAdapter adapter = new ImageAdapter(this, vocabularies);
        lvImage.setHasFixedSize(true);
        lvImage.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        lvImage.setAdapter(adapter);

        adapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                index = position;
                Vocabulary vocabulary = vocabularies.get(position);
                List<View> views = Arrays.asList(tvEnglish, cvImage, imgThing, tvVietnamese);
                animateAndSpeakVocabulary(views, vocabulary);
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

    private void initData() {
        DataAdapter dataAdapter = new DataAdapter(this);
        dataAdapter.createDatabase();
        dataAdapter.open();
        vocabularies = dataAdapter.getVocabularies(getIntent().getIntExtra("id", 1));
        dataAdapter.close();
    }

    private void initActions() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            }
        });

        cvImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakVocabulary();
            }
        });
    }

    private void initViews() {
        View iclHeader = findViewById(R.id.iclHeader);
        imgBack = iclHeader.findViewById(R.id.imgBack);
        TextView tvHeader = iclHeader.findViewById(R.id.tvHeader);
        tvHeader.setText(getIntent().getStringExtra("topic"));

        tvEnglish = findViewById(R.id.tvEnglish);
        cvImage = findViewById(R.id.cvImage);
        imgThing = findViewById(R.id.imgThing);
        tvVietnamese = findViewById(R.id.tvVietnamese);
        lvImage = findViewById(R.id.lvImage);
    }

    @Override
    protected void onPause() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }
}
