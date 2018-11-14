package vn.poly.hailt.project1.ui;

import android.app.Dialog;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import vn.poly.hailt.project1.BackgroundSoundService;
import vn.poly.hailt.project1.Constant;
import vn.poly.hailt.project1.R;
import vn.poly.hailt.project1.fragment.ChooseTopicFragment;

public class HomeActivity extends AppCompatActivity implements Constant {

    private Button btnLearn;
    private Button btnPlay;
    private ToggleButton tgbSound;
    private Button btnInformation;
    private Intent intent;
    private ChooseTopicFragment fragChooseTopic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();
        initActions();
        initBackgroundSound();

        intent = new Intent(HomeActivity.this, ChooseTopicActivity.class);

    }

    private void initViews() {
        btnLearn = findViewById(R.id.btnLearn);
        btnPlay = findViewById(R.id.btnPlay);
        tgbSound = findViewById(R.id.tgbSound);
        btnInformation = findViewById(R.id.btnInformation);
        fragChooseTopic = new ChooseTopicFragment();
    }

    private void initActions() {
        btnLearn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                intent.putExtra("keyAct", 0);
//                startActivity(intent);
//                overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                showChooseTopicFragment();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("keyAct", 1);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
            }
        });

        btnInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInformationDialog();
            }
        });

        tgbSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences pref = getSharedPreferences(PREF_SOUND, MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                if (!isChecked) {
                    startService(new Intent(HomeActivity.this, BackgroundSoundService.class));
                    editor.putBoolean(IS_SOUND, true);
                } else {
                    stopService(new Intent(HomeActivity.this, BackgroundSoundService.class));
                    editor.putBoolean(IS_SOUND, false);
                }
                editor.apply();
            }
        });
    }

    private void initBackgroundSound() {
        SharedPreferences pref = getSharedPreferences(PREF_SOUND, MODE_PRIVATE);
        boolean sound = pref.getBoolean(IS_SOUND, true);
        if (sound) {
            startService(new Intent(HomeActivity.this, BackgroundSoundService.class));
        } else {
            tgbSound.setChecked(true);
        }
    }

    private void showInformationDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_information);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;


        String versionName = "";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        TextView tvVersion = dialog.findViewById(R.id.tvVersion);
        tvVersion.setText((getString(R.string.version) + versionName));

        Button btnClose = dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showChooseTopicFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        if (fragChooseTopic.isAdded()) {
            ft
                    .show(fragChooseTopic);
        } else {
            ft
                    .add(R.id.fragment_container, fragChooseTopic)
                    .show(fragChooseTopic);
        }
        ft.commit();
        Log.e("isShow", String.valueOf(!fragChooseTopic.isHidden()));
    }

    @Override
    public void onBackPressed() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (!fragChooseTopic.isHidden()) {
            ft
                    .setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right)
                    .hide(fragChooseTopic)
                    .commit();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(HomeActivity.this, BackgroundSoundService.class));
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService(new Intent(HomeActivity.this, BackgroundSoundService.class));
    }
}
