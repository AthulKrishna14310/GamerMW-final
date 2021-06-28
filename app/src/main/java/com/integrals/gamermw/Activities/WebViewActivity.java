package com.integrals.gamermw.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.integrals.gamermw.R;

public class WebViewActivity extends AppCompatActivity {
    private WebView webView;
    private LinearProgressIndicator load;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_web_view);
        webView=findViewById(R.id.webView);
        load=findViewById(R.id.webload);
        String url = getIntent().getStringExtra("url");
        setWebView(url);
    }
        @SuppressLint("SetJavaScriptEnabled")
        private void setWebView(String url) {
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setLoadsImagesAutomatically(true);
            webView.getSettings().setAllowFileAccess(true);
            webView.getSettings().setAllowContentAccess(true);
            webView.getSettings().setDatabaseEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
            webView.setWebChromeClient(new WebChromeClient(){
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    load.setProgressCompat(newProgress,true);
             }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });
        webView.loadUrl(url);
    }
}