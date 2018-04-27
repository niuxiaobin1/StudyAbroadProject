package com.xinyi.studyabroad.activities;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xinyi.studyabroad.R;
import com.xinyi.studyabroad.adapter.TutorAdapter;
import com.xinyi.studyabroad.base.BaseActivity;
import com.xinyi.studyabroad.callBack.DialogCallBack;
import com.xinyi.studyabroad.callBack.HandleResponse;
import com.xinyi.studyabroad.constants.AppUrls;
import com.xinyi.studyabroad.utils.DoParams;
import com.xinyi.studyabroad.utils.JsonUtils;
import com.xinyi.studyabroad.utils.SpUtils;
import com.xinyi.studyabroad.utils.UIHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubScribeActivity extends BaseActivity {
    public static final String USER_TOKEN = "teacher_user_token";
    private String teacher_user_token = "";

    //导师信息
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.tutor_name)
    TextView tutor_name;
    @BindView(R.id.tutor_workInfo)
    TextView tutor_workInfo;
    @BindView(R.id.tutor_serviceInfo)
    TextView tutor_serviceInfo;
    @BindView(R.id.tutor_price)
    TextView tutor_price;
    @BindView(R.id.tutor_unit)
    TextView tutor_unit;
    @BindView(R.id.scoreLayout)
    ConstraintLayout scoreLayout;

    @BindView(R.id.date_grid)
    GridView date_grid;


    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private List<Map<String,String>> serviceDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_scribe);
        ButterKnife.bind(this);
        initViews();
        initDatas();
    }

    @Override
    protected void initViews() {
        super.initViews();
        initTitle(R.string.subscribeString);
        hideRightTv();
        serviceDate=new ArrayList<>();
        scoreLayout.setVisibility(View.GONE);
        teacher_user_token = getIntent().getStringExtra(USER_TOKEN);
    }

    @Override
    protected void initDatas() {
        super.initDatas();

        String dates = format.format(new Date(System.currentTimeMillis()));
        String user_token = (String) SpUtils.get(this, SpUtils.USERUSER_TOKEN, "");

        HttpParams params = new HttpParams();
        params.put("user_token", user_token);
        params.put("teacher_user_token", teacher_user_token);
        try {
            params.put("now_time", format.parse(dates).getTime() / 1000 + "");
        } catch (ParseException e) {
            UIHelper.toastMsg(e.getMessage());
        }
        super.initDatas();
        OkGo.<String>post(AppUrls.ServiceDetailUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .params(DoParams.encryptionparams(this, params, user_token))
                .tag(this)
                .execute(new DialogCallBack(SubScribeActivity.this, false) {
                    @Override
                    public void onSuccess(Response<String> response) {

                        try {
                            JSONObject result = new JSONObject(response.body());
                            if (result.getBoolean("result")) {
                                JSONObject data=result.getJSONObject("data");
                                //导师信息
                                JSONObject teacher_info=data.getJSONObject("teacher_info");
                                Glide.with(SubScribeActivity.this).load(teacher_info.getString("image")).into(imageView);
                                tutor_name.setText(teacher_info.getString("true_name"));
                                tutor_workInfo.setText(teacher_info.getString("school_name"));
                                tutor_serviceInfo.setText(teacher_info.getString("service_name"));
                                tutor_price.setText(teacher_info.getString("money_sign") +teacher_info.getString("service_price"));
                                tutor_unit.setText("/" +teacher_info.getString("charge_unit"));
                                //服务日期
                                serviceDate.clear();
                                JSONArray service_date=data.getJSONArray("service_date");
                                Log.e("nxb", JsonUtils.ArrayToList(service_date,
                                        new String[]{"service_date","id","order_flag",
                                        "timezone","service_times"}).toString());
                            } else {
                                UIHelper.toastMsg(result.getString("message"));
                            }
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
                        HandleResponse.handleException(response, SubScribeActivity.this);
                    }
                });

    }
}
