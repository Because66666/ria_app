package com.because.ria_app;

import android.content.Context;
import android.os.Build;
import android.webkit.JavascriptInterface;

public class JsInterface {
    private Context mContext;

    public JsInterface(Context context) {
        this.mContext = context;
    }

    @JavascriptInterface
    public String getDeviceName() {
        // 返回设备名称
        return Build.MODEL;
    }
}
