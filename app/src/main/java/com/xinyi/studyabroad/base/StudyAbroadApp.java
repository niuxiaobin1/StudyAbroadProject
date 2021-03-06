package com.xinyi.studyabroad.base;

import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.multidex.MultiDexApplication;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.MemoryCookieStore;
import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.xinyi.studyabroad.constants.Configer;
import com.xinyi.studyabroad.third.WeakHandler;
import com.xinyi.studyabroad.utils.CommonUtils;
import com.xinyi.studyabroad.utils.SpUtils;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2018/4/16.
 */

public class StudyAbroadApp extends MultiDexApplication {


    private String country_id = "0";

    public Locale getAppLocal() {
        return appLocal;
    }

    public String getCountry_id() {
        String spCountryId = (String) SpUtils.get(getApplicationContext(), SpUtils.COUNTRY_ID, "");
        if (TextUtils.isEmpty(spCountryId)) {
            return country_id;
        } else {
            return spCountryId;
        }
    }

    public void setCountry_id(String country_id) {
        SpUtils.put(getApplicationContext(), SpUtils.COUNTRY_ID, country_id);
        this.country_id = country_id;
    }


    public void setAppLocal(Locale appLocal) {
        this.appLocal = appLocal;
    }

    private Locale appLocal;//当前程序使用的语言
    private Timer timer;//整个app的倒计时
    private boolean isCountDowning;//true：timer运行中，false:timer==null
    private String tel;
    public WeakHandler weakHandler;
    private int countDownNum = 120;//倒计时总数
    private long curTime = 0;//用来标记verifyCodeBean的有效时长
    private LocalBroadcastManager localBroadcastManager;//本地广播manager

    public static final int APP_HANDER_WHAT_FLAG = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        initOkGo();
        initLanguage();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        weakHandler = new WeakHandler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                if (message.what == APP_HANDER_WHAT_FLAG) {
                    Intent intent = new Intent();
                    intent.setAction(Configer.LOCAL_COUNTDOWN_ACTION);
                    intent.putExtra(Configer.LOCAL_COUNTDOWN_KEY, countDownNum);
                    intent.putExtra(Configer.LOCAL_COUNTDOWN_TEL, tel);
                    localBroadcastManager.sendBroadcast(intent);
                    if (countDownNum == 0) {
                        //倒计时结束
                        stopTimer();
                    } else {
                        countDownNum--;
                    }
                }
                return false;
            }
        });
    }


    private void initLanguage() {
        Locale phoneLocal = CommonUtils.getCurrentLocale(getApplicationContext());
        appLocal = CommonUtils.getUserLocale(getApplicationContext());
        if (appLocal == null) {
            appLocal = phoneLocal;
        } else {
        }
    }

    /**
     * init Okgo
     */
    private void initOkGo() {

        //配置HttpClient全局参数
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //log打印级别，决定了log显示的详细程度
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
        //log颜色级别，决定了log在控制台显示的颜色
        loggingInterceptor.setColorLevel(Level.INFO);
        builder.addInterceptor(loggingInterceptor);
        //使用sp保持cookie，如果cookie不过期，则一直有效
//        builder.cookieJar(new CookieJarImpl(new SPCookieStore(this)));
        //使用数据库保持cookie，如果cookie不过期，则一直有效
//        builder.cookieJar(new CookieJarImpl(new DBCookieStore(this)));
        //使用内存保持cookie，app退出后，cookie消失
        builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));


        //方法一：信任所有证书,不安全有风险
        HttpsUtils.SSLParams sslParams1 = HttpsUtils.getSslSocketFactory();
        builder.sslSocketFactory(sslParams1.sSLSocketFactory, sslParams1.trustManager);
        //------------------------配置OkGo全局参数-------------------------


        OkGo.getInstance().init(this)                       //必须调用初始化
                .setOkHttpClient(builder.build())               //建议设置OkHttpClient，不设置将使用默认的
                .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(3);                           //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
        //.addCommonHeaders(headers)                      //全局公共头
        //.addCommonParams(params);                       //全局公共参数
    }

    public boolean isCountDowning() {
        return isCountDowning;
    }

    public void setCountDowning(boolean countDowning) {
        isCountDowning = countDowning;
    }

    /**
     * 停止倒计时
     */
    private void stopTimer() {
        isCountDowning = false;
        tel = "";
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * 开始倒计时
     */
    public void startTimer(String tel) {
        isCountDowning = true;
        this.tel = tel;
        countDownNum = 120;
        if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                weakHandler.sendEmptyMessage(APP_HANDER_WHAT_FLAG);
            }
        }, 0, 1000);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        Locale _UserLocale = CommonUtils.getUserLocale(this);
        //系统语言改变了应用保持之前设置的语言
        if (_UserLocale != null) {
            Locale.setDefault(_UserLocale);
            Configuration _Configuration = new Configuration(newConfig);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                _Configuration.setLocale(_UserLocale);
            } else {
                _Configuration.locale = _UserLocale;
            }
            getResources().updateConfiguration(_Configuration, getResources().getDisplayMetrics());
        }

    }
}
