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
import com.xinyi.studyabroad.utils.GlideCircleTransform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Niu on 2018/4/20.
 */

public class TutorAdapter extends RecyclerView.Adapter<TutorAdapter.ViewHolder> {

    private Context context;
    private  List<Map<String,String>> tutors;

    public TutorAdapter(Context context, List<Map<String,String>> tutors) {
        this.context = context;
        this.tutors=tutors;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tutor_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Map<String,String> map=new HashMap<>();
        Glide.with(context).load(map.get("image")).transform(new GlideCircleTransform(context)).into(holder.header);
        holder.tutor_name.setText(map.get("true_name"));
        holder.tutor_academy.setText(map.get("school_name"));
        holder.tutor_major.setText(map.get("professional_name"));
        holder.tutor_score.setText(map.get("points"));
    }

    @Override
    public int getItemCount() {
        return tutors.size();
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
