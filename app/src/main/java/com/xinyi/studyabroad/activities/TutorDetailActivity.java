package com.xinyi.studyabroad.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.xinyi.studyabroad.R;
import com.xinyi.studyabroad.adapter.EvaluationAdapter;
import com.xinyi.studyabroad.base.BaseActivity;
import com.xinyi.studyabroad.utils.DensityUtil;
import com.xinyi.studyabroad.utils.DividerDecoration;
import com.xinyi.studyabroad.weight.EllipsizingTextView;
import com.xinyi.studyabroad.weight.GlideImageLoader;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TutorDetailActivity extends BaseActivity {

    @BindView(R.id.banner)
    Banner banner;

    //个人简介
    @BindView(R.id.personalProfileTv)
    EllipsizingTextView personalProfileTv;

    @BindView(R.id.isShorten_tv)
    TextView isShorten_tv;
    //个人评价
    @BindView(R.id.evaluationRecylerView)
    RecyclerView evaluationRecylerView;

    @BindView(R.id.evaluationIsShorten_tv)
    TextView evaluationIsShorten_tv;

    private int maxLine = 4;
    private List<Integer> images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_detail);
        ButterKnife.bind(this);
        initViews();
        initDatas();
    }


    @Override
    protected void initViews() {
        super.initViews();

        initTitle(R.string.tutorDetailString);
        hideRightTv();
        initBanner();

        evaluationRecylerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        evaluationRecylerView.addItemDecoration(new DividerDecoration(this, R.color.colorLine,
                DensityUtil.dip2px(this, 1)));
        evaluationRecylerView.setAdapter(new EvaluationAdapter(this));

        personalProfileTv.setMaxLines(4);
        personalProfileTv.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        isShorten_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (maxLine == 4) {
                    personalProfileTv.setMaxLines(Integer.MAX_VALUE);
                    maxLine = Integer.MAX_VALUE;
                    isShorten_tv.setText(R.string.shortenString);
                } else {
                    personalProfileTv.setMaxLines(4);
                    personalProfileTv.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
                    maxLine = 4;
                    isShorten_tv.setText(R.string.openString);

                }
            }
        });
    }

    private void initBanner() {

        images = new ArrayList<>();
        images.add(R.mipmap.academy_action_bar_bg);
        images.add(R.mipmap.academy_action_bar_bg);
        images.add(R.mipmap.academy_action_bar_bg);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置banner样式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        banner.setIndicatorGravity(BannerConfig.CENTER);
        //设置图片集合
        banner.setImages(images);
        //设置banner动画效果
        banner.setBannerAnimation(Transformer.BackgroundToForeground);
        //设置自动轮播，默认为true
        banner.isAutoPlay(true);
        //设置轮播时间
        banner.setDelayTime(3000);
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {

            }
        });
        //banner设置方法全部调用完毕时最后调用
        banner.start();
    }


    @Override
    protected void initDatas() {
        super.initDatas();
    }
}
