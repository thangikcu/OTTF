package vn.poly.hailt.project1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;

public class ChooseTopicActivity extends AppCompatActivity {

    private CardView cv1;
    private ImageView img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_topic);

        initViews();
        initActions();

    }

    private void initActions() {
        cv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseTopicActivity.this, LearnActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initViews() {
        cv1 = findViewById(R.id.cv1);
        img = findViewById(R.id.img);
    }
}
