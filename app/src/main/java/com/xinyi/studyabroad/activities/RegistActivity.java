package com.xinyi.studyabroad.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.xinyi.studyabroad.R;
import com.xinyi.studyabroad.base.BaseActivity;
import com.xinyi.studyabroad.base.StudyAbroadApp;
import com.xinyi.studyabroad.callBack.DialogCallBack;
import com.xinyi.studyabroad.callBack.HandleResponse;
import com.xinyi.studyabroad.constants.AppUrls;
import com.xinyi.studyabroad.constants.Configer;
import com.xinyi.studyabroad.utils.DoParams;
import com.xinyi.studyabroad.utils.StatusBarUtil;
import com.xinyi.studyabroad.utils.UIHelper;
import com.xinyi.studyabroad.weight.FaultTypePopupwindow;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Response;

/**
 * 注册
 */
public class RegistActivity extends BaseActivity {

    @BindView(R.id.parentView)
    LinearLayout parentView;

    @BindView(R.id.registForTelTv)
    TextView registForTelTv;//手机注册

    @BindView(R.id.layout_for_tel)
    LinearLayout layout_for_tel;

    @BindView(R.id.registForMailTv)
    TextView registForMailTv;//邮箱注册

    @BindView(R.id.layout_for_mail)
    LinearLayout layout_for_mail;

    //手机号码
    @BindView(R.id.tel_et)
    EditText tel_et;
    //验证码
    @BindView(R.id.code_et)
    EditText code_et;
    //邮箱地址
    @BindView(R.id.mail_et)
    EditText mail_et;
    //密码
    @BindView(R.id.psw_et)
    EditText psw_et;
    //确认密码
    @BindView(R.id.confirmpsw_et)
    EditText confirmpsw_et;
    //真实姓名
    @BindView(R.id.realName_et)
    EditText realName_et;
    //身份
    @BindView(R.id.identity_layout)
    LinearLayout identity_layout;
    @BindView(R.id.identity_et)
    EditText identity_et;
    //推荐人
    @BindView(R.id.referee_et)
    EditText referee_et;
    //获取验证码
    @BindView(R.id.getCodeTv)
    TextView getCodeTv;
    @BindView(R.id.regist_btn)
    Button regist_btn;

    private StudyAbroadApp app;
    private List<EditText> editTexts;
    private List<String> values;
    private LocalBroadcastManager localBroadcastManager;//本地广播manager
    private mBroadcastReceiver mReceiver;//接受倒计时

    private boolean flag = true;

