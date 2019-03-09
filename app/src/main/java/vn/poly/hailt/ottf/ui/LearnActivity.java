package vn.poly.hailt.ottf.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vn.poly.hailt.ottf.R;
import vn.poly.hailt.ottf.adapter.DataAdapter;
import vn.poly.hailt.ottf.adapter.ImageAdapter;
import vn.poly.hailt.ottf.common.BaseActivity;
import vn.poly.hailt.ottf.model.Vocabulary;

public class LearnActivity extends BaseActivity {

    private View iclHeader;
    private ImageView imgBack;
    private ImageView imgSearch;

    private SearchView svVocabulary;

    private LinearLayout containerMainImage;
    private TextView tvEnglish;
    private TextView tvTranscription;
    private TextView tvVietnamese;
    private RecyclerView lvImage;
    private ImageAdapter adapter;
    private List<Vocabulary> vocabularies;

    private int index = 0;
    private boolean isShowSearch = false;

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

    @Override
    public void onVisibilityChanged(boolean isOpen) {
        super.onVisibilityChanged(isOpen);

        if (containerMainImage != null) {
            containerMainImage.setVisibility(isOpen ? View.GONE : View.VISIBLE);
        }
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

    private void slideDown(ViewGroup viewGroup, float toValue) {
        svVocabulary.setVisibility(View.VISIBLE);
        ObjectAnimator slideDown =
                ObjectAnimator.ofFloat(viewGroup, "translationY", 0, toValue);
        slideDown.setDuration(1000);
        slideDown.setInterpolator(new OvershootInterpolator());
        slideDown.start();
    }

    private void slideUp(ViewGroup viewGroup, float fromValue) {
        ObjectAnimator slideUp =
                ObjectAnimator.ofFloat(viewGroup, "translationY", fromValue, 0);
        slideUp.setDuration(1000);
        slideUp.setInterpolator(new OvershootInterpolator());
        slideUp.start();
        slideUp.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                svVocabulary.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private void initRecyclerView() {
        adapter = new ImageAdapter(this, vocabularies);
        lvImage.setHasFixedSize(true);
        lvImage.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        lvImage.setAdapter(adapter);

        adapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Vocabulary vocabulary = adapter.getItem(position);
                index = vocabularies.indexOf(vocabulary);
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

        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                float distSlideDownSearchView = iclHeader.getHeight() + 32f;
                float distSlideDownContainer = 72f;

                if (!isShowSearch) {

                    slideDown(svVocabulary, distSlideDownSearchView);
                    slideDown(containerMainImage, distSlideDownContainer);
                    isShowSearch = true;
                } else {

                    slideUp(svVocabulary, distSlideDownSearchView);
                    slideUp(containerMainImage, distSlideDownContainer);
                    isShowSearch = false;
                }
            }
        });

        containerMainImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakText(vocabularies.get(index).english);
            }
        });

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchManager != null) {
            svVocabulary.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
//        svVocabulary.setMaxWidth(Integer.MAX_VALUE);

        svVocabulary.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });

    }

    private void initViews() {
        iclHeader = findViewById(R.id.iclHeader);
        imgBack = iclHeader.findViewById(R.id.imgBack);
        imgSearch = findViewById(R.id.imgSearch);
        TextView tvHeader = iclHeader.findViewById(R.id.tvHeader);
        tvHeader.setText(getIntent().getStringExtra("topic"));

        containerMainImage = findViewById(R.id.containerMainImage);
        tvEnglish = findViewById(R.id.tvEnglish);
        tvTranscription = findViewById(R.id.tvTranscription);
        tvVietnamese = findViewById(R.id.tvVietnamese);
        lvImage = findViewById(R.id.lvImage);
        svVocabulary = findViewById(R.id.svVocabulary);
    }

    private void filter(String text) {
        ArrayList<Vocabulary> filteredList = new ArrayList<>();

        for (Vocabulary item : vocabularies) {
            if (item.english.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        adapter.filterList(filteredList);
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//
//        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//            if (svVocabulary != null) {
//                Rect outRect = new Rect();
//                svVocabulary.getGlobalVisibleRect(outRect);
//                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
//                    svVocabulary.clearFocus();
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    if (imm != null) {
//                        imm.hideSoftInputFromWindow(svVocabulary.getWindowToken(), 0);
//                    }
//                }
//            }
//        }
//
//        return super.dispatchTouchEvent(ev);
//    }

    // TODO: UX SearchView + ContainerMainImage
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        if (svVocabulary.isShown()) {
//            svVocabulary.clearFocus();
//        }
//    }
}
