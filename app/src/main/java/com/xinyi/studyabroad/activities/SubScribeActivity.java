package com.xinyi.studyabroad.activities;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
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
import com.xinyi.studyabroad.utils.CommonUtils;
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
    private static final int TYPE_DATE = 0;
    private static final int TYPE_TIME = 1;
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
    //时间选择
    @BindView(R.id.date_grid)
    GridView date_grid;
    @BindView(R.id.time_grid)
    GridView time_grid;
    //确认预约
    @BindView(R.id.subscribe_commitTv)
    TextView subscribe_commitTv;
    //咨询内容
    @BindView(R.id.consulation_et)
    EditText consulation_et;

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
                Log.e("nxb",dateAdapter.getSelectString());
                Log.e("nxb",timeAdapter.getSelectString());
            }
        });
        //咨询
        consulation_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showInputMethod(consulation_et);
            }
        });
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
                                JSONObject data = result.getJSONObject("data");
                                //导师信息
                                JSONObject teacher_info = data.getJSONObject("teacher_info");
                                Glide.with(SubScribeActivity.this).load(teacher_info.getString("image")).into(imageView);
                                tutor_name.setText(teacher_info.getString("true_name"));
                                tutor_workInfo.setText(teacher_info.getString("school_name"));
                                tutor_serviceInfo.setText(teacher_info.getString("service_name"));
                                tutor_price.setText(teacher_info.getString("money_sign") + teacher_info.getString("service_price"));
                                tutor_unit.setText("/" + teacher_info.getString("charge_unit"));
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

    private PopupWindow mPopupWindow;

    private void showTimMinutesPopupWindow(){


    }


}
