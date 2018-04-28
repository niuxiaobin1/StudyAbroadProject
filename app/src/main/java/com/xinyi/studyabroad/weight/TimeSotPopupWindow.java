package com.xinyi.studyabroad.weight;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.contrarywind.adapter.WheelAdapter;
import com.contrarywind.listener.OnItemSelectedListener;
import com.contrarywind.view.WheelView;
import com.xinyi.studyabroad.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Niu on 2018/4/28.
 */

public class TimeSotPopupWindow extends PopupWindow {

    private List<String> timeSlot;
    private List<String> startTime;

    private String service_start_time = "";
    private String service_end_time = "";
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private final static int TYPE_SLOT = 1;
    private final static int TYPE_START = 2;


    @BindView(R.id.cancle_tv)
    TextView cancle_tv;

    @BindView(R.id.sure_tv)
    TextView sure_tv;

    @BindView(R.id.menuLayout)
    LinearLayout menuLayout;

    @BindView(R.id.minutes_wheelView)
    WheelView minutes_wheelView;

    @BindView(R.id.timePoint_wheelView)
    WheelView timePoint_wheelView;

    View rootView;
    private Context context;

    private ArrayWheelAdapter slotAdapter;
    private ArrayWheelAdapter startAdapter;

    private String slotString = "";
    private String startString = "";

    public String[] getResult(){
        return new String[]{slotString,startString};
    }

    public TimeSotPopupWindow(Context context, View.OnClickListener itemsOnClick) {
        super(context);
        this.context = context;
        timeSlot = new ArrayList<>();
        startTime = new ArrayList<>();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.timeslot_selelct_layout, null);
        ButterKnife.bind(this, rootView);

        // 取消按钮
        cancle_tv.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // 销毁弹出框
                dismiss();
            }
        });
        minutes_wheelView.setCyclic(false);
        timePoint_wheelView.setCyclic(false);

//        minutes_wheelView.setAdapter(slotAdapter);
//        timePoint_wheelView.setAdapter(startAdapter);

        // 设置按钮监听
        sure_tv.setOnClickListener(itemsOnClick);
        // 设置SelectPicPopupWindow的View
        this.setContentView(rootView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.popupAnimation);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xaa000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        rootView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = menuLayout.getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return false;
            }
        });

        minutes_wheelView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                slotString = timeSlot.get(index);
                resetStartTime();
                timePoint_wheelView.setCurrentItem(0);
            }
        });
        timePoint_wheelView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                startString=startTime.get(index);
            }
        });
    }

    private void resetStartTime() {
        long minStartTime = Long.parseLong(service_start_time);
        long maxStartTime = Long.parseLong(service_end_time) - Integer.parseInt(slotString) * 60;
        startTime.clear();
        for (long i = minStartTime; i <= maxStartTime; i += 60*10) {
            startTime.add(String.valueOf(i));
        }
        slotAdapter = new ArrayWheelAdapter(TYPE_SLOT);
        startAdapter = new ArrayWheelAdapter(TYPE_START);
        minutes_wheelView.setAdapter(slotAdapter);
        timePoint_wheelView.setAdapter(startAdapter);
        if (startTime.size()>0){
            startString=startTime.get(0);
        }
    }

    public void setDatas(List<String> timeSlot, String service_start_time, String service_end_time) {
        this.timeSlot = timeSlot;
        this.service_end_time = service_end_time;
        this.service_start_time = service_start_time;
        if (timeSlot.size() > 0) {
            slotString = timeSlot.get(0);
        } else {
            return;
        }
        resetStartTime();

    }


    private class ArrayWheelAdapter implements WheelAdapter<String> {

        private int type;

        public ArrayWheelAdapter(int type) {
            this.type = type;
        }

        @Override
        public int getItemsCount() {
            if (type == TYPE_SLOT) {
                return timeSlot.size();
            } else {
                return startTime.size();
            }

        }

        @Override
        public String getItem(int index) {
            if (type == TYPE_SLOT) {
                return timeSlot.get(index) +
                        context.getResources().getString(R.string.minuteString);
            } else {
                return timeFormat.format(new Date(Long.parseLong(startTime.get(index)) * 1000));
            }
        }

        @Override
        public int indexOf(String o) {
            return -1;
        }
    }
}
