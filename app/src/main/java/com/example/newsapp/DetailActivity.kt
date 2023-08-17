package com.example.newsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        var webView = findViewById<WebView>(R.id.webView)
        val progressBar : ProgressBar = findViewById(R.id.progressBar)
        val url : String? = intent.getStringExtra("URL")
        if(url!=null)
        {
            webView.settings.javaScriptEnabled = true
            webView.webViewClient = object  : WebViewClient(){
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    progressBar.visibility = View.GONE
                    webView.visibility = View.VISIBLE
                }
            }
            webView.loadUrl(url)
        }
    }
}