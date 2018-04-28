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
import com.xinyi.studyabroad.activities.TutorDetailActivity;
import com.xinyi.studyabroad.utils.GlideCircleTransform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Niu on 2018/4/20.
 */

public class TutorAdapter extends BaseAdapter<TutorAdapter.ViewHolder> {

    private Context context;

    public TutorAdapter(Context context, List<Map<String,String>> tutors) {
        super();
        this.datas=tutors;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tutor_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
       final Map<String,String> map=datas.get(position);
        Glide.with(context).load(map.get("image")).transform(new GlideCircleTransform(context)).into(holder.header);
        holder.tutor_name.setText(map.get("true_name"));
        holder.tutor_academy.setText(map.get("school_name"));
        holder.tutor_major.setText(map.get("professional_name"));
        holder.tutor_score.setText(map.get("points"));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context, TutorDetailActivity.class);
                it.putExtra(TutorDetailActivity.TEACHER_USER_TOKEN,map.get("user_token"));
                context.startActivity(it);
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.header)
        ImageView header;
        @BindView(R.id.tutor_name)
        TextView tutor_name;
        @BindView(R.id.tutor_academy)
        TextView tutor_academy;
        @BindView(R.id.tutor_major)
        TextView tutor_major;
        @BindView(R.id.tutor_score)
        TextView tutor_score;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
