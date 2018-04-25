package com.xinyi.studyabroad.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xinyi.studyabroad.R;
import com.xinyi.studyabroad.activities.TutorDetailActivity;

/**
 * Created by Niu on 2018/4/24.
 */

public class TutuorFragmentAadapter extends RecyclerView.Adapter<TutuorFragmentAadapter.ViewHolder> {

    private Context context;

    public TutuorFragmentAadapter(Context context){
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tutor_fragment_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(context, TutorDetailActivity.class);
                context.startActivity(it);
            }
        });

    }

    @Override
    public int getItemCount() {
        return 13;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
