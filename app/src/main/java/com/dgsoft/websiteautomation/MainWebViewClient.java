package com.dgsoft.websiteautomation;

import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainWebViewClient extends WebViewClient
{
    private boolean isFirstRun = true;

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error)
    {
        handler.proceed(); // Ignore SSL certificate errors
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request)
    {
        // Handle the URL here
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(request.getUrl().toString()));
        view.getContext().startActivity(browserIntent);

        return true; // Return true to indicate that you've handled the URL
    }
    public void Connect(WebView view)
    {
        try
        {
            BrowseToPersonalSection(view);

            Thread.sleep(500);

            EnterUserInformation(view);

            Thread.sleep(1000);

            PressSmsButton(view);

            BrowseToAcount(view);
        }
        catch (InterruptedException e)
        {
            Log.d("WebView", e.getMessage());
        }
    }

    public void settings(WebView view)
    {
        try
        {
            view.evaluateJavascript("const propertiesButton = document.querySelector('#plan > div > div > div:nth-child(3) > button:nth-child(2)');\n" +
                    "propertiesButton.click();\n", null);

            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            Log.d("WebView", e.getMessage());
        }
    }

    @Override
    public void onPageFinished(WebView view, String url)
    {
        // Inject JavaScript to fill in fields and click buttons
        if (!isFirstRun) return;

        isFirstRun = false;

        HandleLogging(view);
    }

    private void HandleLogging(WebView view)
    {
        // Handle logging
        view.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage)
            {
                Log.d("WebView", consoleMessage.message());
                return true;
            }
        });
    }

    private void PressSmsButton(WebView view)
    {
        view.evaluateJavascript("const smsButton = document.querySelector('#main > div > div > form > div.fieldOTP > p > button:nth-child(2)');\n" +
                "smsButton.click();\n", null);
    }

    private void EnterUserInformation(WebView view)
    {
        view.evaluateJavascript("const phoneInput = document.querySelector('#main > div > div > form > p:nth-child(1) > input[type=tel]');\n" + "phoneInput.value = '0544271250';\n" +
                "const ccInput = document.querySelector('#main > div > div > form > p.required.field.fieldString.pay > input[type=tel]');\n" + "ccInput.value = '7693';\n" +
                "var event = new Event('input', { bubbles: true });\n" + "phoneInput.dispatchEvent(event);\n" +
                "ccInput.dispatchEvent(event);\n", null);
    }

    private void BrowseToPersonalSection(WebView view)
    {
        view.evaluateJavascript("const personalButton = document.querySelector('#main > div > div.wrap2 > a.my > p');\n" +
                "personalButton.click();\n", null);
    }
    private void BrowseToAcount(WebView view)
    {
        view.loadUrl("https://mobile.rami-levy.co.il/My/manageSubs/subDetails?sub=0");
    }
}