    private int registType = 0;//0:手机注册 1:邮箱注册

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        StatusBarUtil.StatusBarLightMode(this);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        initViews();
        initListener();
        initDatas();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registBroadCastReceive();//注册
    }

    @Override
    protected void onPause() {
        super.onPause();
        unRegistBroadCastReceive();//解除注册
    }

    @Override
    protected void initViews() {
        super.initViews();

        app = (StudyAbroadApp) getApplication();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        mReceiver = new mBroadcastReceiver();

        initTitle(R.string.registString);
        hideRightTv();

        editTexts = new ArrayList<>();
        values = new ArrayList<>();
        editTexts.add(tel_et);
        editTexts.add(code_et);
        editTexts.add(mail_et);
        editTexts.add(psw_et);
        editTexts.add(confirmpsw_et);
        editTexts.add(realName_et);
        editTexts.add(identity_et);
        editTexts.add(referee_et);
        setSelected(0);
    }


    private void initListener() {

        registForTelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelected(0);
            }
        });
        registForMailTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelected(1);
            }
        });

        for (int i = 0; i < editTexts.size(); i++) {
            final int currentP = i;
            values.add("");
            final EditText editText = editTexts.get(i);

            editText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editText != identity_et) {
                        UIHelper.showInputMethod(editText);
                    } else {
                        initFaultTypePopup();
                    }
                }
            });


            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    values.set(currentP, s.toString());
                }
            });
        }

        regist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkIsInputEmpty()) {
                    if (values.get(3).equals(values.get(4))) {
                        regist();
                    } else {
                        showToast(R.string.pswIsNotSameString);
                    }
                }
            }
        });

        getCodeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (app.isCountDowning()) {
                    return;
                }
                try {
                    getVerifyCode();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void setSelected(int which) {
        //0:手机注册 1：邮箱注册
        registType = which;
        if (which == 0) {
            registForTelTv.setSelected(true);
            registForMailTv.setSelected(false);
            layout_for_tel.setVisibility(View.VISIBLE);
            layout_for_mail.setVisibility(View.GONE);
        } else {
            registForTelTv.setSelected(false);
            registForMailTv.setSelected(true);
            layout_for_tel.setVisibility(View.GONE);
            layout_for_mail.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initDatas() {
        super.initDatas();
    }


    /**
     * 身份的popup
     */
    private String[] identityStrings;
    private FaultTypePopupwindow mPopupWindow;//选择身份

    private void initFaultTypePopup() {

        if (identityStrings == null) {
            identityStrings = getResources().getStringArray(R.array.identity_list);
        }


        mPopupWindow = new FaultTypePopupwindow(this, new TypeAdapter(), new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                identity_et.setText(identityStrings[i]);
                mPopupWindow.dismiss();
            }
        });
        mPopupWindow.setTitle(R.string.app_name, R.string.userRegistIdentityString);
        mPopupWindow.showAtLocation(parentView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    class TypeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return identityStrings.length;
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = LayoutInflater.from(RegistActivity.this).inflate(R.layout.type_popup_item, null);
            TextView tv = (TextView) view.findViewById(R.id.tvSubFilterName);
            tv.setText(identityStrings[i]);
            return view;
        }
    }


    /**
     * 注册广播
     */
    private void registBroadCastReceive() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Configer.LOCAL_COUNTDOWN_ACTION);
        localBroadcastManager.registerReceiver(mReceiver, intentFilter);
    }

    /**
     * 销毁广播
     */
    private void unRegistBroadCastReceive() {
        localBroadcastManager.unregisterReceiver(mReceiver);
    }


    /**
     * 请求获取验证码
     *
     * @throws JSONException
     */
    private void getVerifyCode() throws JSONException {

        if (TextUtils.isEmpty(values.get(0))) {
            showToast(R.string.telIsEmptyString);
            return;
        }

        HttpParams params = new HttpParams();
        params.put("telephone", values.get(0));
        OkGo.<String>post(AppUrls.GetidentifyingUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .params(DoParams.encryptionparams(RegistActivity.this, params, ""))
                .tag(this)
                .execute(new DialogCallBack(RegistActivity.this, false) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        app.startTimer(values.get(0));
                    }

                    @Override
                    public String convertResponse(Response response) throws Throwable {
                        HandleResponse.handleReponse(response);
                        return super.convertResponse(response);
                    }

                    @Override
                    public void onError(com.lzy.okgo.model.Response<String> response) {
                        super.onError(response);
                        HandleResponse.handleException(response, RegistActivity.this);
                    }
                });

    }


    private class mBroadcastReceiver extends BroadcastReceiver {

        //接收到广播后自动调用该方法
        @Override
        public void onReceive(Context context, Intent intent) {
            //写入接收广播后的操作
            if (intent.getAction().equals(Configer.LOCAL_COUNTDOWN_ACTION)) {
                int countDownNum = intent.getIntExtra(Configer.LOCAL_COUNTDOWN_KEY, 0);
                String telString = intent.getStringExtra(Configer.LOCAL_COUNTDOWN_TEL);

                if (TextUtils.isEmpty(tel_et.getText().toString()) && flag) {
                    tel_et.setText(telString);
                    tel_et.setSelection(telString.length());
                    flag = false;
                }

                if (countDownNum == 0) {
                    getCodeTv.setTextColor(getResources().getColor(R.color.colorMain));
                    getCodeTv.setText(R.string.userGetCodeString);
                } else {
                    getCodeTv.setTextColor(getResources().getColor(R.color.colorpinkColor));
                    getCodeTv.setText(countDownNum + "s");
                }
            }
        }
    }

    /**
     * 校验输入框是否内容为空
     */
    private boolean checkIsInputEmpty() {

        boolean result = true;
        //不校验最后一个
        for (int i = 0; i < editTexts.size() - 1; i++) {
            if (registType == 0) {
                //手机注册不校验邮箱
                if (i != 2) {
                    if (!checkEdittextContent(editTexts.get(i))) {
                        result = false;
                        break;
                    }
                }
            } else {
                //邮箱注册不校验手机号验证码
                if (i > 1) {
                    if (!checkEdittextContent(editTexts.get(i))) {
                        result = false;
                        break;
                    }
                }
            }
        }
        return result;
    }


    private boolean checkEdittextContent(EditText et) {
        if (TextUtils.isEmpty(et.getText().toString().trim())) {
            et.setHintTextColor(getResources().getColor(R.color.colorpinkColor));
            return false;
        } else {
            et.setHintTextColor(getResources().getColor(R.color.colorSearchHint));
            return true;
        }
    }

    /**
     * 注册
     */
    private void regist() {
        String url = AppUrls.TelephoneRegisterUrl;
        HttpParams params = new HttpParams();
        if (registType == 0) {
            url = AppUrls.TelephoneRegisterUrl;
            params.put("telephone", values.get(0));
            params.put("code", values.get(1));
        } else {
            url = AppUrls.EmailRegisterUrl;
            params.put("email", values.get(2));
        }
        params.put("password", values.get(3));
        params.put("true_name", values.get(5));
        if (values.get(6).equals(identityStrings[0])) {
            params.put("identity_flag", "1");
        } else if (values.get(6).equals(identityStrings[1])) {
            params.put("identity_flag", "2");
        } else {
            params.put("identity_flag", "3");
        }

        params.put("inviter_user_code", values.get(7));
        OkGo.<String>post(url)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(RegistActivity.this, params, ""))
                .execute(new DialogCallBack(RegistActivity.this, true) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        try {
                            JSONObject js = new JSONObject(response.body());
                            UIHelper.toastMsg(js.getString("message"));
                            if (js.getBoolean("result")) {
                                finish();
                            }
                        } catch (JSONException e) {
                            UIHelper.toastMsg(e.getMessage());
                        }
                    }

                    @Override
                    public String convertResponse(Response response) throws Throwable {
                        HandleResponse.handleReponse(response);
                        return super.convertResponse(response);
                    }

                    @Override
                    public void onError(com.lzy.okgo.model.Response<String> response) {
                        super.onError(response);
                        HandleResponse.handleException(response, RegistActivity.this);
                    }
                });

    }

}
