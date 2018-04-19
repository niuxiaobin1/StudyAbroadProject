package com.xinyi.studyabroad.base;

import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.xinyi.studyabroad.R;
import com.xinyi.studyabroad.utils.AppManager;
import com.xinyi.studyabroad.utils.StatusBarUtil;

import butterknife.ButterKnife;

public class BaseActivity extends AppCompatActivity {

    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.transparencyBar(this);
        AppManager.addActivity(this);

    }


    protected void initViews(){

    }
    protected void initDatas(){

    }

    protected void showToast(@StringRes int stringRes){
        if (toast==null){

            toast=Toast.makeText(this,stringRes,Toast.LENGTH_SHORT);
        }else{
            toast.setText(stringRes);
        }
        toast.show();
    }
}
