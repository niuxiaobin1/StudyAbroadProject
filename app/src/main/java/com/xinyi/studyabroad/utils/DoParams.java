package com.xinyi.studyabroad.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.lzy.okgo.model.HttpParams;
import com.xinyi.studyabroad.base.StudyAbroadApp;

import java.util.Locale;

/**
 * Created by Niu on 2018/4/20.
 */

public class DoParams {

    public static HttpParams encryptionparams(Context context, HttpParams httpParams, String user_token) {

        String time = String.valueOf(System.currentTimeMillis() / 1000);
        String salt = "fjsadhfkjashfhwruefhijoishfeu";
        httpParams.put("t", time);
        Activity activity = (Activity) context;
        if (!((StudyAbroadApp) activity.getApplication()).getAppLocal().getLanguage().equals("zh")) {
            httpParams.put("lan", "1");
        } else {
            httpParams.put("lan", "0");
        }
        if (TextUtils.isEmpty(user_token)) {
            user_token = salt + time;
        } else {
            user_token = user_token + salt + time;
        }
        httpParams.put("api_token", EncryUtil.MD5(user_token));
        return httpParams;
    }
}
