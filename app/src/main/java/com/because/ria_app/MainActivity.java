package com.because.ria_app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;
import com.because.ria_app.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private WebView webView;
    private ValueCallback<Uri[]> mFilePathCallback;  // 文件路径回调
    private static final int FILECHOOSER_RESULTCODE = 2; // 请求码
    private ActivityResultLauncher<Intent> filePickerLauncher;

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


        // 添加 WebViewClient 和 WebChromeClient
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient() {
                                       // For Android >= 5.0
                                       public boolean onShowFileChooser(ValueCallback<Uri[]> filePathCallback,
                                                                        FileChooserParams fileChooserParams) {
                                           if (mFilePathCallback != null) {
                                               mFilePathCallback.onReceiveValue(null);
                                           }
                                           mFilePathCallback = filePathCallback;
                                           showFileChooser(filePathCallback, fileChooserParams.getFilenameHint());
                                           return true;
                                       }
                                   });

        
        BottomNavigationView navView = binding.navView;

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.sign_in, R.id.map, R.id.luntan, R.id.wiki)
                .build();


        // 添加监听器以处理点击事件
        navView.setOnItemSelectedListener(this::onNavigationItemSelected);

        // 默认加载第一个页面
        loadUrl("https://bbs.ria.red/checkin");
    }


    private void showFileChooser(ValueCallback<Uri[]> filePathCallback, String acceptType) {
        Intent intent = createFilePickerIntent(acceptType);
        if (Build.VERSION.SDK_INT < 30) {
            // For API levels below 30, use startActivityForResult
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, FILECHOOSER_RESULTCODE);

        } else {
            // For API level 30 and above, use ActivityResultLauncher
            filePickerLauncher.launch(intent);
        }
    }

    private Intent createFilePickerIntent(String acceptType) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*"); // 或者根据 acceptType 设置特定类型
        return intent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (mFilePathCallback == null) {
                return;
            }
            Uri result = null;
            if (resultCode != RESULT_OK) {
                mFilePathCallback.onReceiveValue(null);
                return;
            }
            if (data == null) {
                mFilePathCallback.onReceiveValue(null);
                return;
            }
            result = data.getData();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mFilePathCallback.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
            } else {
                mFilePathCallback.onReceiveValue(new Uri[]{result});
            }
            mFilePathCallback = null;
        }
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
