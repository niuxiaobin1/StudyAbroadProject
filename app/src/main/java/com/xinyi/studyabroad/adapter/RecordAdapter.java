package com.xinyi.studyabroad.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xinyi.studyabroad.R;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Niu on 2018/4/24.
 */

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {

    private OnDeleteListener onDeleteListener;
    private List<Map<String, String>> data;

    public RecordAdapter() {

    }


    public void setData(List<Map<String, String>> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_record_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.contentTv.setText(data.get(position).get("title"));
        holder.deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDeleteListener!=null){
                    onDeleteListener.onDelete(data.get(position).get("title"));
                }
            }
        });

    }


    public interface OnDeleteListener {
        void onDelete(String title);
    }

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.contentTv)
        TextView contentTv;

        @BindView(R.id.deleteImage)
        ImageView deleteImage;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
