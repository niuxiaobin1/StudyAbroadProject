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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Niu on 2018/4/25.
 */

public class FindAdapter extends BaseAdapter<FindAdapter.ViewHolder> {


    private Context context;

    public FindAdapter(Context context) {
        super();
        this.context = context;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Map<String, String> map = datas.get(position);
        Glide.with(context).load(map.get("image")).into(holder.imageView);
        holder.newsName.setText(map.get("name"));
        holder.newsDetail.setText(map.get("description"));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context, FindDetailActivity.class);
//                it.putExtra(FindDetailActivity.NEWS_ID, map.get("id"));
                context.startActivity(it);
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imageView)
        ImageView imageView;
        @BindView(R.id.newsName)
        TextView newsName;
        @BindView(R.id.newsDetail)
        EllipsizingTextView newsDetail;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
