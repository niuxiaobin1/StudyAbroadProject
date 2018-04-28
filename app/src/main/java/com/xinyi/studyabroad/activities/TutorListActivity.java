package com.xinyi.studyabroad.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xinyi.studyabroad.R;
import com.xinyi.studyabroad.adapter.TutuorFragmentAdapter;
import com.xinyi.studyabroad.base.BaseActivity;
import com.xinyi.studyabroad.callBack.DialogCallBack;
import com.xinyi.studyabroad.callBack.HandleResponse;
import com.xinyi.studyabroad.constants.AppUrls;
import com.xinyi.studyabroad.utils.DensityUtil;
import com.xinyi.studyabroad.utils.DividerDecoration;
import com.xinyi.studyabroad.utils.DoParams;
import com.xinyi.studyabroad.utils.JsonUtils;
import com.xinyi.studyabroad.utils.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TutorListActivity extends BaseActivity {

    public static final String SEARCH_KEY = "_search_keyword";
    private String key = "";

    @BindView(R.id.key_tv)
    TextView key_tv;

    @BindView(R.id.recylerView)
    RecyclerView recylerView;

    private TutuorFragmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_list);
        ButterKnife.bind(this);
        initViews();
        initDatas();
    }

    @Override
    protected void initViews() {
        super.initViews();
        key = getIntent().getStringExtra(SEARCH_KEY);
        if (!TextUtils.isEmpty(key)) {
            key_tv.setText(key);
        }

        key_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(TutorListActivity.this, SearchActivity.class);
                startActivity(it);
            }
        });
        adapter = new TutuorFragmentAdapter(this);
        recylerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recylerView.addItemDecoration(new DividerDecoration(this, R.color.colorLine,
                DensityUtil.dip2px(this, 1)));
        recylerView.setAdapter(adapter);
    }

    @Override
    protected void initDatas() {
        super.initDatas();

        HttpParams params = new HttpParams();
        params.put("key_words", key);
        OkGo.<String>post(AppUrls.CusomerSearchUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(TutorListActivity.this, params, ""))
                .execute(new DialogCallBack(TutorListActivity.this, true) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject js = new JSONObject(response.body());

                            if (js.getBoolean("result")) {
                                adapter.addDatas(JsonUtils.ArrayToList(js.getJSONArray("data"),
                                        new String[]{"school_name", "professional_name", "true_name", "service_name"
                                                , "service_price", "points", "user_token", "image", "money_sign", "charge_unit"}));
                            } else {
                                UIHelper.toastMsg(js.getString("message"));
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
                        HandleResponse.handleException(response, TutorListActivity.this);
                    }
                });
    }
}
