package vn.poly.hailt.ottf.ui;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import vn.poly.hailt.ottf.R;
import vn.poly.hailt.ottf.common.Constant;
import vn.poly.hailt.ottf.fragment.ChooseTopicFragment;
import vn.poly.hailt.ottf.service.BackgroundSoundService;

public class HomeActivity extends AppCompatActivity implements Constant {

    private Button btnLearn;
    private Button btnPlay;
    private ToggleButton tgbSound;
    private Button btnInformation;
    private ChooseTopicFragment fragChooseTopic;

    private boolean doubleBackToExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initViews();
        initActions();
        initBackgroundSound();
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
                showChooseTopicFragment();

                Bundle bundle = new Bundle();
                bundle.putInt("keyAct", 0);

                fragChooseTopic.setArguments(bundle);
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooseModeDialog();
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
        if (sound) startService(new Intent(HomeActivity.this, BackgroundSoundService.class));
        else tgbSound.setChecked(true);
    }

    private void showInformationDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_information);
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
    }

    private void showChooseModeDialog() {
        final Dialog dlgChooseMode = new Dialog(this);
        dlgChooseMode.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlgChooseMode.setContentView(R.layout.dialog_choose_mode);
        dlgChooseMode.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dlgChooseMode.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        Button btnSelectionMode = dlgChooseMode.findViewById(R.id.btnSelectionMode);
        Button btnWordGuessMode = dlgChooseMode.findViewById(R.id.btnWordGuessMode);

        btnSelectionMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChooseTopicFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("keyAct", 1);

                fragChooseTopic.setArguments(bundle);
                dlgChooseMode.dismiss();
            }
        });

        btnWordGuessMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, WordGuessModeActivity.class));
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                dlgChooseMode.dismiss();
            }
        });

        dlgChooseMode.show();
    }

    @Override
    public void onBackPressed() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (!fragChooseTopic.isHidden() && fragChooseTopic.isAdded()) {
            ft
                    .setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right)
                    .hide(fragChooseTopic)
                    .commit();

        } else {
            if (doubleBackToExit) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExit = true;
            Toast.makeText(this, getString(R.string.double_back_to_exit), Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExit = false;
                }
            }, 2000);
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initBackgroundSound();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService(new Intent(HomeActivity.this, BackgroundSoundService.class));
    }
}
