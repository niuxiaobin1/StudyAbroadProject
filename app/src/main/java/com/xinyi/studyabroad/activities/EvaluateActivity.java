package com.xinyi.studyabroad.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xinyi.studyabroad.R;
import com.xinyi.studyabroad.base.BaseActivity;
import com.xinyi.studyabroad.callBack.DialogCallBack;
import com.xinyi.studyabroad.callBack.HandleResponse;
import com.xinyi.studyabroad.constants.AppUrls;
import com.xinyi.studyabroad.utils.DoParams;
import com.xinyi.studyabroad.utils.SpUtils;
import com.xinyi.studyabroad.utils.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 评价
 */
public class EvaluateActivity extends BaseActivity {

    public static final String ORDER_CODE = "order_code";


    //匿名评价
    @BindView(R.id.anonymousTv)
    TextView anonymousTv;

    @BindView(R.id.edittext)
    EditText edittext;

    @BindView(R.id.commit_btn)
    Button commit_btn;

    @BindView(R.id.describeRatingBar)
    SimpleRatingBar describeRatingBar;

    @BindView(R.id.wholeRatingBar)
    SimpleRatingBar wholeRatingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluate);
        ButterKnife.bind(this);
        initViews();
        initDatas();
    }

    @Override
    protected void initViews() {
        super.initViews();
        hideRightTv();
        initTitle(R.string.evaluateString);
        edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showInputMethod(edittext);
            }
        });

        anonymousTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anonymousTv.setSelected(!anonymousTv.isSelected());
            }
        });

        commit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCommit();
            }
        });

    }


    private void doCommit() {
        String content = edittext.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            showToast(R.string.emptyAlertString);
            return;
        }
        String user_token = (String) SpUtils.get(this, SpUtils.USERUSER_TOKEN, "");
        HttpParams params = new HttpParams();
        params.put("user_token", user_token);
        params.put("order_code", getIntent().getStringExtra(ORDER_CODE));
        params.put("button_status", "evaluated");
        params.put("stars", wholeRatingBar.getRating());
        params.put("content", content);
        params.put("anonymous", anonymousTv.isSelected() ? "1" : "0");
        OkGo.<String>post(AppUrls.OrderUpdateUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .params(DoParams.encryptionparams(EvaluateActivity.this, params, user_token))
                .tag(this)
                .execute(new DialogCallBack((Activity) EvaluateActivity.this, true) {
                    @Override
                    public void onSuccess(Response<String> response) {

                        try {
                            JSONObject result = new JSONObject(response.body());
                            if (result.getBoolean("result")) {
                                finish();
                            } else {
                            }
                            UIHelper.toastMsg(result.getString("message"));
                        } catch (JSONException e) {
                            UIHelper.toastMsg(e.getMessage());
                        }

                    }

                    @Override
                    public String convertResponse(okhttp3.Response response) throws Throwable {
                        HandleResponse.handleReponse(response);
                        return super.convertResponse(response);
                    }


                    @Override
                    public void onError(com.lzy.okgo.model.Response<String> response) {
                        super.onError(response);
                        HandleResponse.handleException(response, EvaluateActivity.this);
                    }
                });
    }
}
