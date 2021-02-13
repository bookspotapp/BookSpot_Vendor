package com.bookspot.app.vendor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class Menu extends AppCompatActivity implements View.OnClickListener {

    ImageView cross , back;
    TextView profile, manage, customer, ratings, notification, signout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        initializeit();
    }

    private void initializeit() {
        cross = (ImageView) findViewById(R.id.cross);
        back = (ImageView) findViewById(R.id.back_arrow);
        profile = (TextView) findViewById(R.id.profile_settings);
        manage = (TextView) findViewById(R.id.manage_queue);
        customer = (TextView) findViewById(R.id.customer_data);
        ratings = (TextView) findViewById(R.id.view_your_ratings);
        notification = (TextView) findViewById(R.id.notifications);
        signout = (TextView) findViewById(R.id.signout);

        cross.setOnClickListener(this);
        back.setOnClickListener(this);
        profile.setOnClickListener(this);
        manage.setOnClickListener(this);
        customer.setOnClickListener(this);
        ratings.setOnClickListener(this);
        notification.setOnClickListener(this);
        signout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_arrow:
            case R.id.cross:
                finish();
                break;

            case R.id.profile_settings:
                startActivity(new Intent(Menu.this, ProfileSettings.class));
                break;

            case R.id.manage_queue:
                startActivity(new Intent(Menu.this, ManageQueue.class));
                break;
            case R.id.customer_data:
                startActivity(new Intent(Menu.this, CustomerData.class));
                break;
            case R.id.view_your_ratings:
                startActivity(new Intent(Menu.this, ViewYourRatings.class));
                break;
            case R.id.notifications:
                startActivity(new Intent(Menu.this, Notifications.class));
                break;
            case R.id.signout:
                SharedPreferences sharedPreferences = Menu.this.getSharedPreferences("user", MODE_PRIVATE);
                sharedPreferences.edit().putBoolean("logged", false).apply();
                sharedPreferences.edit().putString("add", "").apply();
                sharedPreferences.edit().putString("email", "").apply();
                sharedPreferences.edit().putString("website","").apply();
                sharedPreferences.edit().putString("addno", "").apply();
                sharedPreferences.edit().putString("cat", "").apply();
                sharedPreferences.edit().putString("services", "").apply();
                sharedPreferences.edit().putString("img","").apply();
                sharedPreferences.edit().putString("total_token", "").apply();
                sharedPreferences.edit().putString("sdate", "").apply();
                sharedPreferences.edit().putString("stime", "").apply();
                sharedPreferences.edit().putString("ltiming", "").apply();
                sharedPreferences.edit().putString("lat","").apply();
                sharedPreferences.edit().putString("lng", "").apply();
                sharedPreferences.edit().putString("rat", "").apply();
                startActivity(new Intent(Menu.this, Register.class));
                finishAffinity();
                FirebaseAuth.getInstance().signOut();
                break;
        }
    }
}