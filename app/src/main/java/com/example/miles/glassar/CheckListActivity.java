package com.example.miles.glassar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.miles.glassar.LoaderNCalculater.FileReader;

import java.io.File;

public class CheckListActivity extends Activity {

    Button launchAR;

    float[] ARC = FileReader.ARTReadBinary("optical_param_left.dat");

    String[] filenames = new String[]{"skull.stl", "maxilla.stl", "mandible.stl", "max_OSP.stl"
            , "man_OSP.stl", "arproject.txt", "arpoints.txt", "calipara.txt", "optical_param_left.dat"};
    boolean[] check = new boolean[]{false, false, false, false, false, false, false, false, false};
    boolean check_all = true;

    TextView skull_check;
    TextView maxiila_check;
    TextView mandible_check;
    TextView osp1_check;
    TextView osp2_check;
    TextView project_check;
    TextView arMarker_check;
    RadioButton STC_check;
    RadioButton ARC_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_check_list);
        launchAR = (Button) findViewById(R.id.launch_button);

        skull_check = (TextView) findViewById(R.id.skull_check_text);
        maxiila_check = (TextView) findViewById(R.id.maxi_check_text);
        mandible_check = (TextView) findViewById(R.id.mand_check_text);
        osp1_check = (TextView) findViewById(R.id.osp1_check_text);
        osp2_check = (TextView) findViewById(R.id.osp2_check_text);
        project_check = (TextView) findViewById(R.id.project_check_text);
        arMarker_check = (TextView) findViewById(R.id.ar_check_text);
        STC_check = (RadioButton) findViewById(R.id.stc_radButton);
        ARC_check = (RadioButton) findViewById(R.id.arc_radButton);

        launchAR.setEnabled(false);
        launchAR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckListActivity.this, MainActivity.class);

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                Bundle bundle = new Bundle();
                if (STC_check.isChecked()) {
                    bundle.putBoolean("STCorN", true);
                    Log.d("l-STCorN", true + "");
                }
                if (ARC_check.isChecked()) {
                    bundle.putBoolean("STCorN", false);
                    bundle.putFloat("ARC", ARC[1]);
                    Log.d("l-STCorN", false + "");
                }

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        check();
    }

    public void check() {
        File file;
        for (int i = 0; i < 9; i++) {
            file = new File("/sdcard/" + filenames[i]);
            if (file.exists()) {
                set_read_color(i);
            }
        }
        for (int i = 0; i < 9; i++) {
            check_all = check_all & check[i];
        }

        if (check_all) {
            launchAR.setEnabled(true);
        }
    }

    public void set_read_color(int n) {
        check[n] = true;
        switch (n) {
            case 0:
                skull_check.setTextColor(Color.GREEN);
                break;
            case 1:
                maxiila_check.setTextColor(Color.GREEN);
                break;
            case 2:
                mandible_check.setTextColor(Color.GREEN);
                break;
            case 3:
                osp1_check.setTextColor(Color.GREEN);
                break;
            case 4:
                osp2_check.setTextColor(Color.GREEN);
                break;
            case 5:
                project_check.setTextColor(Color.GREEN);
                break;
            case 6:
                arMarker_check.setTextColor(Color.GREEN);
                break;
            case 7:
                STC_check.setTextColor(Color.GREEN);
                break;
            case 8:
                ARC_check.setTextColor(Color.GREEN);
                break;
        }
    }
}
