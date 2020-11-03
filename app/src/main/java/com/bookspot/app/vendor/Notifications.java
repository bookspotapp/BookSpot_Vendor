package com.bookspot.app.vendor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class Notifications extends AppCompatActivity {

    ImageView back;
    SharedPreferences sharedPreferences;
    SwitchMaterial sound, vibrate, next_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        sharedPreferences = Notifications.this.getSharedPreferences("user", MODE_PRIVATE);
        initializeit();
    }

    private void initializeit() {
        sound = (SwitchMaterial) findViewById(R.id.sound);
        vibrate = (SwitchMaterial) findViewById(R.id.vibrate);
        next_token = (SwitchMaterial) findViewById(R.id.next_token);

        back = (ImageView) findViewById(R.id.back_arrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    sharedPreferences.edit().putBoolean("sound", true).apply();
                else
                    sharedPreferences.edit().putBoolean("sound", false).apply();
            }
        });

        vibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    sharedPreferences.edit().putBoolean("vibrate", true).apply();
                else
                    sharedPreferences.edit().putBoolean("vibrate", false).apply();
            }
        });

        next_token.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    sharedPreferences.edit().putBoolean("next", true).apply();
                else
                    sharedPreferences.edit().putBoolean("next", false).apply();
            }
        });

        sound.setChecked(sharedPreferences.getBoolean("sound", false));
        vibrate.setChecked(sharedPreferences.getBoolean("vibrate", false));
        next_token.setChecked(sharedPreferences.getBoolean("next", false));
    }
}