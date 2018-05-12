package com.xinyi.studyabroad.activities;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xinyi.studyabroad.R;
import com.xinyi.studyabroad.adapter.PayMethdoAdapter;
import com.xinyi.studyabroad.adapter.TutorAdapter;
import com.xinyi.studyabroad.base.BaseActivity;
import com.xinyi.studyabroad.callBack.DialogCallBack;
import com.xinyi.studyabroad.callBack.HandleResponse;
import com.xinyi.studyabroad.constants.AppUrls;
import com.xinyi.studyabroad.utils.CommonUtils;
import com.xinyi.studyabroad.utils.DensityUtil;
import com.xinyi.studyabroad.utils.DividerDecoration;
import com.xinyi.studyabroad.utils.DoParams;
import com.xinyi.studyabroad.utils.JsonUtils;
import com.xinyi.studyabroad.utils.SpUtils;
import com.xinyi.studyabroad.utils.UIHelper;
import com.xinyi.studyabroad.weight.TimeSotPopupWindow;

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

public class SubScribeActivity extends BaseActivity implements View.OnClickListener {
    public static final String USER_TOKEN = "teacher_user_token";
    private static final int TYPE_DATE = 0;
    private static final int TYPE_TIME = 1;
    private static final int MIN_TIME = 30;//最短选择时长
    private static final int MIN_PER_TIME = 10;//最小增加时长
    private String teacher_user_token = "";


    @BindView(R.id.parentView)
    LinearLayout parentView;
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
    //时间选择
    @BindView(R.id.date_grid)
    GridView date_grid;
    @BindView(R.id.time_grid)
    GridView time_grid;
    @BindView(R.id.time_select)
    TextView time_select;
    //确认预约
    @BindView(R.id.subscribe_commitTv)
    TextView subscribe_commitTv;
    //咨询内容
    @BindView(R.id.consulation_et)
    EditText consulation_et;
    //支付方式
    @BindView(R.id.payMethod_recylerView)
    RecyclerView payMethod_recylerView;
    //总价
    @BindView(R.id.totlaPriceTv)
    TextView totlaPriceTv;

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat dayFormat = new SimpleDateFormat("MM.dd");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");


    private int dateSelectPostion = 0;//当前日期选中的位置
    private int timeSelectPostion = 0;//当前时间段选中的位置
    //日期和时间的适配器
    private List<Map<String, String>> serviceDate;
    private List<Map<String, String>> servicetime;
    private DateTimeAdapter dateAdapter;
    private DateTimeAdapter timeAdapter;

    private String perHourPrice = "";//单价
    private String priceUnit = "$";
    private String totlaPrice = "0";//总价
    private String slotTime = "";//时长
    private String service_start_time = "";
    private PayMethdoAdapter payMethdoAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
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
        serviceDate = new ArrayList<>();
        servicetime = new ArrayList<>();
        scoreLayout.setVisibility(View.GONE);
        teacher_user_token = getIntent().getStringExtra(USER_TOKEN);

