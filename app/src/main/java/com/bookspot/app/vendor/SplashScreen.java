package com.bookspot.app.vendor;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class SplashScreen extends AppCompatActivity {

    public static final String CHANNEL_ID = "booking";
    public static Container_Class.Vendor vendor = new Container_Class.Vendor();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        createNotificationChannel();

        View decor = getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        vendor = new Container_Class.Vendor();

        Handler handler = new Handler();
      /*  handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = SplashScreen.this.getSharedPreferences("user", MODE_PRIVATE);
                if(sharedPreferences.getBoolean("logged", false)) {

                    if(sharedPreferences.getString("add", "").equals("")) {
                        vendor.setOname(sharedPreferences.getString("oname", ""));
                        vendor.setUID(sharedPreferences.getString("UID", ""));
                        vendor.setFname(sharedPreferences.getString("fname", ""));
                        vendor.setCno(sharedPreferences.getString("cno", ""));
                        startActivity(new Intent(SplashScreen.this, AddBusiness.class));
                    }

                    else if(sharedPreferences.getString("services", "").equals("")) {
                        vendor.setOname(sharedPreferences.getString("oname", ""));
                        vendor.setUID(sharedPreferences.getString("UID", ""));
                        vendor.setFname(sharedPreferences.getString("fname", ""));
                        vendor.setCno(sharedPreferences.getString("cno", ""));
                        vendor.setAdd(sharedPreferences.getString("add", ""));
                        vendor.setAddno(sharedPreferences.getString("addno", ""));
                        vendor.setEmail(sharedPreferences.getString("email", ""));
                        vendor.setWebsite(sharedPreferences.getString("website", ""));
                        startActivity(new Intent(SplashScreen.this, Categories.class));
                    }

                    else if(sharedPreferences.getString("img", "").equals("")) {
                        vendor.setOname(sharedPreferences.getString("oname", ""));
                        vendor.setUID(sharedPreferences.getString("UID", ""));
                        vendor.setFname(sharedPreferences.getString("fname", ""));
                        vendor.setCno(sharedPreferences.getString("cno", ""));
                        vendor.setAdd(sharedPreferences.getString("add", ""));
                        vendor.setAddno(sharedPreferences.getString("addno", ""));
                        vendor.setEmail(sharedPreferences.getString("email", ""));
                        vendor.setWebsite(sharedPreferences.getString("website", ""));
                        vendor.setCat(sharedPreferences.getString("cat", ""));
                        vendor.setServices(sharedPreferences.getString("services", ""));
                        startActivity(new Intent(SplashScreen.this, FirstTime.class));
                    }

                    else {
                        vendor.setOname(sharedPreferences.getString("oname", ""));
                        vendor.setUID(sharedPreferences.getString("UID", ""));
                        vendor.setFname(sharedPreferences.getString("fname", ""));
                        vendor.setCno(sharedPreferences.getString("cno", ""));
                        vendor.setAdd(sharedPreferences.getString("add", ""));
                        vendor.setAddno(sharedPreferences.getString("addno", ""));
                        vendor.setEmail(sharedPreferences.getString("email", ""));
                        vendor.setWebsite(sharedPreferences.getString("website", ""));
                        vendor.setCat(sharedPreferences.getString("cat", ""));
                        vendor.setServices(sharedPreferences.getString("services", ""));
                        vendor.setImage(sharedPreferences.getString("img", ""));
                        vendor.setTotal_tokens(sharedPreferences.getString("total_tokens", ""));
                        vendor.setSdate(sharedPreferences.getString("sdate", ""));
                        vendor.setStime(sharedPreferences.getString("stime", ""));
                        vendor.setRat(sharedPreferences.getString("rat", ""));

                        startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    }
                }else
                    startActivity(new Intent( SplashScreen.this, Register.class));

                finish();
            }
        }, 2000);

       */

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this, Extra.class));
            }
        }, 2000);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Booking Alert";
            String description = "New Booking";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(SplashScreen.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}