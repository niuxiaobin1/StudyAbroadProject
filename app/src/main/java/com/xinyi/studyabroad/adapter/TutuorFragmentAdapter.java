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

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Niu on 2018/4/24.
 */

public class TutuorFragmentAdapter extends BaseAdapter<TutuorFragmentAdapter.ViewHolder> {

    private Context context;

    public TutuorFragmentAdapter(Context context) {
        super();
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tutor_fragment_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (datas.size() == 0) {
            return;
        }
        Map<String, String> map = datas.get(position);
        Glide.with(context).load(map.get("image")).into(holder.imageView);
        holder.tutor_name.setText(map.get("true_name"));
        holder.tutor_workInfo.setText(map.get("school_name"));
        holder.tutor_serviceInfo.setText(map.get("service_name"));
        holder.tutor_price.setText(map.get("money_sign") + map.get("service_price"));
        holder.tutor_unit.setText("/" + map.get("charge_unit"));
        holder.tutor_score.setText(map.get("points"));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(context, TutorDetailActivity.class);
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
        @BindView(R.id.tutor_name)
        TextView tutor_name;
        @BindView(R.id.tutor_workInfo)
        TextView tutor_workInfo;
        @BindView(R.id.tutor_serviceInfo)
        TextView tutor_serviceInfo;
        @BindView(R.id.tutor_price)
        TextView tutor_price;
        @BindView(R.id.tutor_unit)
        TextView tutor_unit;
        @BindView(R.id.tutor_score)
        TextView tutor_score;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
