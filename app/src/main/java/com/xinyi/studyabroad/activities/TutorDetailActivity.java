package com.xinyi.studyabroad.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xinyi.studyabroad.R;
import com.xinyi.studyabroad.adapter.EvaluationAdapter;
import com.xinyi.studyabroad.base.BaseActivity;
import com.xinyi.studyabroad.callBack.DialogCallBack;
import com.xinyi.studyabroad.callBack.HandleResponse;
import com.xinyi.studyabroad.constants.AppUrls;
import com.xinyi.studyabroad.utils.DensityUtil;
import com.xinyi.studyabroad.utils.DividerDecoration;
import com.xinyi.studyabroad.utils.DoParams;
import com.xinyi.studyabroad.utils.JsonUtils;
import com.xinyi.studyabroad.utils.SpUtils;
import com.xinyi.studyabroad.utils.UIHelper;
import com.xinyi.studyabroad.weight.EllipsizingTextView;
import com.xinyi.studyabroad.weight.GlideImageLoader;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TutorDetailActivity extends BaseActivity {

    private String favorite_flag;//0:未关注 否则是fid

    @BindView(R.id.banner)
    Banner banner;
    //导师信息
    @BindView(R.id.tutor_name)
    TextView tutor_name;
    @BindView(R.id.tutor_workInfo)
    TextView tutor_workInfo;
    @BindView(R.id.tutor_standard)
    TextView tutor_standard;
    @BindView(R.id.tutor_unit)
    TextView tutor_unit;
    @BindView(R.id.tutor_major)
    TextView tutor_major;
    @BindView(R.id.tutor_grade)
    TextView tutor_grade;
    @BindView(R.id.tutor_category)
    TextView tutor_category;
    @BindView(R.id.servire_exp)
    TextView servire_exp;
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
    //关注
    @BindView(R.id.fllow_tv)
    TextView fllow_tv;
    //预约
    @BindView(R.id.subscribeTv)
    TextView subscribeTv;

    private int maxLine = 4;
    private List<Integer> images;

    private EvaluationAdapter adapter;

    public static final String TEACHER_USER_TOKEN="_teacher_user_token";
    private String teacher_user_token = "";

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
        teacher_user_token=getIntent().getStringExtra(TEACHER_USER_TOKEN);
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
        adapter=new EvaluationAdapter(this);
        evaluationRecylerView.setAdapter(adapter);

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

        fllow_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doFavo();
            }
        });
        subscribeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_token = (String) SpUtils.get(TutorDetailActivity.this, SpUtils.USERUSER_TOKEN, "");
                if (TextUtils.isEmpty(user_token)) {

                    return;
                }
                Intent it = new Intent(TutorDetailActivity.this, SubScribeActivity.class);
                it.putExtra(SubScribeActivity.USER_TOKEN, teacher_user_token);
                startActivity(it);
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
        String user_token = (String) SpUtils.get(this, SpUtils.USERUSER_TOKEN, "");
        HttpParams params = new HttpParams();
        params.put("teacher_user_token", teacher_user_token);
        params.put("user_token", user_token);
        OkGo.<String>post(AppUrls.TeacherInfoUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(TutorDetailActivity.this, params, user_token))
                .execute(new DialogCallBack(TutorDetailActivity.this, true) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject js = new JSONObject(response.body());

                            if (js.getBoolean("result")) {
                                JSONObject teacher_info = js.getJSONObject("data").getJSONObject("teacher_info");
                                tutor_name.setText(teacher_info.getString("true_name"));
                                tutor_workInfo.setText(teacher_info.getString("school_name"));
                                tutor_standard.setText(teacher_info.getString("money_sign") +teacher_info.getString("service_price"));
                                tutor_unit.setText("/" +teacher_info.getString("charge_unit"));
                                tutor_major.setText(teacher_info.getString("professional_name"));
                                tutor_grade.setText(teacher_info.getString("grade"));
                                tutor_category.setText(teacher_info.getString("lb"));
                                personalProfileTv.setText(teacher_info.getString("service_name"));
                                servire_exp.setText(teacher_info.getString("created"));
                                favorite_flag = teacher_info.getString("favorite_flag");
                                //
                                JSONArray review_list = js.getJSONObject("data").getJSONArray("review_list");

                                adapter.addDatas(JsonUtils.ArrayToList(review_list,
                                        new String[]{"true_name", "image", "content", "stars", "created","service_name"}));

                                if (!TextUtils.isEmpty(favorite_flag) && !favorite_flag.equals("0")) {
                                    fllow_tv.setSelected(true);
                                } else {
                                    fllow_tv.setSelected(false);
                                }
                            } else {

                                UIHelper.toastMsg(js.getString("message"));
                            }
                        } catch (JSONException e) {
                            UIHelper.toastMsg(e.getMessage());
                        }
                    }


                    @Override
                    public String convertResponse(okhttp3.Response response) throws Throwable {
                        HandleResponse.handleReponse(response);
                        return super.convertResponse(response);
                    }

                    @Override
                    public void onError(com.lzy.okgo.model.Response<String> response) {
                        super.onError(response);
                        HandleResponse.handleException(response, TutorDetailActivity.this);
                    }
                });
    }


    private void doFavo() {
        String user_token = (String) SpUtils.get(this, SpUtils.USERUSER_TOKEN, "");
        if (TextUtils.isEmpty(user_token)) {
            showToast(R.string.firstLoginString);
            return;
        }
        HttpParams params = new HttpParams();
        params.put("user_token", user_token);
        String fllowUrl = AppUrls.CusomerAddFavoriteUrl;
        if (!TextUtils.isEmpty(favorite_flag) && !favorite_flag.equals("0")) {
            //已关注
            fllowUrl = AppUrls.CusomerRemoveFavoriteUrl;
            params.put("fid", favorite_flag);
        } else {
            fllowUrl = AppUrls.CusomerAddFavoriteUrl;
            params.put("teacher_user_token", teacher_user_token);
        }

        OkGo.<String>post(fllowUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(TutorDetailActivity.this, params, user_token))
                .execute(new DialogCallBack(TutorDetailActivity.this, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject js = new JSONObject(response.body());

                            if (js.getBoolean("result")) {
                                if (!TextUtils.isEmpty(favorite_flag) && !favorite_flag.equals("0")) {
                                    //已关注
                                    favorite_flag = "0";
                                    fllow_tv.setSelected(false);
                                } else {
                                    favorite_flag = js.getString("data");
                                    fllow_tv.setSelected(true);
                                }

                            } else {

                                UIHelper.toastMsg(js.getString("message"));
                            }
                        } catch (JSONException e) {
                            UIHelper.toastMsg(e.getMessage());
                        }
                    }


                    @Override
                    public String convertResponse(okhttp3.Response response) throws Throwable {
                        HandleResponse.handleReponse(response);
                        return super.convertResponse(response);
                    }

                    @Override
                    public void onError(com.lzy.okgo.model.Response<String> response) {
                        super.onError(response);
                        HandleResponse.handleException(response, TutorDetailActivity.this);
                    }
                });
    }
}
