package com.fakegps.optimustechproject.fakegps;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Locale;


public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;
    protected boolean _active = true;
    protected int _splashTime = 3000;
    ImageView iv;
    String curr_lang;
    Locale myLocale;

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        StartAnimations();
    }
    private void StartAnimations() {

        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (_active && (waited < _splashTime)) {
                        sleep(100);
                        if (_active) {
                            waited += 100;
                        }
                    }
                } catch (Exception e) {

                } finally {
                    if(DbHandler.contains(SplashActivity.this,"language")){
                        curr_lang=DbHandler.getString(SplashActivity.this,"language","");
                    }
                    else{
                        curr_lang="english";
                        DbHandler.putString(SplashActivity.this,"language","english");
                    }

                    if (curr_lang.equals("english")) {
                        changeLang("en");
                    }
                    else {
                        changeLang("hi");
                    }

                    if(DbHandler.getBoolean(SplashActivity.this,"isLoggedIn",false)){
                        Intent intent = new Intent(SplashActivity.this,NavigationActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Intent intent = new Intent(SplashActivity.this, NavigationActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            };
        };
        splashTread.start();

    }

    public void changeLang(String lang)
    {
        if (lang.equalsIgnoreCase(""))
            return;
        myLocale = new Locale(lang);
        //saveLocale(lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        //updateTexts();

    }

    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (myLocale != null){
            newConfig.locale = myLocale;
            Locale.setDefault(myLocale);
            getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
        }
    }
}