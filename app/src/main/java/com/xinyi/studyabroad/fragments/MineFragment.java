package com.xinyi.studyabroad.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xinyi.studyabroad.R;
import com.xinyi.studyabroad.activities.LoginActivity;
import com.xinyi.studyabroad.activities.PersonSettingsActivity;
import com.xinyi.studyabroad.base.BaseFragment;
import com.xinyi.studyabroad.callBack.DialogCallBack;
import com.xinyi.studyabroad.callBack.HandleResponse;
import com.xinyi.studyabroad.constants.AppUrls;
import com.xinyi.studyabroad.constants.Configer;
import com.xinyi.studyabroad.utils.DoParams;
import com.xinyi.studyabroad.utils.GlideCircleTransform;
import com.xinyi.studyabroad.utils.SpUtils;
import com.xinyi.studyabroad.utils.StatusBarUtil;
import com.xinyi.studyabroad.utils.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 我的
 * {@link MineFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MineFragment extends BaseFragment {

    private LocalBroadcastManager localBroadcastManager;//本地广播manager
    private LoginBroadcastReceiver mReceiver;

    @BindView(R.id.back_image)
    ImageView back_image;


    @BindView(R.id.title_tv)
    TextView title_tv;

    @BindView(R.id.right_tv)
    TextView right_tv;

    @BindView(R.id.userInfoLayout)
    LinearLayout userInfoLayout;

    //个人信息
    @BindView(R.id.imageHeader)
    ImageView imageHeader;

    @BindView(R.id.topTextView)
    TextView topTextView;

    @BindView(R.id.bottomTextView)
    TextView bottomTextView;
    //预约管理
    @BindView(R.id.subscribeLayout)
    LinearLayout subscribeLayout;
    //支付管理
    @BindView(R.id.payManagerLayout)
    LinearLayout payManagerLayout;
    //时间管理
    @BindView(R.id.timeManagerLayout)
    LinearLayout timeManagerLayout;
    //我的钱包
    @BindView(R.id.mypauseLayout)
    RelativeLayout mypauseLayout;
    //我的评价
    @BindView(R.id.myevaluationLayout)
    RelativeLayout myevaluationLayout;
    //我的关注
    @BindView(R.id.myfllowLayout)
    RelativeLayout myfllowLayout;
    //客服
    @BindView(R.id.serverLayout)
    RelativeLayout serverLayout;
    //设置
    @BindView(R.id.settingsLayout)
    RelativeLayout settingsLayout;

    @BindView(R.id.parentView)
    LinearLayout parentView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MineFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MineFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MineFragment newInstance(String param1, String param2) {
        MineFragment fragment = new MineFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registBroadCastReceive();//注册
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_mine, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void initViews() {
        back_image.setVisibility(View.GONE);
        right_tv.setVisibility(View.GONE);
        title_tv.setText(R.string.footer_mineString);
        title_tv.setTextColor(getResources().getColor(R.color.colorWhite));
        parentView.setPadding(0, StatusBarUtil.getStatusBarHeight(getActivity()), 0, 0);

        userInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUserLayoutClick();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isHidden()) {
            checkIsLogin();
        }
    }

    private void onUserLayoutClick() {
        String user_token = (String) SpUtils.get(getActivity(), SpUtils.USERUSER_TOKEN, "");
        Intent it = null;
        if (TextUtils.isEmpty(user_token)) {
            it = new Intent(getActivity(), LoginActivity.class);
        } else {
            it = new Intent(getActivity(), PersonSettingsActivity.class);
        }
        startActivity(it);
    }

    private void checkIsLogin() {
        String user_token = (String) SpUtils.get(getActivity(), SpUtils.USERUSER_TOKEN, "");
        if (TextUtils.isEmpty(user_token)) {
            //未登录
            Glide.with(getActivity()).load(R.mipmap.ic_launcher).transform(new CenterCrop(getActivity()),
                    new GlideCircleTransform(getActivity())).into(imageHeader);
            topTextView.setText(R.string.clickLoginString);
            bottomTextView.setVisibility(View.GONE);
        } else {
            //登录
            String headerUrl = (String) SpUtils.get(getActivity(), SpUtils.USEIMAGE, "");
            String name = (String) SpUtils.get(getActivity(), SpUtils.USERNAME, "");
            String trueName = (String) SpUtils.get(getActivity(), SpUtils.USERTRUE_NAME, "");
            Glide.with(getActivity()).load(headerUrl).transform(new CenterCrop(getActivity()),
                    new GlideCircleTransform(getActivity())).into(imageHeader);
            topTextView.setText(name);
            bottomTextView.setVisibility(View.VISIBLE);
            bottomTextView.setText(trueName);
        }
        String identity_flag = (String) SpUtils.get(getActivity(), SpUtils.USERIDENTITY_FLAG, "");
        if (identity_flag.equals("1")) {
            switchLayout(0);
        } else {
            switchLayout(1);
        }
        initUserInfo();

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            checkIsLogin();
        }

    }

    /**
     * 0：学生 1：老师和机构
     */
    private void switchLayout(int which) {
        if (which == 0) {
            payManagerLayout.setVisibility(View.VISIBLE);
            timeManagerLayout.setVisibility(View.GONE);
            myfllowLayout.setVisibility(View.VISIBLE);
            myevaluationLayout.setVisibility(View.GONE);

        } else {
            payManagerLayout.setVisibility(View.GONE);
            timeManagerLayout.setVisibility(View.VISIBLE);
            myfllowLayout.setVisibility(View.GONE);
            myevaluationLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void initDatas() {

    }

    private void initUserInfo() {

        String user_token = (String) SpUtils.get(getActivity(), SpUtils.USERUSER_TOKEN, "");
        if (TextUtils.isEmpty(user_token)) {
            return;
        }

        HttpParams params = new HttpParams();
        params.put("user_token", user_token);
        OkGo.<String>post(AppUrls.UseInfoUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(getActivity(), params, user_token))
                .execute(new DialogCallBack(getActivity(), false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject js = new JSONObject(response.body());

                            if (js.getBoolean("result")) {
                                savaUserInfo(js.getJSONObject("data").getJSONObject("user"));
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
                        HandleResponse.handleException(response, getActivity());
                    }
                });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * 注册广播
     */
    private void registBroadCastReceive() {
        localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        mReceiver = new LoginBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Configer.LOCAL_USERLOGIN_ACTION);
        localBroadcastManager.registerReceiver(mReceiver, intentFilter);
    }

    /**
     * 销毁广播
     */
    private void unRegistBroadCastReceive() {
        localBroadcastManager.unregisterReceiver(mReceiver);
    }

    private class LoginBroadcastReceiver extends BroadcastReceiver {

        //接收到广播后自动调用该方法
        @Override
        public void onReceive(Context context, Intent intent) {
            //写入接收广播后的操作
            if (intent.getAction().equals(Configer.LOCAL_USERLOGIN_ACTION)) {
                checkIsLogin();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegistBroadCastReceive();//解除注册
    }

    private void savaUserInfo(JSONObject jsonObject) throws JSONException {
        SpUtils.put(getActivity(), SpUtils.USERNAME, jsonObject.getString("name"));
        SpUtils.put(getActivity(), SpUtils.USEREMAIL, jsonObject.getString("email"));
        SpUtils.put(getActivity(), SpUtils.USETELEPHONE, jsonObject.getString("telephone"));
        SpUtils.put(getActivity(), SpUtils.USERAPP_CODE, jsonObject.getString("app_code"));
        SpUtils.put(getActivity(), SpUtils.USERLOGIN_TYPE, jsonObject.getString("login_type"));
        SpUtils.put(getActivity(), SpUtils.USERTRUE_NAME, jsonObject.getString("true_name"));
        SpUtils.put(getActivity(), SpUtils.USERCONFIRM_FLAG, jsonObject.getString("confirm_flag"));
        SpUtils.put(getActivity(), SpUtils.USERPAYMENT, jsonObject.getString("payment"));
        SpUtils.put(getActivity(), SpUtils.USERPAYMENT_NO, jsonObject.getString("payment_no"));
        SpUtils.put(getActivity(), SpUtils.USERIDENTITY_FLAG, jsonObject.getString("identity_flag"));
        SpUtils.put(getActivity(), SpUtils.USERGRADE, jsonObject.getString("grade"));
        SpUtils.put(getActivity(), SpUtils.USERPROFESSION_ID, jsonObject.getString("profession_id"));
        SpUtils.put(getActivity(), SpUtils.USERPROFESSIONAL_NAME, jsonObject.getString("professional_name"));
        SpUtils.put(getActivity(), SpUtils.USERSCHOOL_ID, jsonObject.getString("school_id"));
        SpUtils.put(getActivity(), SpUtils.USERSCHOOL_NAME, jsonObject.getString("school_name"));
        SpUtils.put(getActivity(), SpUtils.USERSTANDARD_ID, jsonObject.getString("standard_id"));
        SpUtils.put(getActivity(), SpUtils.USERSERVICE_NAME, jsonObject.getString("service_name"));
        SpUtils.put(getActivity(), SpUtils.USERSERVICE_PRICE, jsonObject.getString("service_price"));
        SpUtils.put(getActivity(), SpUtils.USERCHARGE_UNIT, jsonObject.getString("charge_unit"));
        SpUtils.put(getActivity(), SpUtils.USERCONTACT, jsonObject.getString("contact"));
        SpUtils.put(getActivity(), SpUtils.USERCOMPANY_ADDRESS, jsonObject.getString("company_address"));
        SpUtils.put(getActivity(), SpUtils.USERVIDO_ADDRESS, jsonObject.getString("vido_address"));
        SpUtils.put(getActivity(), SpUtils.USERTOTAL_AMOUNT, jsonObject.getString("total_amount"));
        SpUtils.put(getActivity(), SpUtils.USERUSER_TOKEN, jsonObject.getString("user_token"));
        SpUtils.put(getActivity(), SpUtils.USERINVITER_USER_TOKEN, jsonObject.getString("inviter_user_token"));
        SpUtils.put(getActivity(), SpUtils.USERPOINTS, jsonObject.getString("points"));
        SpUtils.put(getActivity(), SpUtils.USECREATED, jsonObject.getString("created"));
        SpUtils.put(getActivity(), SpUtils.USEMODIFIED, jsonObject.getString("modified"));
        SpUtils.put(getActivity(), SpUtils.USEIMAGE, jsonObject.getString("image"));
        SpUtils.put(getActivity(), SpUtils.USECARD_IMAGE, jsonObject.getString("card_image"));

    }

}
