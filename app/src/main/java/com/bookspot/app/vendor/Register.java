package com.bookspot.app.vendor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    EditText  owner_name, contact_no;
    TextView send_otp;
    public static String  o_name,  c_no;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        createNotificationChannel();
        initialize();

        send_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                o_name = owner_name.getText().toString();
                c_no = contact_no.getText().toString();

                if(o_name.isEmpty())
                    owner_name.setError("Enter the owner name");
                if(c_no.isEmpty() || c_no.length() != 10)
                    contact_no.setError("Enter the valid Contact No.");

                if(!o_name.isEmpty() && isPhoneNumberCorrect(c_no)){
                    startActivity(new Intent(Register.this, Verification.class));
                    finish();
                }
            }
        });
    }

    private void initialize() {
        owner_name = (EditText) findViewById(R.id.owner_name);
        contact_no = (EditText) findViewById(R.id.contact_no);
        send_otp = (TextView) findViewById(R.id.send_otp);
    }

    private boolean isPhoneNumberCorrect(String phone) {
        return Pattern.matches("[0-9]{10}", phone);
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

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Register.super.onBackPressed();
            }
        });
        builder.setNegativeButton("No", null);

        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.orange));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.orange));
            }
        });
        dialog.show();
    }
}