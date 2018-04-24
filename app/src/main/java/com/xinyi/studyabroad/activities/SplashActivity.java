package com.xinyi.studyabroad.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.xinyi.studyabroad.R;
import com.xinyi.studyabroad.base.BaseActivity;
import com.xinyi.studyabroad.utils.CommonUtils;
import com.xinyi.studyabroad.utils.SpUtils;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        checkLanguage();
        findViewById(R.id.rootView).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent it = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(it);
                finish();
            }
        }, 3000);
    }


    private void checkLanguage() {
        if (TextUtils.isEmpty((String) SpUtils.get(this, SpUtils.APPFirst, ""))) {
            //
            if (CommonUtils.getUserLocale(this) == null) {

            }
        }
        Log.e("nxb", CommonUtils.getCurrentLocale(this).getLanguage());
        Log.e("nxb", getResources().getConfiguration().locale.getLanguage());
    }

}