        dateAdapter = new DateTimeAdapter(TYPE_DATE);
        timeAdapter = new DateTimeAdapter(TYPE_TIME);
        date_grid.setAdapter(dateAdapter);
        //date selected
        date_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dateSelectPostion = position;
                dateAdapter.notifyDataSetChanged();
                timeSelectPostion = 0;
                JSONArray service_times = null;
                try {
                    service_times = new JSONArray(serviceDate.get(position).get("service_times"));
                    servicetime.clear();
                    servicetime = JsonUtils.ArrayToList(service_times, new String[]{
                            "service_start_time",
                            "service_end_time", "order_flag", "length_time"});
                    timeAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                }

            }
        });
        time_grid.setAdapter(timeAdapter);
        //time selected
        time_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                timeSelectPostion = position;
                timeAdapter.notifyDataSetChanged();
            }
        });
        //commit
        subscribe_commitTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(slotTime)) {
                    showToast(R.string.slotTimeEmptyString);
                    return;
                }
                if (TextUtils.isEmpty(consulation_et.getText().toString().trim())) {
                    showToast(R.string.askContentEmptyString);
                    return;
                }
                Map<String, String> payMap = payMethdoAdapter.getPayMethod();
                BalancePay(payMap);

            }
        });
        //咨询
        consulation_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showInputMethod(consulation_et);
            }
        });

        time_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimMinutesPopupWindow();
            }
        });

        payMethdoAdapter = new PayMethdoAdapter(this);
        payMethod_recylerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        payMethod_recylerView.addItemDecoration(new DividerDecoration(this, R.color.colorLine, DensityUtil
                .dip2px(this, 1)));
        payMethod_recylerView.setAdapter(payMethdoAdapter);
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
                                JSONObject data = result.getJSONObject("data");
                                //导师信息
                                JSONObject teacher_info = data.getJSONObject("teacher_info");
                                Glide.with(SubScribeActivity.this).load(teacher_info.getString("image")).into(imageView);
                                tutor_name.setText(teacher_info.getString("true_name"));
                                tutor_workInfo.setText(teacher_info.getString("school_name"));
                                tutor_serviceInfo.setText(teacher_info.getString("service_name"));
                                tutor_price.setText(teacher_info.getString("money_sign") + teacher_info.getString("service_price"));
                                tutor_unit.setText("/" + teacher_info.getString("charge_unit"));

                                perHourPrice = teacher_info.getString("service_price");
                                priceUnit = teacher_info.getString("money_sign");
                                //服务日期
                                serviceDate.clear();
                                JSONArray service_date = data.getJSONArray("service_date");
                                serviceDate = JsonUtils.ArrayToList(service_date,
                                        new String[]{"service_date", "id", "order_flag",
                                                "timezone", "service_times"});
                                JSONArray service_times = new JSONArray(serviceDate.get(0).get("service_times"));
                                servicetime = JsonUtils.ArrayToList(service_times, new String[]{
                                        "service_start_time",
                                        "service_end_time", "order_flag", "length_time"});
                                dateAdapter.notifyDataSetChanged();
                                timeAdapter.notifyDataSetChanged();
                                //支付方式
                                payMethdoAdapter.addDatas(JsonUtils.ArrayToList(
                                        data.getJSONArray("payment_list"), new String[]{
                                                "name", "image", "pfn"
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
                        HandleResponse.handleException(response, SubScribeActivity.this);
                    }
                });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sure_tv:
                mPopupWindow.dismiss();
                caculatorPrice(Integer.parseInt(mPopupWindow.getResult()[0]));
                time_select.setText(timeFormat.format(new Date(Long.parseLong(mPopupWindow.getResult()[1]) * 1000))
                        + "(" + mPopupWindow.getResult()[0]
                        + getResources().getString(R.string.minuteString) + ")");
                service_start_time = mPopupWindow.getResult()[1];

                break;
        }
    }


    private class DateTimeAdapter extends BaseAdapter {
        private int type;

        public String getSelectString() {
            return selectString;
        }

        public void setSelectString(String selectString) {
            this.selectString = selectString;
        }

        /**
         * 当前选择的时间
         */
        private String selectString;

        /**
         * type:0:选择日期 1：选择时间段
         *
         * @param type
         */
        public DateTimeAdapter(int type) {
            this.type = type;
        }

        @Override
        public int getCount() {
            if (type == TYPE_DATE) {
                return serviceDate.size();
            } else {
                return servicetime.size();
            }


        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = LayoutInflater.from(SubScribeActivity.this).inflate(R.layout.date_time_item, null);
            TextView content = v.findViewById(R.id.content_tv);
            Map<String, String> map = type == TYPE_DATE ? serviceDate.get(position) : servicetime.get(position);
            String text;
            if (type == TYPE_DATE) {
                content.setBackgroundResource(R.drawable.date_select);
                content.setTextColor(getResources().getColorStateList(R.color.date_selelct_color));
                if (position == dateSelectPostion) {
                    setSelectString(map.get("service_date"));
                    content.setSelected(true);
                } else {
                    content.setSelected(false);
                }
                text = dayFormat.format(new Date(Long.parseLong(map.get("service_date")) * 1000));
            } else {
                if (position == timeSelectPostion) {
                    setSelectString(map.get("service_start_time") + "-" + map.get("service_end_time"));
                    content.setTextColor(getResources().getColor(R.color.colorWhite));
                    content.setBackgroundResource(R.drawable.green_corner_bg);
                } else {
                    if (map.get("order_flag").equals("0")) {
                        content.setBackgroundResource(R.drawable.white_color_bg);
                        content.setTextColor(getResources().getColor(R.color.colorSubTitle));
                    } else {
                        content.setBackgroundResource(R.drawable.grey_corner_bg);
                        content.setTextColor(getResources().getColor(R.color.colorWhite));
                    }

                }

                text = timeFormat.format(new Date(Long.parseLong(map.get("service_start_time")) * 1000)) +
                        "-" + timeFormat.format(new Date(Long.parseLong(map.get("service_end_time")) * 1000));
            }

            content.setText(text);
            return v;
        }
    }

    private TimeSotPopupWindow mPopupWindow;

    private void showTimMinutesPopupWindow() {
        if (mPopupWindow != null) {
            mPopupWindow = null;
        }
        mPopupWindow = new TimeSotPopupWindow(this, this);
        initTimeSlotData();
        mPopupWindow.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);

    }

    /**
     * 计算时间数据
     */
    private void initTimeSlotData() {
        if (servicetime.size() == 0) {
            return;
        }
        Map<String, String> selectMap = servicetime.get(timeSelectPostion);
        String maxTime = selectMap.get("length_time");//单位分钟

        int count = (Integer.parseInt(maxTime) - MIN_TIME) / MIN_PER_TIME;
        List<String> timeSlot = new ArrayList<>();
        for (int i = 0; i < count + 1; i++) {
            timeSlot.add(String.valueOf(MIN_TIME + MIN_PER_TIME * i));
        }
        mPopupWindow.setDatas(timeSlot, selectMap.get("service_start_time"), selectMap.get("service_end_time"));
    }

    /**
     * 计算总价
     *
     * @param minutes
     */
    private void caculatorPrice(int minutes) {
        slotTime = String.valueOf(minutes);
        totlaPrice = String.format("%.2f", Double.parseDouble(perHourPrice) / 60 * minutes);
        totlaPriceTv.setText(priceUnit + totlaPrice);
    }

    /**
     * 生成订单
     */
    private void BalancePay(final Map<String, String> payMap) {
        String user_token = (String) SpUtils.get(this, SpUtils.USERUSER_TOKEN, "");
        HttpParams params = new HttpParams();
        params.put("user_token", user_token);
        params.put("teacher_user_token", teacher_user_token);
        params.put("service_date", dateAdapter.getSelectString());
        params.put("service_start_time", service_start_time);
        long endTime = Long.parseLong(service_start_time) + Integer.parseInt(slotTime) * 60;
        params.put("service_end_time", String.valueOf(endTime));
        params.put("length_time", slotTime);
        params.put("ask_content", consulation_et.getText().toString().trim());
        params.put("payment_method", payMap.get("name"));
        params.put("payment_pfn", payMap.get("pfn"));

        OkGo.<String>post(AppUrls.OrderConfirmUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .params(DoParams.encryptionparams(this, params, user_token))
                .tag(this)
                .execute(new DialogCallBack(SubScribeActivity.this, false) {
                    @Override
                    public void onSuccess(Response<String> response) {

                        try {
                            JSONObject result = new JSONObject(response.body());
                            if (result.getBoolean("result")) {

                                if (payMap.get("pfn").equals("Balances")) {
                                    //余额
                                    balancePay(result.getJSONObject("data"));
                                } else if (payMap.get("pfn").equals("WeChatPay")) {
                                    //微信

                                } else if (payMap.get("pfn").equals("Alipay")) {
                                    //支付宝
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
                        HandleResponse.handleException(response, SubScribeActivity.this);
                    }
                });
    }

    /**
     * 余额支付
     *
     * @param js
     * @throws JSONException
     */
    private void balancePay(JSONObject js) throws JSONException {
        String user_token = (String) SpUtils.get(this, SpUtils.USERUSER_TOKEN, "");
        HttpParams params = new HttpParams();
        params.put("user_token", user_token);
        params.put("order_code", js.getString("order_code"));
        OkGo.<String>post(AppUrls.OrderPayUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .params(DoParams.encryptionparams(this, params, user_token))
                .tag(this)
                .execute(new DialogCallBack(SubScribeActivity.this, true) {
                    @Override
                    public void onSuccess(Response<String> response) {

                        try {
                            JSONObject result = new JSONObject(response.body());
                            if (result.getBoolean("result")) {
                                Intent it = new Intent(SubScribeActivity.this, OrderManagerActivity.class);
                                startActivity(it);
                                finish();
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
