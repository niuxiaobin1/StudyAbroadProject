package com.xinyi.studyabroad.base;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xinyi.studyabroad.R;
import com.xinyi.studyabroad.utils.AppManager;
import com.xinyi.studyabroad.utils.StatusBarUtil;

import java.util.Locale;

import butterknife.ButterKnife;

import static com.xinyi.studyabroad.utils.AppContext.getResources;

public class BaseActivity extends AppCompatActivity {
    private ImageView back_image;
    private TextView title_tv;
    private TextView right_tv;

    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.transparencyBar(this);
        AppManager.addActivity(this);
    }


    protected void initViews(){
        back_image=findViewById(R.id.back_image);
        title_tv=findViewById(R.id.title_tv);
        right_tv=findViewById(R.id.right_tv);

        if (back_image!=null){
            back_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    protected void initTitle(@StringRes int title){
        if (title_tv!=null){
            title_tv.setText(title);
        }
    }


    protected void initRightText(@StringRes int right){
        if (right_tv!=null){
            right_tv.setText(right);
        }
    }

    protected void initRightIcon(@DrawableRes int bg){
        if (right_tv!=null){
            right_tv.setBackgroundResource(bg);
        }
    }
    protected void hideRightTv(){
        if (right_tv!=null){
            right_tv.setVisibility(View.GONE);
        }
    }

    protected void setOnRightClickListener(View.OnClickListener listener){
        if (right_tv!=null){
            right_tv.setOnClickListener(listener);
        }
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
