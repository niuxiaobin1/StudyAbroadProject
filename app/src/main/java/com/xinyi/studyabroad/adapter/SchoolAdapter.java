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
import com.xinyi.studyabroad.activities.UniversityDetailActivity;
import com.xinyi.studyabroad.weight.EllipsizingTextView;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Niu on 2018/5/12.
 */

public class SchoolAdapter extends BaseAdapter<SchoolAdapter.ViewHolder> {

    private Context context;

    public SchoolAdapter(Context context) {
        super();
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.school_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Map<String, String> map = datas.get(position);
        Glide.with(context).load(map.get("image")).into(holder.shool_image);
        holder.schoolName.setText(map.get("school_name"));
        holder.schoolHotPro.setText(map.get("hot_professional"));
        holder.schoolAreaBelong.setText(map.get("sta_pro_name"));
        holder.schoolRanking.setText(map.get("ranking"));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context, UniversityDetailActivity.class);
                it.putExtra(UniversityDetailActivity.SCHOOL_ID, map.get("id"));
                context.startActivity(it);
            }
        });

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.shool_image)
        ImageView shool_image;

        @BindView(R.id.schoolName)
        TextView schoolName;

        @BindView(R.id.schoolHotPro)
        EllipsizingTextView schoolHotPro;

        @BindView(R.id.schoolAreaBelong)
        EllipsizingTextView schoolAreaBelong;

        @BindView(R.id.schoolRanking)
        EllipsizingTextView schoolRanking;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
