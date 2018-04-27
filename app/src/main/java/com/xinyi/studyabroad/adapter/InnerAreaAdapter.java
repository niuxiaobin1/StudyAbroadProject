package com.xinyi.studyabroad.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.xinyi.studyabroad.R;
import com.xinyi.studyabroad.utils.DensityUtil;
import com.xinyi.studyabroad.utils.GlideRoundTransform;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Niu on 2018/4/24.
 */

public class InnerAreaAdapter extends BaseAdapter<InnerAreaAdapter.ViewHolder> {

    private Context context;

    public InnerAreaAdapter(Context context){
        super();
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.area_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(context).load(R.mipmap.academy_action_bar_bg).transform(new CenterCrop(context),new GlideRoundTransform(context,
                DensityUtil.dip2px(context,3))).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.imageView)
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
