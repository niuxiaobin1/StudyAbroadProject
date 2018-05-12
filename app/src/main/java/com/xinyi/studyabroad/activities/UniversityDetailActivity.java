package com.xinyi.studyabroad.activities;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xinyi.studyabroad.R;
import com.xinyi.studyabroad.adapter.TutorAdapter;
import com.xinyi.studyabroad.base.BaseActivity;
import com.xinyi.studyabroad.callBack.DialogCallBack;
import com.xinyi.studyabroad.callBack.HandleResponse;
import com.xinyi.studyabroad.constants.AppUrls;
import com.xinyi.studyabroad.utils.DensityUtil;
import com.xinyi.studyabroad.utils.DividerDecoration;
import com.xinyi.studyabroad.utils.DoParams;
import com.xinyi.studyabroad.utils.GlideCircleTransform;
import com.xinyi.studyabroad.utils.StatusBarUtil;
import com.xinyi.studyabroad.utils.UIHelper;
import com.xinyi.studyabroad.weight.MyGridView;
import com.xinyi.studyabroad.weight.ObservableScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 院校详情
 */
public class UniversityDetailActivity extends BaseActivity {

    public static final String SCHOOL_ID = "_id";
    private String id="";

    @BindView(R.id.banner_image)
    ImageView banner_image;//banner

    @BindView(R.id.academy_name)
    TextView academy_name;//院校名称

    @BindView(R.id.academy_ranking)
    TextView academy_ranking;//院校排名

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    @BindView(R.id.tab_layout)
    RelativeLayout tab_layout;

    @BindView(R.id.appbar)
    AppBarLayout appbar;//外层appbar

    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @BindView(R.id.scrollView)
    ObservableScrollView scrollView;//外层scrollview

    //titleBar部分
    @BindView(R.id.title_layout)
    RelativeLayout title_layout;

    @BindView(R.id.back_layout)
    RelativeLayout back_layout;

    @BindView(R.id.back_image)
    ImageView back_image;

    @BindView(R.id.title_tv)
    TextView title_tv;

    @BindView(R.id.isShorten_tv)
    TextView isShorten_tv;//是否展开webview

    //学校详情Webview
    @BindView(R.id.introdection_webview)
    WebView introdection_webview;

    //导师recyler
    @BindView(R.id.tutor_recylerView)
    RecyclerView tutor_recylerView;

    //成绩Grid
    @BindView(R.id.gridView)
    MyGridView gridView;

    //申请信息Grid
    @BindView(R.id.informationGridView)
    MyGridView informationGridView;

    //申请列表
    @BindView(R.id.inforList_tv)
    TextView inforList_tv;

    //三部分layout
    @BindView(R.id.academy_introduction_ll)
    LinearLayout academy_introduction_ll;

    @BindView(R.id.tutorList_ll)
    LinearLayout tutorList_ll;

    @BindView(R.id.score_ll)
    LinearLayout score_ll;

    private String[] titles;//tablayout标题

    private boolean isShort = true;//webview是否展开
    private boolean isTouchScroll = true;//是否触摸滑动


    private static final int START_ALPHA = 0;
    private static final int END_ALPHA = 255;
    private Drawable drawable;        //顶部渐变布局需设置的Drawable

    int scroll = 0;//scrollview需要滚动的位置

    private int appMinHeight;//appbar最小高度
    private int scrollY;
    private int fadingHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_university_detail);
        StatusBarUtil.StatusBarLightMode(this);
        ButterKnife.bind(this);
        title_layout.setPadding(0, StatusBarUtil.getStatusBarHeight(this), 0, 0);
        initViews();
        initDatas();
    }

    @Override
    protected void onResume() {
        super.onResume();

        title_tv.setTextColor(Color.argb(scrollY / fadingHeight * 255, 0, 0, 0));
        drawable.setAlpha(scrollY * (END_ALPHA - START_ALPHA) / fadingHeight + START_ALPHA);
        if (scrollY == fadingHeight) {
            back_image.setImageResource(R.mipmap.arrow_back);
        } else {
            back_image.setImageResource(R.mipmap.back_white);
        }
    }

    @Override
    protected void initViews() {
        super.initViews();
        id=getIntent().getStringExtra(SCHOOL_ID);
        initTab();
        initWebView();
        initListener();

        drawable = getResources().getDrawable(R.drawable.color_title_transparent);
        drawable.setAlpha(START_ALPHA);
        title_layout.setBackgroundDrawable(drawable);


        appMinHeight = StatusBarUtil.getStatusBarHeight(UniversityDetailActivity.this) + DensityUtil.dip2px(UniversityDetailActivity.this, 80);
        fadingHeight = DensityUtil.dip2px(this, 240) - appMinHeight;
        collapsingToolbarLayout.setMinimumHeight(appMinHeight);

        tutor_recylerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        tutor_recylerView.addItemDecoration(new DividerDecoration(this, R.color.colorWhite,
                DensityUtil.dip2px(this, 5)));

    }

    private void initWebView() {
        //声明WebSettings子类
        WebSettings webSettings = introdection_webview.getSettings();

        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);


        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        //缩放操作
        webSettings.setSupportZoom(false); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(true); //隐藏原生的缩放控件

        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //优先使用缓存:
        // webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //缓存模式如下：
        //LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
        //LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
        //LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
        //LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。

        //不使用缓存:
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);


        introdection_webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });
    }


    private void initListener() {

        isShorten_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShort) {
                    //展开
                    resizeWebView(false);
                    isShorten_tv.setText(R.string.shortenString);
                } else {
                    resizeWebView(true);
                    isShorten_tv.setText(R.string.openString);
                }
                isShort = !isShort;
