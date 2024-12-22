package com.dgsoft.websiteautomation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity
{
    private SmsReceiver smsReceiver = null;
    private MainWebViewClient _webClient = null;
    private WebView _webView = null;

    @SuppressLint("SetJavaScriptEnabled")
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Request SMS permissions
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED

        )
        {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.RECEIVE_SMS,
                    android.Manifest.permission.SEND_SMS,
                    android.Manifest.permission.READ_SMS,
                    android.Manifest.permission.INTERNET
            }, 1);
        }

        smsReceiver = new SmsReceiver();
        // Register the SMS receiver
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver, filter);

        _webView = findViewById(R.id.webview);
        HandleWebViewSetting(_webView);

        _webClient = new MainWebViewClient();
        _webView.setWebViewClient(_webClient);

        HandleBackPress();
        _webView.loadUrl("https://mobile.rami-levy.co.il/");
    }

    public void connectAccount(View view)
    {
        _webClient.Connect(_webView);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void HandleWebViewSetting(WebView webView)
    {
        WebSettings webSettings = webView.getSettings();
        // Get the default user agent
        String defaultUserAgent = webSettings.getUserAgentString();
        // Modify the user agent to mimic Google Chrome
        String newUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36";
        // Set the new user agent
        webSettings.setJavaScriptEnabled(true);
        WebView.setWebContentsDebuggingEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        // Adjust settings to fit the content
        webSettings.setLoadWithOverviewMode(true);
        //webSettings.setUseWideViewPort(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW); // Allow mixed content
        webSettings.setTextZoom(100);
    }

    private void HandleBackPress()
    {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true)
        {
            @Override
            public void handleOnBackPressed()
            {
                WebView webView = findViewById(R.id.webview);
                if (webView.canGoBack())
                {
                    webView.goBack();
                } else
                {
                    // Handle other back press actions or call enabled = false to allow the default behavior
                    //setEnabled(false);
                    finish();
//                    // Let the system handle the back button press
//                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        // Unregister the SMS receiver
        unregisterReceiver(smsReceiver);
    }

    protected void Authenticate(String smsCode)
    {
        WebView webView = findViewById(R.id.webview);

        try
        {
            webView.evaluateJavascript(
                    "{\n" +
                            "const codeInput = document.querySelector('#main > div > div > form > div.fieldOTP > div > p > input[type=tel]')\n" +
                            "codeInput.value = " + smsCode + "\n" +
                            "const event = new Event('input', { bubbles: true });\n" +
                            "codeInput.dispatchEvent(event);\n" +
                            "}\n",
                    null);

            Thread.sleep(2000);

            webView.evaluateJavascript(
                    "{\n" +
                            "const smsButton = document.querySelector('#main > div > div > form > div.sendAndInfo > button')\n" +
                            "smsButton.click()\n" +
                            "}\n",
                    null);
        } catch (InterruptedException e)
        {
            Log.d("WebView", e.getMessage());
        }
    }
}
