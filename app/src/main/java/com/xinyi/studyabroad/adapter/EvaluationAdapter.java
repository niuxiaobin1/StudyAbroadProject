package com.xinyi.studyabroad.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.xinyi.studyabroad.R;
import com.xinyi.studyabroad.utils.GlideCircleTransform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.opengles.GL;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Niu on 2018/4/25.
 */

public class EvaluationAdapter extends BaseAdapter<EvaluationAdapter.ViewHolder> {

    private Context context;

    public EvaluationAdapter(Context context) {
        super();
        this.context = context;

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.evaluation_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Map<String, String> map = datas.get(position);
        Glide.with(context).load(map.get("image")).transform(new CenterCrop(context), new GlideCircleTransform(context))
                .into(holder.imageView);
        holder.eva_name.setText(map.get("true_name"));
        holder.eva_content.setText(map.get("content"));
        holder.eva_score.setText(map.get("stars"));
        holder.eva_time.setText(map.get("created"));
        holder.eva_personInfo.setText(map.get("service_name"));
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imageView)
        ImageView imageView;
        @BindView(R.id.eva_name)
        TextView eva_name;
        @BindView(R.id.eva_content)
        TextView eva_content;
        @BindView(R.id.eva_score)
        TextView eva_score;
        @BindView(R.id.eva_time)
        TextView eva_time;
        @BindView(R.id.eva_personInfo)
        TextView eva_personInfo;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