//                if (CommonUtils.getUserLocale(UniversityDetailActivity.this) == null) {
//                    CommonUtils.setLanguage(CommonUtils.LOCALE_ENGLISH, UniversityDetailActivity.this);
//                }
            }
        });

        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                verticalOffset = Math.abs(verticalOffset);
                if (verticalOffset > fadingHeight) {
                    verticalOffset = fadingHeight;   //当滑动到指定位置之后设置颜色为纯色，之前的话要渐变---实现下面的公式即可

                }
                if (verticalOffset == fadingHeight) {
                    back_image.setImageResource(R.mipmap.arrow_back);
                } else {
                    back_image.setImageResource(R.mipmap.back_white);
                }
                title_tv.setTextColor(Color.argb((int) ((float) verticalOffset / (float) fadingHeight * 255), 0, 0, 0));
                drawable.setAlpha(verticalOffset * (END_ALPHA - START_ALPHA) / fadingHeight + START_ALPHA);
                scrollY = verticalOffset;
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (isTouchScroll) {
                    return;
                }
                for (int i = 0; i < titles.length; i++) {
                    if (titles[i].equals(tab.getText())) {
                        doScroll(i);
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        scrollView.setOnScrollChangeListener(new ObservableScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChanged(ObservableScrollView scrollView, int l, int t, int oldl, int oldt) {
                if (isTouchScroll) {
                    setTabSelected(t);
                }
            }
        });

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isTouchScroll = true;
                return false;
            }
        });


        back_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initTab() {
        titles = getResources().getStringArray(R.array.academyDetail_tab);
        for (int i = 0; i < titles.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(titles[i]));
        }
        initTabClick();
    }

    @Override
    protected void initDatas() {
        HttpParams params = new HttpParams();
        params.put("id", id);
        super.initDatas();
        OkGo.<String>post(AppUrls.AcademyUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .params(DoParams.encryptionparams(this, params, ""))
                .tag(this)
                .execute(new DialogCallBack(UniversityDetailActivity.this, false) {
                    @Override
                    public void onSuccess(Response<String> response) {

                        try {
                            JSONObject result = new JSONObject(response.body());

                            if (result.getBoolean("result")) {
                                JSONObject object = result.getJSONObject("data");
                                //banner part
                                Glide.with(UniversityDetailActivity.this).load(
                                        object.getString("image")).into(banner_image);
                                academy_name.setText(object.getString("school_name"));
                                title_tv.setText(object.getString("school_name"));
                                academy_ranking.setText(object.getString("ranking"));
                                //init webView
                                String css = "<style type=\"text/css\"> </style>";
                                String html = "<html><header><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no>" + css + "</header>" + "<body>"
                                        + object.getString("description") + "</body>" + "</html>";
                                introdection_webview.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
                                //tutor
                                JSONArray teacher = object.getJSONArray("teacher");
                                List<Map<String, String>> tutors = new ArrayList<>();
                                for (int i = 0; i < teacher.length(); i++) {
                                    JSONObject jsonObject = teacher.getJSONObject(i);
                                    Map<String, String> map = new HashMap();
                                    map.put("true_name", jsonObject.getString("true_name"));
                                    map.put("grade", jsonObject.getString("grade"));
                                    map.put("image", jsonObject.getString("image"));
                                    map.put("school_name", jsonObject.getString("school_name"));
                                    map.put("professional_name", jsonObject.getString("professional_name"));
                                    map.put("user_token", jsonObject.getString("user_token"));
                                    tutors.add(map);
                                }
                                tutor_recylerView.setAdapter(new TutorAdapter(UniversityDetailActivity.this, tutors));
                                //score
                                JSONArray score = object.getJSONArray("score");
                                List<Map<String, String>> scoreList = new ArrayList<>();
                                for (int i = 0; i < score.length(); i++) {
                                    JSONObject jsonObject = score.getJSONObject(i);
                                    Map<String, String> map = new HashMap();
                                    map.put("name", jsonObject.getString("name"));
                                    map.put("image", jsonObject.getString("image"));
                                    map.put("score", jsonObject.getString("score"));
                                    scoreList.add(map);
                                }
                                gridView.setAdapter(new ScoreAdapter(scoreList));
                                //申请信息
                                JSONArray cost = object.getJSONArray("cost");
                                List<Map<String, String>> infors = new ArrayList<>();
                                for (int i = 0; i < cost.length(); i++) {
                                    JSONObject jsonObject = cost.getJSONObject(i);
                                    Map<String, String> map = new HashMap();
                                    map.put("name", jsonObject.getString("name"));
                                    map.put("cost", jsonObject.getString("cost"));
                                    infors.add(map);
                                }
                                informationGridView.setAdapter(new InformationAdapter(infors));

                                String apply_list = object.getString("apply_list");
                                inforList_tv.setText(apply_list);
                            } else {
                                UIHelper.toastMsg(result.getString("message"));
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
                        HandleResponse.handleException(response, UniversityDetailActivity.this);
                    }
                });

    }

    /**
     * 重新设置webview高度
     *
     * @param isShort
     */
    private void resizeWebView(boolean isShort) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) introdection_webview.getLayoutParams();
        if (isShort) {
            params.height = DensityUtil.dip2px(this, 200);
        } else {
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        }
        introdection_webview.setLayoutParams(params);
    }

    /**
     * 成绩要求adapter
     */
    private class ScoreAdapter extends BaseAdapter {

        private List<Map<String, String>> datas;

        public ScoreAdapter(List<Map<String, String>> datas) {
            this.datas = datas;
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = LayoutInflater.from(UniversityDetailActivity.this).inflate(R.layout.score_item, null);
            ImageView imageView = v.findViewById(R.id.imageView);
            View divider_line = v.findViewById(R.id.divider_line);
            if (position == getCount() - 1) {
                divider_line.setVisibility(View.INVISIBLE);
            } else {
                divider_line.setVisibility(View.VISIBLE);
            }
            Map<String, String> map = datas.get(position);
            Glide.with(UniversityDetailActivity.this).load(map.get("image")).transform(
                    new GlideCircleTransform(UniversityDetailActivity.this)).into(imageView);

            TextView score_name = v.findViewById(R.id.score_name);
            TextView score_points = v.findViewById(R.id.score_points);
            score_name.setText(map.get("name"));
            score_points.setText(map.get("score"));
            return v;
        }
    }

    /**
     * 申请信息adapter
     */
    private class InformationAdapter extends BaseAdapter {

        private List<Map<String, String>> datas;

        public InformationAdapter(List<Map<String, String>> datas) {
            this.datas = datas;
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = LayoutInflater.from(UniversityDetailActivity.this).inflate(R.layout.information_item, null);
            TextView infor_name = v.findViewById(R.id.infor_name);
            TextView infor_price = v.findViewById(R.id.infor_price);
            Map<String, String> map = datas.get(position);
            infor_name.setText(map.get("name"));
            infor_price.setText(map.get("cost"));
            return v;
        }
    }

    /**
     * 指定滚动
     *
     * @param index
     */
    private void doScroll(int index) {
        appbar.setExpanded(false, false);
        int Y1 = academy_introduction_ll.getTop();
        int Y2 = tutorList_ll.getTop();
        int Y3 = score_ll.getTop();
        if (index == 0) {
            scroll = Y1;
        } else if (index == 1) {
            scroll = Y2;
        } else {
            scroll = Y3;
        }
        int distenceY = scroll - scrollView.getScrollY();
        if (distenceY < 0) {
            scrollView.scrollBy(0, distenceY);
        } else {
            scrollView.smoothScrollBy(0, distenceY);
        }
    }

    /**
     * 根据滚动设置tab选中位置
     *
     * @param disY
     */
    private void setTabSelected(int disY) {
        int Y1 = academy_introduction_ll.getTop();
        int Y2 = tutorList_ll.getTop();
        int Y3 = score_ll.getTop();
        if (disY < Y2) {
            tabLayout.setScrollPosition(0, 0, true);
            tabLayout.getTabAt(0).select();
        } else if (disY < Y3) {
            tabLayout.setScrollPosition(1, 0, true);
            tabLayout.getTabAt(1).select();
        } else {
            tabLayout.setScrollPosition(2, 0, true);
            tabLayout.getTabAt(2).select();
        }
    }


    private void initTabClick() {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab == null) return;
            //这里使用到反射，拿到Tab对象后获取Class
            Class c = tab.getClass();
            try {
                //Filed “字段、属性”的意思,c.getDeclaredField 获取私有属性。
                //"mView"是Tab的私有属性名称(可查看TabLayout源码),类型是 TabView,TabLayout私有内部类。
                Field field = c.getDeclaredField("mView");
                //值为 true 则指示反射的对象在使用时应该取消 Java 语言访问检查。值为 false 则指示反射的对象应该实施 Java 语言访问检查。
                //如果不这样会报如下错误
                // java.lang.IllegalAccessException:
                //Class com.test.accessible.Main
                //can not access
                //a member of class com.test.accessible.AccessibleTest
                //with modifiers "private"
                field.setAccessible(true);
                final View view = (View) field.get(tab);
                if (view == null) return;
                view.setTag(i);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = (int) view.getTag();
                        //这里就可以根据业务需求处理点击事件了。
                        isTouchScroll = false;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
