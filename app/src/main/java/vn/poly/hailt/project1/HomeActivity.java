package vn.poly.hailt.project1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

public class HomeActivity extends AppCompatActivity {


    private Button btnLearn;
    private Button btnPlay;
    private ToggleButton tgbSound;
    private Button btnInformation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();

        btnLearn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ChooseTopicActivity.class);
                startActivity(intent);
            }
        });
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, PlayActivity.class);
                startActivity(intent);
            }
        });


//        ImageView imgQuestion = findViewById(R.id.imgQuestion);

//        Glide.with(this).load("https://image.ibb.co/bx1z7f/GARENA.png").into(imgQuestion);

    }

    private void initViews() {
        btnLearn = findViewById(R.id.btnLearn);
        btnPlay = findViewById(R.id.btnPlay);
        tgbSound = findViewById(R.id.tgbSound);
        btnInformation = findViewById(R.id.btnInformation);

    }
}
