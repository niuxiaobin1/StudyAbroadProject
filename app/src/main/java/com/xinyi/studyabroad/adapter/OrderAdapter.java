package com.xinyi.studyabroad.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xinyi.studyabroad.R;
import com.xinyi.studyabroad.activities.ComplaintActivity;
import com.xinyi.studyabroad.activities.EvaluateActivity;
import com.xinyi.studyabroad.activities.OrderManagerActivity;
import com.xinyi.studyabroad.activities.SubScribeActivity;
import com.xinyi.studyabroad.activities.VideoActivity;
import com.xinyi.studyabroad.callBack.DialogCallBack;
import com.xinyi.studyabroad.callBack.HandleResponse;
import com.xinyi.studyabroad.constants.AppUrls;
import com.xinyi.studyabroad.utils.DoParams;
import com.xinyi.studyabroad.utils.SpUtils;
import com.xinyi.studyabroad.utils.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Niu on 2018/4/28.
 */

public class OrderAdapter extends BaseAdapter<OrderAdapter.ViewHolder> {

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat dayFormat = new SimpleDateFormat("MM.dd");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    private Context context;
    private String identity_flag;

    AlertDialog.Builder builder;

    public OrderAdapter(Context context) {
        super();
        this.context = context;
        identity_flag = (String) SpUtils.get(context, SpUtils.USERIDENTITY_FLAG, "");
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Map<String, String> map = datas.get(position);
        holder.orderNum.setText(map.get("order_code"));
        final String order_status = map.get("order_status");
        holder.orderStatus.setText(map.get("status_name"));
        Glide.with(context).load(map.get("image")).into(holder.orderImage);
        holder.orderName.setText(map.get("true_name"));
        holder.orderTime.setText(dayFormat.format(new Date(Long.parseLong(map.get("service_date")) * 1000))
                + " " + timeFormat.format(new Date(Long.parseLong(map.get("service_start_time")) * 1000)) + "-" +
                timeFormat.format(new Date(Long.parseLong(map.get("service_end_time")) * 1000)));
        holder.tutor_price.setText(map.get("money_sign") + map.get("service_price"));
        holder.tutor_unit.setText("/" + map.get("charge_unit"));
        holder.total_time.setText(String.format("%.2f", Double.parseDouble(map.get("length_time")) / 60) + map.get("charge_unit"));
        holder.total_price.setText(map.get("money_sign") + map.get("price"));

//        Order_status = 1
//        取消预约    去支付
//        Order_status = 2
//        学生： 取消预约     导师 ：取消预约   接单
//        Order_status = 3
//        取消预约     发起视频
//
//        Order_status = 4
//        结束服务
//        Order_status = 5,
//         学生：投诉  完成订单    导师 ：
//
//        Order_status=6
//        学生：去评价  删除订单  导师 ：
//                Order_status=7
//        删除订单
//                Order_status = 9
//        删除订单
//                Order_status = 88
//        删除订单
        if (order_status.equals("1")) {
            holder.left_tv.setText(R.string.cancleOrderString);
            holder.right_tv.setText(R.string.goPayString);
            changeVisbility(0, holder);
        } else if (order_status.equals("2")) {
            if (identity_flag.equals("1")) {
                holder.left_tv.setText(R.string.cancleOrderString);
                holder.right_tv.setText("");
                changeVisbility(1, holder);
            } else {
                holder.left_tv.setText(R.string.cancleOrderString);
                holder.right_tv.setText(R.string.receptString);
                changeVisbility(0, holder);
            }
        } else if (order_status.equals("3")) {
            holder.left_tv.setText(R.string.cancleOrderString);
            holder.right_tv.setText(R.string.LaunchVideoString);
            changeVisbility(0, holder);
        } else if (order_status.equals("4")) {
            holder.left_tv.setText(R.string.finishServiceString);
            holder.right_tv.setText(R.string.LaunchVideoString);
            changeVisbility(0, holder);
        } else if (order_status.equals("5")) {
            if (identity_flag.equals("1")) {
                holder.left_tv.setText(R.string.complaintString);
                holder.right_tv.setText(R.string.finishOrderString);
                changeVisbility(0, holder);
            } else {
                holder.left_tv.setText("");
                holder.right_tv.setText("");
                changeVisbility(3, holder);
            }

        } else if (order_status.equals("6")) {
            if (identity_flag.equals("1")) {
                holder.left_tv.setText(R.string.toEvaluateString);
                holder.right_tv.setText(R.string.deleteString);
                changeVisbility(0, holder);
            } else {
                holder.left_tv.setText("");
                holder.right_tv.setText("");
                changeVisbility(3, holder);
            }
        } else {
            holder.left_tv.setText(R.string.deleteString);
            holder.right_tv.setText("");
            changeVisbility(1, holder);
        }

        holder.right_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.alertString);
                builder.setMessage(R.string.sureMessageString);
                builder.setNegativeButton(R.string.cancleString, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setPositiveButton(R.string.sureString, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (order_status.equals("3")||order_status.equals("4")) {
                            //视频聊天

                            long currentTime = System.currentTimeMillis() / 1000;
                            if (currentTime >= Long.parseLong(map.get("service_start_time"))) {
                                Intent it = new Intent(context, VideoActivity.class);
                                it.putExtra(VideoActivity.CHANNEL, map.get("order_code"));
                                context.startActivity(it);
                            } else {
                                UIHelper.toastMsg(context.getResources().getString(R.string.timeNostartString));
                            }


                        } else if (order_status.equals("1")) {
                            //去支付

                        } else if (order_status.equals("2")) {
                            if (!identity_flag.equals("1")) {
                                //接单
                            }
                        } else if (order_status.equals("5")) {
                            //完成订单
                            doOrderOperation(position, map.get("order_code"), "complete",
                                    "");
                        } else if (order_status.equals("6")) {
                            //删除订单
                            doOrderOperation(position, map.get("order_code"), "delete",
                                    "");
                        }
                    }
                });
                builder.create().show();

            }
        });

        holder.left_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.alertString);
                builder.setMessage(R.string.sureMessageString);
                builder.setNegativeButton(R.string.cancleString, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setPositiveButton(R.string.sureString, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (order_status.equals("1") || order_status.equals("2") || order_status.equals("3")) {
                            //取消预约
                            try {
                                doOrderOperation(position, map.get("order_code"), "cancel"
                                        , format.parse(format.format(new Date(System.currentTimeMillis()))).getTime() / 1000 + "");
                            } catch (ParseException e) {

                            }
                        } else if (order_status.equals("4")) {
                            //结束服务
                            doOrderOperation(position, map.get("order_code"), "end", "");
                        } else if (order_status.equals("5")) {
                            //投诉
                            Intent it=new Intent(context, ComplaintActivity.class);
                            it.putExtra(ComplaintActivity.ORDER_CODE,map.get("order_code"));
                            context.startActivity(it);
                        } else if (order_status.equals("6")) {
                            //去评价
                            Intent it=new Intent(context, EvaluateActivity.class);
                            it.putExtra(EvaluateActivity.ORDER_CODE,map.get("order_code"));
                            context.startActivity(it);
                        } else {
                            //删除订单
                            doOrderOperation(position, map.get("order_code"), "delete",
                                    "");

                        }
                    }
                });
                builder.create().show();
            }
        });

    }

    /**
     * 0：都显示
     * 1：显示左边
     * 2：显示右边
     * 3：都不显示
     *
     * @param type
     */
    private void changeVisbility(int type, ViewHolder holder) {
        switch (type) {
            case 0:
                holder.left_tv.setVisibility(View.VISIBLE);
                holder.right_tv.setVisibility(View.VISIBLE);
                break;
            case 1:
                holder.left_tv.setVisibility(View.VISIBLE);
                holder.right_tv.setVisibility(View.GONE);
                break;
            case 2:
                holder.left_tv.setVisibility(View.GONE);
                holder.right_tv.setVisibility(View.VISIBLE);
                break;
            case 3:
                holder.left_tv.setVisibility(View.GONE);
                holder.right_tv.setVisibility(View.GONE);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.orderNum)
        TextView orderNum;

        @BindView(R.id.orderStatus)
        TextView orderStatus;

        @BindView(R.id.orderImage)
        ImageView orderImage;

        @BindView(R.id.orderName)
        TextView orderName;

        @BindView(R.id.orderTime)
        TextView orderTime;

        @BindView(R.id.tutor_price)
        TextView tutor_price;

        @BindView(R.id.tutor_unit)
        TextView tutor_unit;

        @BindView(R.id.total_time)
        TextView total_time;

        @BindView(R.id.total_price)
        TextView total_price;

        @BindView(R.id.left_tv)
        TextView left_tv;

        @BindView(R.id.right_tv)
        TextView right_tv;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private void doOrderOperation(final int position, String order_code, final String button_status, String now_time) {
        String user_token = (String) SpUtils.get(context, SpUtils.USERUSER_TOKEN, "");
        HttpParams params = new HttpParams();
        params.put("user_token", user_token);
        params.put("order_code", order_code);
        params.put("button_status", button_status);
        params.put("now_time", now_time);
        OkGo.<String>post(AppUrls.OrderUpdateUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .params(DoParams.encryptionparams(context, params, user_token))
                .tag(this)
                .execute(new DialogCallBack((Activity) context, true) {
                    @Override
                    public void onSuccess(Response<String> response) {

                        try {
                            JSONObject result = new JSONObject(response.body());
                            if (result.getBoolean("result")) {
                                JSONObject data = result.getJSONObject("data");
                                if (button_status.equals("delete")) {
                                    datas.remove(position);
                                } else {
                                    datas.get(position).put("order_status", data.getString("order_status"));
                                }
                                notifyDataSetChanged();
                            } else {
                            }
                            UIHelper.toastMsg(result.getString("message"));
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
                        HandleResponse.handleException(response, (Activity) context);
                    }
                });
    }

}
