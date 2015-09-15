/*
 * Copyright © Appus Studio LLC 2009 - 2015
 */

package com.appus.splashsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.appus.splash.Splash;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WebView webView = (WebView) findViewById(R.id.web_view);
        webView.loadUrl("https://github.com/roman-voronoy/Appus/blob/master/Splash/readme.md");
        Splash.Builder splash = new Splash.Builder(this, getSupportActionBar());
        splash.perform();
    }
}
