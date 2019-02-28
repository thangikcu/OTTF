package vn.poly.hailt.ottf.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import vn.poly.hailt.ottf.R;
import vn.poly.hailt.ottf.adapter.DataAdapter;
import vn.poly.hailt.ottf.adapter.ImageAdapter;
import vn.poly.hailt.ottf.common.BaseActivity;
import vn.poly.hailt.ottf.model.Vocabulary;

public class LearnActivity extends BaseActivity {

    private ImageView imgBack;

    private LinearLayout containerMainImage;
    private TextView tvEnglish;
    private TextView tvTranscription;
    private TextView tvVietnamese;
    private RecyclerView lvImage;

    private List<Vocabulary> vocabularies;


    private int index = 0;

    @Override
    protected int getLayoutID() {
        return R.layout.activity_learn;
    }

    @Override
    protected void onCreateActivity(Bundle savedInstanceState) {
        initViews();
        initTextToSpeech();
        initActions();
        initData();
        if (vocabularies.size() > 0)
            loadVocabulary(vocabularies.get(0));
        initRecyclerView();
    }

    private void animateAndSpeakVocabulary(List<View> views, final Vocabulary vocabulary) {
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
                    loadVocabulary(vocabulary);
                    speakText(vocabulary.english);
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
            tvEnglish.setTextColor(randomColor());
            tvTranscription.setText(vocabulary.transcription);
            tvTranscription.setTextColor(randomColor());
            tvVietnamese.setText(vocabulary.vietnamese);
            tvVietnamese.setTextColor(randomColor());
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
                List<View> views = Arrays.asList(tvEnglish, containerMainImage, tvTranscription, tvVietnamese);
                animateAndSpeakVocabulary(views, vocabulary);
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

        containerMainImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakText(vocabularies.get(index).english);
            }
        });
    }

    private void initViews() {
        View iclHeader = findViewById(R.id.iclHeader);
        imgBack = iclHeader.findViewById(R.id.imgBack);
        TextView tvHeader = iclHeader.findViewById(R.id.tvHeader);
        tvHeader.setText(getIntent().getStringExtra("topic"));


        containerMainImage = findViewById(R.id.containerMainImage);
        tvEnglish = findViewById(R.id.tvEnglish);
        tvTranscription = findViewById(R.id.tvTranscription);
        tvVietnamese = findViewById(R.id.tvVietnamese);
        lvImage = findViewById(R.id.lvImage);
    }

}
