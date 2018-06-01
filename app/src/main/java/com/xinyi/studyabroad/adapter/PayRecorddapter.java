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

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Niu on 2018/4/25.
 */

public class PayRecorddapter extends BaseAdapter<PayRecorddapter.ViewHolder> {


    private Context context;

    public PayRecorddapter(Context context) {
        super();
        this.context = context;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.payrecord_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Map<String, String> map = datas.get(position);
        holder.payCodeTv.setText(map.get("order_code"));
        holder.payDatev.setText(map.get("created"));
        holder.payNumTv.setText(map.get("total"));


    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.payCodeTv)
        TextView payCodeTv;
        @BindView(R.id.payDatev)
        TextView payDatev;
        @BindView(R.id.payNumTv)
        TextView payNumTv;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
