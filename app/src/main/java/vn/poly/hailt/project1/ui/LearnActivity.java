package vn.poly.hailt.project1.ui;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;

import vn.poly.hailt.project1.R;
import vn.poly.hailt.project1.adapter.DataAdapter;
import vn.poly.hailt.project1.adapter.ImageAdapter;
import vn.poly.hailt.project1.model.Vocabulary;

public class LearnActivity extends AppCompatActivity {

    private ImageView imgBack;
    private TextView tvHeader;//Tên chủ đề

    private TextView tvEnglish;
    private CardView cvImage;
    private ImageView imgThing;
    private TextView tvVietnamese;
    private RecyclerView lvImage;

    private ImageAdapter adapter;
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

        adapter = new ImageAdapter(this, vocabularies);
        lvImage.setHasFixedSize(true);
        lvImage.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        lvImage.setAdapter(adapter);
        loadVocabulary(vocabularies.get(0));
        adapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                index = position;
                Vocabulary vocabulary = vocabularies.get(position);
                loadVocabulary(vocabulary);
                speakVocabulary();
            }
        });

    }

    private void speakVocabulary() {
        String toSpeak = vocabularies.get(index).english;
        tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
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

    private void loadVocabulary(Vocabulary vocabulary) {
        tvEnglish.setText(vocabulary.english);
        Glide.with(this).load(vocabulary.imageLink).into(imgThing);
        tvVietnamese.setText(vocabulary.vietnamese);
    }

    private void initData() {
        DataAdapter dataAdapter = new DataAdapter(this);
        dataAdapter.createDatabase();
        dataAdapter.open();
        vocabularies = dataAdapter.getData();
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
        tvHeader = iclHeader.findViewById(R.id.tvHeader);
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