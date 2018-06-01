package com.xinyi.studyabroad.activities;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xinyi.studyabroad.R;
import com.xinyi.studyabroad.base.BaseActivity;
import com.xinyi.studyabroad.callBack.DialogCallBack;
import com.xinyi.studyabroad.callBack.HandleResponse;
import com.xinyi.studyabroad.constants.AppUrls;
import com.xinyi.studyabroad.constants.Configer;
import com.xinyi.studyabroad.utils.DoParams;
import com.xinyi.studyabroad.utils.SpUtils;
import com.xinyi.studyabroad.utils.StatusBarUtil;
import com.xinyi.studyabroad.utils.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 登录
 */
public class LoginActivity extends BaseActivity {

    @BindView(R.id.resigt_textview)
    TextView resigt_textview;

    @BindView(R.id.account_et)
    EditText account_et;

    @BindView(R.id.password_et)
    EditText password_et;

    @BindView(R.id.login_btn)
    Button login_btn;

    private LocalBroadcastManager localBroadcastManager;//本地广播manager

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        StatusBarUtil.StatusBarDarkMode(this);
        StatusBarUtil.transparencyBar(this);
        ButterKnife.bind(this);
        initViews();
        initDatas();

    }

    @Override
    protected void initViews() {
        super.initViews();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        resigt_textview.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线

        resigt_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(LoginActivity.this, RegistActivity.class);
                startActivity(it);
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

    }

    @Override
    protected void initDatas() {
        super.initDatas();
    }

    /**
     *
     */
    private void login() {
        String account = account_et.getText().toString().trim();
        String psw = password_et.getText().toString().trim();
        if (TextUtils.isEmpty(account) || TextUtils.isEmpty(psw)) {
            showToast(R.string.loginNoCompeleteString);
            return;
        }
        HttpParams params = new HttpParams();
        params.put("user_code", account);
        params.put("password", psw);
        OkGo.<String>post(AppUrls.LoginUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(LoginActivity.this, params, ""))
                .execute(new DialogCallBack(LoginActivity.this, true) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject js = new JSONObject(response.body());

                            if (js.getBoolean("result")) {
                                savaUserInfo(js.getJSONObject("data"));
                                senLocalBroadCast();
                                finish();
                            }
                            UIHelper.toastMsg(js.getString("message"));
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
                        HandleResponse.handleException(response, LoginActivity.this);
                    }
                });
    }


    private void savaUserInfo(JSONObject jsonObject) throws JSONException {
        SpUtils.put(this, SpUtils.USERNAME, jsonObject.getString("name"));
        SpUtils.put(this, SpUtils.USEREMAIL, jsonObject.getString("email"));
        SpUtils.put(this, SpUtils.USETELEPHONE, jsonObject.getString("telephone"));
        SpUtils.put(this, SpUtils.USERAPP_CODE, jsonObject.getString("app_code"));
        SpUtils.put(this, SpUtils.USERLOGIN_TYPE, jsonObject.getString("login_type"));
        SpUtils.put(this, SpUtils.USERTRUE_NAME, jsonObject.getString("true_name"));
        SpUtils.put(this, SpUtils.USERCONFIRM_FLAG, jsonObject.getString("confirm_flag"));
        SpUtils.put(this, SpUtils.USERPAYMENT, jsonObject.getString("payment"));
        SpUtils.put(this, SpUtils.USERPAYMENT_NO, jsonObject.getString("payment_no"));
        SpUtils.put(this, SpUtils.USERIDENTITY_FLAG, jsonObject.getString("identity_flag"));
        SpUtils.put(this, SpUtils.USERGRADE, jsonObject.getString("grade"));
        SpUtils.put(this, SpUtils.USERPROFESSION_ID, jsonObject.getString("profession_id"));
        SpUtils.put(this, SpUtils.USERPROFESSIONAL_NAME, jsonObject.getString("professional_name"));
        SpUtils.put(this, SpUtils.USERSCHOOL_ID, jsonObject.getString("school_id"));
        SpUtils.put(this, SpUtils.USERSCHOOL_NAME, jsonObject.getString("school_name"));
        SpUtils.put(this, SpUtils.USERSTANDARD_ID, jsonObject.getString("standard_id"));
        SpUtils.put(this, SpUtils.USERSERVICE_NAME, jsonObject.getString("service_name"));
        SpUtils.put(this, SpUtils.USERSERVICE_PRICE, jsonObject.getString("service_price"));
        SpUtils.put(this, SpUtils.USERCHARGE_UNIT, jsonObject.getString("charge_unit"));
        SpUtils.put(this, SpUtils.USERCONTACT, jsonObject.getString("contact"));
        SpUtils.put(this, SpUtils.USERCOMPANY_ADDRESS, jsonObject.getString("company_address"));
        SpUtils.put(this, SpUtils.USERVIDO_ADDRESS, jsonObject.getString("vido_address"));
        SpUtils.put(this, SpUtils.USERTOTAL_AMOUNT, jsonObject.getString("total_amount"));
        SpUtils.put(this, SpUtils.USERUSER_TOKEN, jsonObject.getString("user_token"));
        SpUtils.put(this, SpUtils.USERINVITER_USER_TOKEN, jsonObject.getString("inviter_user_token"));
        SpUtils.put(this, SpUtils.USERPOINTS, jsonObject.getString("points"));
        SpUtils.put(this, SpUtils.USECREATED, jsonObject.getString("created"));
        SpUtils.put(this, SpUtils.USEMODIFIED, jsonObject.getString("modified"));
        SpUtils.put(this, SpUtils.USEIMAGE, jsonObject.getString("image"));
        SpUtils.put(this, SpUtils.USECARD_IMAGE, jsonObject.getString("card_image"));

    }

    private void senLocalBroadCast() {
        Intent intent = new Intent();
        intent.setAction(Configer.LOCAL_USERLOGIN_ACTION);
        localBroadcastManager.sendBroadcast(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!TextUtils.isEmpty((String) SpUtils.get(this, SpUtils.USERUSER_TOKEN, ""))) {
            Intent it = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(it);
            finish();
        }
    }
}
