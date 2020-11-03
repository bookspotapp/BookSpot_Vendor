package com.bookspot.app.vendor;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Range;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.RangeSlider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class ManageQueue extends AppCompatActivity implements View.OnClickListener {

    TextView  save, ftiming, fmor, lmor, feve, leve;
    EditText tkn_lmt, bkd;
    ImageView back;
    Spinner spinner;
    MaterialButton bt_fmor, bt_lmor, bt_feve, bt_leve;

    List<String> list = Arrays.asList("Choose from Below",
            "Sunday",
            "Monday",
            "Tuesday",
            "Wednesday",
            "Thrusday",
            "Friday",
            "Saturday");

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_queue);
        createNotificationChannel();

        initioalizeit();

    }

    private void initioalizeit() {

        fmor = (EditText) findViewById(R.id.fmor);
        lmor = (EditText) findViewById(R.id.lmor);
        feve = (EditText) findViewById(R.id.feve);
        leve = (EditText) findViewById(R.id.leve);
        bt_feve = (MaterialButton) findViewById(R.id.bt_feve);
        bt_fmor = (MaterialButton) findViewById(R.id.bt_fmor);
        bt_leve = (MaterialButton) findViewById(R.id.bt_leve);
        bt_lmor = (MaterialButton) findViewById(R.id.bt_lmor);

        bt_leve.setOnClickListener(this);
        bt_fmor.setOnClickListener(this);
        bt_feve.setOnClickListener(this);
        bt_lmor.setOnClickListener(this);

        tkn_lmt = (EditText) findViewById(R.id.tkn_lmt);
        bkd = (EditText) findViewById(R.id.bkd);

        final SharedPreferences sharedPreferences = ManageQueue.this.getSharedPreferences("user", MODE_PRIVATE);
        tkn_lmt.setText(sharedPreferences.getString("total_token", ""));
        bkd.setText(sharedPreferences.getString("sbkd", ""));

        spinner = (Spinner) findViewById(R.id.optional_offs);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item,list);
        spinner.setAdapter(adapter);
        spinner.setSelection(sharedPreferences.getInt("op_offs", 0));

        save = (TextView) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int op_off = spinner.getSelectedItemPosition();
                String tkn = tkn_lmt.getText().toString();
                String bkds = bkd.getText().toString();

                if(tkn.isEmpty())
                    tkn_lmt.setError("Enter the daily token limit");
                if(bkds.isEmpty())
                    bkd.setError("Enter the no. of seats can bbe booked before opening");

               if( fmor != null && !fmor.getText().toString().isEmpty()
                        && feve != null && !feve.getText().toString().isEmpty()
                        && leve != null && !leve.getText().toString().isEmpty()
                        && lmor != null && !lmor.getText().toString().isEmpty()
                        && !tkn.isEmpty() && !bkds.isEmpty()) {

                   String timming = fmor.getText().toString() + " - "  + leve.getText().toString();
                   String ltiming;

                   String op;
                   switch (op_off){
                       case 0:
                           op = "no" ;
                           break;
                       case 1:
                           op ="Sunday";
                           break;
                       case 2:
                           op = "Monday";
                           break;
                       case 3:
                           op = "Tuesday";
                           break;
                       case 4:
                           op = "Wednesday";
                           break;
                       case 5:
                           op = "Thrusday";
                           break;
                       case 6:
                           op = "Friday";
                           break;
                       case 7:
                           op = "Saturday";
                           break;
                       default:
                           op =  "no";
                           break;
                   }

                   if(lmor.getText().toString().equals(feve.getText().toString()))
                       ltiming = "no";
                   else
                       ltiming = feve.getText().toString() + " - "  + lmor.getText().toString();;

                   System.out.println("\n op_off = " + op_off);

                   DatabaseReference ref = FirebaseDatabase.getInstance().getReference("vendors/" + SplashScreen.vendor.getUID());
                   ref.child("timing").setValue(timming);
                   ref.child("lunch").setValue(ltiming);
                   ref.child("total_tokens").setValue(tkn);
                   ref.child("sbkd").setValue(bkds);
                   ref.child("op").setValue(op);

                   sharedPreferences.edit().putString("total_tokens", tkn).apply();
                   sharedPreferences.edit().putString("sbkd", bkds).apply();
                   sharedPreferences.edit().putInt("op_offs", op_off).apply();

                   Toast.makeText(ManageQueue.this, "Data Saved", Toast.LENGTH_SHORT).show();
                   finish();
               }else{
                   Toast.makeText(ManageQueue.this, "Data  not Saved", Toast.LENGTH_SHORT).show();
                   finish();
               }
            }
        });

        back = (ImageView) findViewById(R.id.back_arrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ftiming = (TextView) findViewById(R.id.firm_timings);
        ftiming.setText(SplashScreen.vendor.getCat() +  " Timmings ");
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
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.bt_feve:
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ManageQueue.this , R.style.abirStyle,new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        feve.setVisibility(View.VISIBLE);
                        bt_feve.setVisibility(View.INVISIBLE);
                        feve.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
                mTimePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(ManageQueue.this, R.color.orange));
                mTimePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(ManageQueue.this, R.color.orange));
                break;

            case R.id.bt_fmor:
                Calendar mcurrentTime1 = Calendar.getInstance();
                int hour1 = mcurrentTime1.get(Calendar.HOUR_OF_DAY);
                int minute1 = mcurrentTime1.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker1;
                mTimePicker = new TimePickerDialog(ManageQueue.this, R.style.abirStyle, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        fmor.setVisibility(View.VISIBLE);
                        bt_fmor.setVisibility(View.INVISIBLE);
                        fmor.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour1, minute1, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
                mTimePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(ManageQueue.this, R.color.orange));
                mTimePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(ManageQueue.this, R.color.orange));
                break;

            case R.id.bt_leve:
                Calendar mcurrentTime2 = Calendar.getInstance();
                int hour2 = mcurrentTime2.get(Calendar.HOUR_OF_DAY);
                int minute2 = mcurrentTime2.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker2;
                mTimePicker = new TimePickerDialog(ManageQueue.this, R.style.abirStyle, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        leve.setVisibility(View.VISIBLE);
                        bt_leve.setVisibility(View.INVISIBLE);
                        leve.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour2, minute2, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
                mTimePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(ManageQueue.this, R.color.orange));
                mTimePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(ManageQueue.this, R.color.orange));
                break;

            case R.id.bt_lmor:
                Calendar mcurrentTime3 = Calendar.getInstance();
                int hour3 = mcurrentTime3.get(Calendar.HOUR_OF_DAY);
                int minute3 = mcurrentTime3.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker3;
                mTimePicker = new TimePickerDialog(ManageQueue.this, R.style.abirStyle, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        lmor.setVisibility(View.VISIBLE);
                        bt_lmor.setVisibility(View.INVISIBLE);
                        lmor.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour3, minute3, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
                mTimePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(ManageQueue.this, R.color.orange));
                mTimePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(ManageQueue.this, R.color.orange));
                break;
        }
    }

}