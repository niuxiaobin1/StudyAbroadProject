package com.xinyi.studyabroad.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xinyi.studyabroad.R;
import com.xinyi.studyabroad.adapter.BaseAdapter;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Niu on 2018/4/28.
 */

public class PayMethdoAdapter extends BaseAdapter<PayMethdoAdapter.ViewHolder> {

    public Map<String, String> getPayMethod(){
        return datas.get(currentPosition);
    }

    private int currentPosition = 0;
    private Context context;

    public PayMethdoAdapter(Context context) {
        super();
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.patmetod_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        if (position == currentPosition) {
            holder.falg_tv.setSelected(true);
        } else {
            holder.falg_tv.setSelected(false);
        }
        Glide.with(context).load(datas.get(position).get("image")).into(holder.pay_image);
        holder.pay_text.setText(datas.get(position).get("name"));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPosition = position;
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.pay_text)
        TextView pay_text;

        @BindView(R.id.falg_tv)
        TextView falg_tv;

        @BindView(R.id.pay_image)
        ImageView pay_image;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
