package com.xinyi.studyabroad.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xinyi.studyabroad.PullRefreshLayout.PullRefreshLayout;
import com.xinyi.studyabroad.R;
import com.xinyi.studyabroad.adapter.EvaluationAdapter;
import com.xinyi.studyabroad.base.BaseActivity;
import com.xinyi.studyabroad.callBack.DialogCallBack;
import com.xinyi.studyabroad.callBack.HandleResponse;
import com.xinyi.studyabroad.constants.AppUrls;
import com.xinyi.studyabroad.utils.DensityUtil;
import com.xinyi.studyabroad.utils.DividerDecoration;
import com.xinyi.studyabroad.utils.DoParams;
import com.xinyi.studyabroad.utils.JsonUtils;
import com.xinyi.studyabroad.utils.SpUtils;
import com.xinyi.studyabroad.utils.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyEvaluateListActivity extends BaseActivity {

    @BindView(R.id.refresh_layout)
    PullRefreshLayout refresh_layout;

    @BindView(R.id.recylerView)
    RecyclerView recylerView;

    private EvaluationAdapter adapter;
    private int page=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_evaluate_list);
        ButterKnife.bind(this);
        initViews();
        initDatas();
    }

    @Override
    protected void initViews() {
        super.initViews();
        hideRightTv();
        initTitle(R.string.myevaluationString);

        refresh_layout.setMode(PullRefreshLayout.DISABLED);
        recylerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recylerView.addItemDecoration(new DividerDecoration(this, R.color.colorLineE0D, DensityUtil.dip2px(
                this, 0.5f
        )));

        adapter=new EvaluationAdapter(this);
        recylerView.setAdapter(adapter);
    }

    @Override
    protected void initDatas() {
        super.initDatas();

        String user_token = (String) SpUtils.get(this, SpUtils.USERUSER_TOKEN, "");
        HttpParams params = new HttpParams();
        params.put("user_token", user_token);
        params.put("page", String.valueOf(page));
        OkGo.<String>post(AppUrls.CustomerMy_evaluationUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .params(DoParams.encryptionparams(MyEvaluateListActivity.this, params, user_token))
                .tag(this)
                .execute(new DialogCallBack((Activity) MyEvaluateListActivity.this, true) {
                    @Override
                    public void onSuccess(Response<String> response) {

                        try {
                            JSONObject result = new JSONObject(response.body());
                            if (result.getBoolean("result")) {
                                adapter.addDatas(JsonUtils.ArrayToList(
                                        result.getJSONArray("data"),new String[]{
                                                "true_name", "image", "content", "stars", "created","service_name"
                                        }
                                ));
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
                        HandleResponse.handleException(response, MyEvaluateListActivity.this);
                    }
                });
    }
}
