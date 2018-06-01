package com.xinyi.studyabroad.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xinyi.studyabroad.R;
import com.xinyi.studyabroad.activities.FindDetailActivity;
import com.xinyi.studyabroad.weight.EllipsizingTextView;
import com.xinyi.studyabroad.weight.SlideDelete;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Niu on 2018/4/25.
 */

public abstract class TimesAdapter extends BaseAdapter<TimesAdapter.ViewHolder> {


    private Context context;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public TimesAdapter(Context context) {
        super();
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Map<String, String> map = datas.get(position);
        String date = format.format(new Date(Long.parseLong(map.get("service_start_time")) * 1000));
        String startTime = timeFormat.format(new Date(Long.parseLong(map.get("service_start_time")) * 1000));
        String endTime = timeFormat.format(new Date(Long.parseLong(map.get("service_end_time")) * 1000));
        holder.time_tv.setText(date + " " + startTime + "-" + endTime);
        holder.delete_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDelete(map);
            }
        });
        holder.container.requestLayout();
    }

    public abstract void onDelete(Map<String, String> map);

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.time_tv)
        TextView time_tv;
        @BindView(R.id.container)
        SlideDelete container;
        @BindView(R.id.delete_tv)
        TextView delete_tv;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
