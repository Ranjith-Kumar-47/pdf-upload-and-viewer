package com.example.pdfuploadandviewer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URLEncoder;

public class PdfViewer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        String name = getIntent().getStringExtra("name");
        String url = getIntent().getStringExtra("url");
        WebView webView = findViewById(R.id.webView);

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(name);
        progressDialog.setMessage("Opening....!");

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressDialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressDialog.dismiss();
            }
        });

        String urls = "";
        try {
            urls = URLEncoder.encode(url,"UTF-8");
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setDisplayZoomControls(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://docs.google.com/gview?embedded=true&url="+urls);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}