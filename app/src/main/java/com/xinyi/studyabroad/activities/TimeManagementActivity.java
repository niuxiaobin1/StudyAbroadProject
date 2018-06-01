package com.xinyi.studyabroad.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectChangeListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xinyi.studyabroad.R;
import com.xinyi.studyabroad.adapter.TimesAdapter;
import com.xinyi.studyabroad.base.BaseActivity;
import com.xinyi.studyabroad.callBack.DialogCallBack;
import com.xinyi.studyabroad.callBack.HandleResponse;
import com.xinyi.studyabroad.constants.AppUrls;
import com.xinyi.studyabroad.utils.DensityUtil;
import com.xinyi.studyabroad.utils.DividerDecoration;
import com.xinyi.studyabroad.utils.DoParams;
import com.xinyi.studyabroad.utils.JsonUtils;
import com.xinyi.studyabroad.utils.SpUtils;
import com.xinyi.studyabroad.utils.StatusBarUtil;
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
import sun.bob.mcalendarview.MarkStyle;
import sun.bob.mcalendarview.listeners.OnExpDateClickListener;
import sun.bob.mcalendarview.listeners.OnMonthChangeListener;
import sun.bob.mcalendarview.utils.CurrentCalendar;
import sun.bob.mcalendarview.views.ExpCalendarView;
import sun.bob.mcalendarview.vo.DateData;
import sun.bob.mcalendarview.vo.MarkedDates;

public class TimeManagementActivity extends BaseActivity {

    @BindView(R.id.rootView)
    RelativeLayout rootView;

    @BindView(R.id.calendar)
    ExpCalendarView calendar;

    @BindView(R.id.main_YYMM_Tv)
    TextView main_YYMM_Tv;

    @BindView(R.id.time_list)
    RecyclerView time_list;

    @BindView(R.id.add_timesTv)
    TextView add_timesTv;

    private String currentDate;
    private TimesAdapter adapter;

