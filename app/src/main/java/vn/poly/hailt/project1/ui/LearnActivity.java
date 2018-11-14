package vn.poly.hailt.project1.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import vn.poly.hailt.project1.R;
import vn.poly.hailt.project1.adapter.ImageAdapter;
import vn.poly.hailt.project1.model.Vocabulary;

public class LearnActivity extends AppCompatActivity {

    private View iclHeader;
    private ImageView imgBack;
    private TextView tvHeader;

    private RecyclerView lvImage;
    private LayoutManager manager;
    private ImageAdapter adapter;
    private List<Vocabulary> vocabularies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        initViews();
        initActions();
        vocabularies = new ArrayList<>();
        vocabularies.add(new Vocabulary(R.drawable.img_topic_number));
        vocabularies.add(new Vocabulary(R.drawable.img_topic_number));
        vocabularies.add(new Vocabulary(R.drawable.img_topic_number));
        vocabularies.add(new Vocabulary(R.drawable.img_topic_number));
        vocabularies.add(new Vocabulary(R.drawable.img_topic_number));
        adapter = new ImageAdapter(this, vocabularies);
        manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        lvImage.setLayoutManager(manager);
        lvImage.setAdapter(adapter);

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

    private void initViews() {
        iclHeader = findViewById(R.id.iclHeader);
        imgBack = iclHeader.findViewById(R.id.imgBack);
        tvHeader = iclHeader.findViewById(R.id.tvHeader);

        lvImage = findViewById(R.id.lvImage);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }
}
