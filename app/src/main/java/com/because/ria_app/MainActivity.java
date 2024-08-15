package com.because.ria_app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;
import com.because.ria_app.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private WebView webView;


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // 禁用标题栏
        getSupportActionBar().hide();

        // 初始化 WebView
        webView = new WebView(this);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        binding.webContainer.addView(webView);

        // 设置缓存
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setDomStorageEnabled(true); // 启用 DOM 存储
        webSettings.setDatabaseEnabled(true); // 启用数据库存储
        // 启用其他的权限
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setMixedContentMode(webSettings.MIXED_CONTENT_ALWAYS_ALLOW);


        
        BottomNavigationView navView = binding.navView;

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.sign_in, R.id.map, R.id.luntan, R.id.wiki)
                .build();


        // 添加监听器以处理点击事件
        navView.setOnItemSelectedListener(this::onNavigationItemSelected);
        // 注入JavaScript接口
        webView.addJavascriptInterface(new JsInterface(this), "Android");

        // 默认加载第一个页面
        loadUrl("https://bbs.ria.red/checkin");
    }


    private void loadUrl(String url) {
        webView.loadUrl(url);
    }

    private boolean onNavigationItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.sign_in) {
            loadUrl("https://bbs.ria.red/checkin");
        } else if (itemId == R.id.map) {
            loadUrl("https://satellite.ria.red/map/zth");
        } else if (itemId == R.id.luntan) {
            loadUrl("https://bbs.ria.red/");
        } else if (itemId == R.id.wiki) {
            loadUrl("https://wiki.ria.red/");
        } else {
            return false; // 如果是未知的 item，则返回 false
        }

        return true;
    }
}