    private List<String> hours;
    private List<List<String>> minutes;

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private List<Map<String, String>> servicesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_management);
        ButterKnife.bind(this);
        initViews();
        initDatas();
    }


    @Override
    protected void initViews() {
        super.initViews();
        StatusBarUtil.StatusBarDarkMode(this);
        StatusBarUtil.transparencyBar(this);
        initleftIcon(R.mipmap.back_white);
        initTitle(R.string.timeManagerString, R.color.colorWhite);
        initRightText(R.string.editString, R.color.colorWhite);

        minutes = new ArrayList<>();
        hours = new ArrayList<>();
        servicesList = new ArrayList<>();
        //默认当前选择日期为当天日期
        MarkedDates.getInstance().setCurrentSelectData(CurrentCalendar.getCurrentDateData());
        calendar.setOnDateClickListener(new OnExpDateClickListener() {
            @Override
            public void onClick(DateData date) {
                upDateTimes();
            }
        });

        calendar.setOnMonthChangeListener(new OnMonthChangeListener() {
            @Override
            public void onMonthChange(int year, int month) {
                main_YYMM_Tv.setText(year + getResources().getString(R.string.yearString) +
                        getMonthString(month) + getResources().getString(R.string.monthString));
            }
        });

        main_YYMM_Tv.setText(CurrentCalendar.getCurrentDateData().getYear() + getResources().getString(R.string.yearString) +
                CurrentCalendar.getCurrentDateData().getMonthString() + getResources().getString(R.string.monthString));

        time_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        time_list.addItemDecoration(new DividerDecoration(this, R.color.colorWhite,
                DensityUtil.dip2px(this, 10)));
        adapter = new TimesAdapter(this) {
            @Override
            public void onDelete(Map<String, String> map) {
                /**
                 * 删除操作
                 */
                adapter.getDatas().remove(map);
                adapter.notifyDataSetChanged();

                try {
                    update();
                } catch (ParseException e) {
                }
            }
        };
        time_list.setAdapter(adapter);

        add_timesTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加时间
                addTimes();
            }
        });
        upDateTimes();
    }

    public String getMonthString(int month) {
        return month > 9 ? String.format("%d", month) : String.format("0%d", month);
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        String user_token = (String) SpUtils.get(this, SpUtils.USERUSER_TOKEN, "");
        HttpParams params = new HttpParams();
        params.put("user_token", user_token);
        OkGo.<String>post(AppUrls.ServiceListsUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(TimeManagementActivity.this, params, user_token))
                .execute(new DialogCallBack(TimeManagementActivity.this, true) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject js = new JSONObject(response.body());

                            if (js.getBoolean("result")) {

                                servicesList.clear();
                                servicesList.addAll(JsonUtils.ArrayToList(js.getJSONArray("data"),
                                        new String[]{"service_date", "id", "order_flag", "service_times"}));

                                initDate();
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
                        HandleResponse.handleException(response, TimeManagementActivity.this);
                    }
                });
    }


    /**
     * 将日期标记在日历上
     */
    private void initDate() {
        MarkedDates.getInstance().removeAdd();

        for (int i = 0; i < servicesList.size(); i++) {
            String service_date = servicesList.get(i).get("service_date");
            String order_flag = servicesList.get(i).get("order_flag");
            //如果列表为空 不标记
            try {
                JSONArray array = new JSONArray(servicesList.get(i).get("service_times"));
                if (array.length() == 0) {
                    continue;
                }
            } catch (JSONException e) {
            }
            if (!TextUtils.isEmpty(service_date)) {
                String[] result = format.format(new Date(Long.parseLong(service_date) * 1000)).split("-");
                if (result.length == 3) {
                    DateData dateData = new DateData(Integer.parseInt(result[0]),
                            Integer.parseInt(result[1]), Integer.parseInt(result[2]));

                    if (!TextUtils.isEmpty(order_flag) && order_flag.equals("1")) {
                        //约满
                        dateData.setMarkStyle(MarkStyle.DEFAULT, Color.LTGRAY);
                    } else {
                        dateData.setMarkStyle(MarkStyle.DEFAULT, Color.GREEN);
                    }
                    calendar.markDate(dateData);
                }

            }
        }

    }

    /**
     * 更新当前时间的服务列表
     */
    private void upDateTimes() {
        adapter.clearDatas();
        DateData dateData = MarkedDates.getInstance().getCurrentSelectData();
        currentDate = dateData.getYear() + "-" + dateData.getMonthString() +
                "-" + dateData.getDay();
        try {
            long currentTimes = format.parse(currentDate).getTime();
            String service_times = "";
            //找到list里当天的数据
            for (int i = 0; i < servicesList.size(); i++) {
                if (servicesList.get(i).get("service_date").equals(String.valueOf(currentTimes / 1000))) {
                    service_times = servicesList.get(i).get("service_times");
                    break;
                }
            }
            //判断是否有数据
            if (!TextUtils.isEmpty(service_times)) {
                JSONArray array = new JSONArray(service_times);
                adapter.addDatas(JsonUtils.ArrayToList(array,
                        new String[]{"service_start_time", "service_end_time"}));
            }


        } catch (ParseException e) {

        } catch (JSONException e) {
        }

    }


    /**
     * 更新数据网络请求
     *
     * @throws ParseException
     */
    private void update() throws ParseException {

        String user_token = (String) SpUtils.get(this, SpUtils.USERUSER_TOKEN, "");
        HttpParams params = new HttpParams();
        params.put("user_token", user_token);
        params.put("service_date", (format.parse(currentDate).getTime() / 1000) + "");
        params.put("service_times", JsonUtils.map2Json(adapter.getDatas()));
        OkGo.<String>post(AppUrls.ServiceSaveUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(TimeManagementActivity.this, params, user_token))
                .execute(new DialogCallBack(TimeManagementActivity.this, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject js = new JSONObject(response.body());

                            if (js.getBoolean("result")) {
                                //成功后 更新本地list。不更新的话，切换日期后数据会还原，只有退出页面重新进来才刷新
                                try {
                                    long currentTimes = format.parse(currentDate).getTime();
                                    boolean isExit = false;
                                    for (int i = 0; i < servicesList.size(); i++) {
                                        if (servicesList.get(i).get("service_date").equals(String.valueOf(currentTimes / 1000))) {
                                            servicesList.get(i).put("service_times", adapter.getDatas().toString());
                                            isExit = true;
                                            break;
                                        }
                                    }

                                    if (!isExit){
                                        //新增加
                                        Map<String,String> map=new HashMap<>();
                                        map.put("service_date",String.valueOf(currentTimes/1000));
                                        map.put("order_flag","0");
                                        map.put("service_times",JsonUtils.map2Json(adapter.getDatas()));
                                        servicesList.add(map);
                                    }

                                    //重新校验标记是否正确

                                    String[] result = format.format(new Date(currentTimes)).split("-");
                                    if (result.length == 3) {
                                        DateData ND= new DateData(Integer.parseInt(result[0]),
                                                Integer.parseInt(result[1]), Integer.parseInt(result[2]));

                                        List<DateData> marked=MarkedDates.getInstance().getAll();
                                        if (marked.contains(ND)){
                                            //当前已经标记

                                            if (adapter.getDatas().size()==0){
                                                //移除
                                                marked.remove(ND);
                                            }

                                        }else{
                                            if (adapter.getDatas().size()!=0){
                                                //新增
                                                ND.setMarkStyle(MarkStyle.DEFAULT, Color.GREEN);
                                                calendar.markDate(ND);
                                            }
                                        }

                                    }

                                } catch (ParseException e) {

                                }

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
                        HandleResponse.handleException(response, TimeManagementActivity.this);
                    }
                });
    }


    private void addTimes() {
        initStartPopWindows(true);
    }

    private OptionsPickerView Popup;
    private String startTime;
    private String endTime;

    private void initStartPopWindows(final boolean isStart) {
        String title = isStart ? getResources().getString(R.string.timeStartString1) :
                getResources().getString(R.string.timeEndString);
        try {
            initItems(isStart);
        } catch (ParseException e) {
        }
        Popup = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置

                if (isStart) {
                    startTime = hours.get(options1) + "-" + minutes.get(options1).get(options2);
                    initStartPopWindows(false);

                } else {
                    endTime = hours.get(options1) + "-" + minutes.get(options1).get(options2);
                    try {
                        doAdd();
                    } catch (ParseException e) {
                    }
                }
            }
        })
                .setTitleText(title)
                .setContentTextSize(20)//设置滚轮文字大小
                .setDividerColor(Color.LTGRAY)//设置分割线的颜色
                .setSelectOptions(0, 0)//默认选中项
                .setBgColor(Color.WHITE)
                .setTitleBgColor(Color.WHITE)
                .setTitleColor(Color.BLACK)
                .setCancelColor(Color.GRAY)
                .setSubmitColor(Color.BLUE)
                .setTextColorCenter(Color.LTGRAY)
                .isRestoreItem(true)//切换时是否还原，设置默认选中第一项。
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setLabels("时", "分", "")
                .setBackgroundId(0x55000000) //设置外部遮罩颜色
                .isDialog(false)
                .setDecorView(rootView)
                .setOptionsSelectChangeListener(new OnOptionsSelectChangeListener() {
                    @Override
                    public void onOptionsSelectChanged(int options1, int options2, int options3) {
                    }
                })
                .build();

