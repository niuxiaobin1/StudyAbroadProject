package com.xinyi.studyabroad.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xinyi.studyabroad.R;
import com.xinyi.studyabroad.adapter.BaseAdapter;
import com.xinyi.studyabroad.adapter.OrderAdapter;
import com.xinyi.studyabroad.adapter.OrderTeacherAdapter;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 我的预约
 */
public class OrderManagerActivity extends BaseActivity {

    @BindView(R.id.order_recylerView)
    RecyclerView order_recylerView;

    private BaseAdapter adapter;
    private String identity_flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_manager);
        ButterKnife.bind(this);
        initViews();
        initDatas();
    }


    @Override
    protected void initViews() {
        super.initViews();
        initTitle(R.string.orderListTitleString);
        initRightText(R.string.editorString);
        identity_flag = (String) SpUtils.get(this, SpUtils.USERIDENTITY_FLAG, "");
        order_recylerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        order_recylerView.addItemDecoration(new DividerDecoration(this, R.color.colorLineE0D, DensityUtil.dip2px(
                this, 5
        )));
        if (identity_flag.equals("1")) {
            adapter = new OrderAdapter(this);
        } else {
            adapter = new OrderTeacherAdapter(this);
        }

        order_recylerView.setAdapter(adapter);
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        String user_token = (String) SpUtils.get(this, SpUtils.USERUSER_TOKEN, "");
        HttpParams params = new HttpParams();
        params.put("user_token", user_token);
        OkGo.<String>post(AppUrls.OrderSearchUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .params(DoParams.encryptionparams(this, params, user_token))
                .tag(this)
                .execute(new DialogCallBack(OrderManagerActivity.this, true) {
                    @Override
                    public void onSuccess(Response<String> response) {

                        try {
                            JSONObject result = new JSONObject(response.body());
                            if (result.getBoolean("result")) {
                                JSONArray data = result.getJSONArray("data");

                                if (identity_flag.equals("1")) {
                                    adapter.addDatas(JsonUtils.ArrayToList(data, new String[]{
                                            "order_code", "order_status", "service_date", "service_start_time"
                                            , "service_end_time", "length_time", "price", "image"
                                            , "true_name", "service_price", "money_sign", "charge_unit", "status_name"
                                    }));

                                } else {
                                    adapter.addDatas(JsonUtils.ArrayToList(data, new String[]{
                                            "order_code", "order_status", "service_date", "service_start_time"
                                            , "service_end_time", "length_time", "price", "image"
                                            , "true_name", "ask_content", "money_sign", "status_name"
                                    }));
                                }


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
                        HandleResponse.handleException(response, OrderManagerActivity.this);
                    }
                });
    }
}
