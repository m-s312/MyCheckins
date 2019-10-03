package android.bignerdranch.mycheckins;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class HelpWebView extends AppCompatActivity {
    private WebView mWebView;
    private ProgressBar mProgressbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_help);

        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.setWebViewClient(new WebViewClient(){
            public boolean shoulderOverrideURlLoading (WebView view, WebResourceRequest request){
                return false;
            }
        });

        mWebView.loadUrl("https://www.wikihow.com/Check-In_on_Facebook");

        mProgressbar = (ProgressBar) findViewById(R.id.progress_bar);
        mProgressbar.setMax(100);
        mWebView.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView webView, int newProgress) {
                if (newProgress == 100) {
                    mProgressbar.setVisibility(View.GONE);
                } else {
                    mProgressbar.setVisibility(View.VISIBLE);
                    mProgressbar.setProgress(newProgress);
                }
            }
            public void onReceivedTitle(WebView webView, String title){
                getSupportActionBar().setSubtitle(title);
            }
        });

    }
    public void OnBackPressed(){
        if (mWebView.canGoBack()){
            mWebView.goBack();
        }else {
            super.onBackPressed();
        }
    }
    public static Intent newIntent(Context packageContext){

        Intent intent = new Intent(packageContext, HelpWebView.class);
        return intent;

    }
}