//        pvOptions.setSelectOptions(1,1);
        /*pvOptions.setPicker(options1Items);//一级选择器*/
        Popup.setPicker(hours, minutes);//二级选择器
        /*pvOptions.setPicker(options1Items, options2Items,options3Items);//三级选择器*/
        Popup.show();
    }


    /**
     * 计算选择时间数据
     *
     * @param isStart
     * @throws ParseException
     */
    private void initItems(boolean isStart) throws ParseException {
        minutes.clear();
        hours.clear();

        if (isStart) {
            for (int i = 0; i <= 23; i++) {
                hours.add(i > 9 ? String.format("%d", i) : String.format("0%d", i));
                List<String> list = new ArrayList<>();
                if (i == 23) {
                    for (int j = 0; j <= 29; j++) {
                        list.add(j > 9 ? String.format("%d", j) : String.format("0%d", j));
                    }
                } else {
                    for (int j = 0; j <= 59; j++) {
                        list.add(j > 9 ? String.format("%d", j) : String.format("0%d", j));
                    }
                }
                minutes.add(list);
            }
        } else {
            if (TextUtils.isEmpty(startTime)) {
                return;
            }
            String[] strings = startTime.split("-");
            //单位是分钟
            long minTimes = format.parse(currentDate).getTime() / 1000 / 60 +
                    Integer.parseInt(strings[0]) * 60 + Integer.parseInt(strings[1]) + 30;

            long maxTimes = format.parse(currentDate).getTime() / 1000 / 60 +
                    24 * 60;
            List<String> list = new ArrayList<>();
            for (long i = minTimes; i < maxTimes; i++) {
                String s = timeFormat.format(new Date(i * 60 * 1000));
                String next = timeFormat.format(new Date((i + 1) * 60 * 1000));
                list.add(s.split(":")[1]);
                if (s.split(":")[0].equals(next.split(":")[0])) {

                } else {
                    hours.add(s.split(":")[0]);
                    List inner = new ArrayList();
                    inner.addAll(list);
                    list.clear();
                    minutes.add(inner);
                }

            }

        }

    }


    private void doAdd() throws ParseException {
        Map<String, String> map = new HashMap<>();
        long start = format.parse(currentDate).getTime() / 1000 +
                Integer.parseInt(startTime.split("-")[0]) * 60 * 60
                + Integer.parseInt(startTime.split("-")[1]) * 60;
        long end = format.parse(currentDate).getTime() / 1000 +
                Integer.parseInt(endTime.split("-")[0]) * 60 * 60
                + Integer.parseInt(endTime.split("-")[1]) * 60;

        map.put("service_start_time", String.valueOf(start));
        map.put("service_end_time", String.valueOf(end));
        adapter.getDatas().add(map);
        adapter.notifyDataSetChanged();

        update();
    }
}
