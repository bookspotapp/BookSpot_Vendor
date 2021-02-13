package com.bookspot.app.vendor;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Range;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.RangeSlider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class ManageQueue extends AppCompatActivity implements View.OnClickListener {

    TextView ftiming;
    EditText tkn_lmt;
    ImageView back;
    Spinner spinner;
    MaterialButton edit;
    String timing = "";

    List<String> list = Arrays.asList("Choose from Below",
            "Sunday",
            "Monday",
            "Tuesday",
            "Wednesday",
            "Thrusday",
            "Friday",
            "Saturday");

    List<Chip> mor_chip, eve_chip;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timing = SplashScreen.vendor.getStime();
        setContentView(R.layout.activity_manage_queue);
        createNotificationChannel();
        initioalizeit();
        setColorsofChips();
        setOnClickListenersforChips();
        disableAllChips();
    }

    private void setColorsofChips() {
        for(Chip chip : mor_chip){
            if(SplashScreen.vendor.getStime().contains(chip.getText().toString())){
                chip.setTextColor(getResources().getColor(R.color.orange));
                chip.setChipStrokeColor(getResources().getColorStateList(R.color.orange));
            }
        }
        for(Chip chip : eve_chip){
            if(SplashScreen.vendor.getStime().contains(chip.getText().toString())){
                chip.setTextColor(getResources().getColor(R.color.orange));
                chip.setChipStrokeColor(getResources().getColorStateList(R.color.orange));
            }
        }
    }

    private void setOnClickListenersforChips() {
        for(Chip chip : mor_chip){
            chip.setOnClickListener(this);
        }
        for(Chip chip : eve_chip){
            chip.setOnClickListener(this);
        }
    }

    private void disableAllChips() {
        System.out.println("all chips disabled ");
        for(Chip chip : mor_chip){
            chip.setEnabled(false);
        }
        for(Chip chip : eve_chip){
            chip.setEnabled(false);
        }
    }


    private void enableAllChips() {
        System.out.println("all chips enabled ");
        for(Chip chip : mor_chip){
            chip.setEnabled(true);
        }
        for(Chip chip : eve_chip){
            chip.setEnabled(true);
        }
    }

    private void initioalizeit() {

        edit = (MaterialButton) findViewById(R.id.edit);
        spinner = (Spinner) findViewById(R.id.optional_offs);
        tkn_lmt = (EditText) findViewById(R.id.tkn_lmt);

        mor_chip = Arrays.asList(
                (Chip) findViewById(R.id.c6),
                (Chip) findViewById(R.id.c7),
                (Chip) findViewById(R.id.c8),
                (Chip) findViewById(R.id.c9),
                (Chip) findViewById(R.id.c10),
                (Chip) findViewById(R.id.c11),
                (Chip) findViewById(R.id.c12),
                (Chip) findViewById(R.id.c13),
                (Chip) findViewById(R.id.c14)
        );

        eve_chip = Arrays.asList(
                (Chip) findViewById(R.id.c15),
                (Chip) findViewById(R.id.c16),
                (Chip) findViewById(R.id.c17),
                (Chip) findViewById(R.id.c18),
                (Chip) findViewById(R.id.c19),
                (Chip) findViewById(R.id.c20),
                (Chip) findViewById(R.id.c21),
                (Chip) findViewById(R.id.c22),
                (Chip) findViewById(R.id.c23)
        );

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edit.getText().toString().equals("Save Details")){
                    int op_off = spinner.getSelectedItemPosition();
                    String tkn = tkn_lmt.getText().toString();

                    if(tkn.isEmpty())
                        tkn_lmt.setError("Enter the daily token limit");

                    if(!tkn.isEmpty()) {
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

                        System.out.println("\n op_off = " + op_off);
                        System.out.println("\n op = " + op);

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("vendors/" + SplashScreen.vendor.getUID());
                        if(!timing.isEmpty())
                            ref.child("stime").setValue(timing);
                        ref.child("total_tokens").setValue(tkn);
                        ref.child("op").setValue(op);

                        if(!timing.isEmpty())
                            SplashScreen.vendor.setStime(timing);
                        SplashScreen.vendor.setTotal_tokens(tkn);
                        SplashScreen.vendor.setOp(op);

                        final SharedPreferences sharedPreferences = ManageQueue.this.getSharedPreferences("user", MODE_PRIVATE);
                        sharedPreferences.edit().putString("total_tokens", tkn).apply();
                        sharedPreferences.edit().putString("op", op).apply();
                        if(!timing.isEmpty())
                            sharedPreferences.edit().putString("stime", timing).apply();

                        Toast.makeText(ManageQueue.this, "Data Saved", Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Toast.makeText(ManageQueue.this, "Data not Saved", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    System.out.println("timings = "+ timing);
                }else {

                    enableAllChips();
                    edit.setText("Save Details");
                    Drawable icon = getResources().getDrawable(R.drawable.done);
                    edit.setIcon(icon);

                    spinner.setEnabled(true);
                    tkn_lmt.setEnabled(true);

                }
            }
        });



        final SharedPreferences sharedPreferences = ManageQueue.this.getSharedPreferences("user", MODE_PRIVATE);
        tkn_lmt.setText(sharedPreferences.getString("total_tokens", ""));
        tkn_lmt.setEnabled(false);

        System.out.println("getting op from sp = " + sharedPreferences.getString("op", ""));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item,list);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        switch (sharedPreferences.getString("op", "")){
            case "Sunday":
                System.out.println("Sunday");
                spinner.setEnabled(true);
               spinner.setSelection(1);
                break;
            case "Monday":
                System.out.println("monday");
                spinner.setEnabled(true);
                spinner.setSelection(2);
                break;
            case "Tuesday":
                System.out.println("tuesday");
                spinner.setEnabled(true);
                spinner.setSelection(3);
                break;
            case "Wednesday":
                System.out.println("wednesday");
                spinner.setEnabled(true);
                spinner.setSelection(4);
                break;
            case "Thrusday":
                System.out.println("thrusday");
                spinner.setEnabled(true);
                spinner.setSelection(5);
                break;
            case "Friday":
                System.out.println("friday");
                spinner.setEnabled(true);
                spinner.setSelection(6);
                break;
            case "Saturday":
                System.out.println("Saturday");
                spinner.setEnabled(true);
                spinner.setSelection(7);
                break;
            default:
                System.out.println("default");
                spinner.setEnabled(true);
                spinner.setSelection(0);
                break;
        }

        spinner.setEnabled(false);

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
            channel.setVibrationPattern(new long[] { 1000, 1500, 1000, 1500, 1000 });
            channel.enableVibration(true);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    @Override
    public void onClick(View v) {
        Chip chip = findViewById(v.getId());
        if (timing.contains(chip.getText().toString())) {
            System.out.println("timing = " + timing);
            timing = timing.replace(chip.getText().toString() + ";", "");
        } else {
            System.out.println("timing doesnot contain  = " + timing);
            timing += chip.getText().toString() + ";";
        }
    }
/*
        switch (v.getId()) {



            case R.id.feve:
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ManageQueue.this , R.style.abirStyle,new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        feve.setVisibility(View.VISIBLE);
                        feve.setText( format.format(selectedHour) + ":" + format.format(selectedMinute));
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
                mTimePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(ManageQueue.this, R.color.orange));
                mTimePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(ManageQueue.this, R.color.orange));
                break;

            case R.id.fmor:
                Calendar mcurrentTime1 = Calendar.getInstance();
                int hour1 = mcurrentTime1.get(Calendar.HOUR_OF_DAY);
                int minute1 = mcurrentTime1.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker1;
                mTimePicker1 = new TimePickerDialog(ManageQueue.this, R.style.abirStyle, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        fmor.setVisibility(View.VISIBLE);
                        fmor.setText( format.format(selectedHour) + ":" + format.format(selectedMinute));
                    }
                }, hour1, minute1, false);//Yes 24 hour time
                mTimePicker1.setTitle("Select Time");
                mTimePicker1.show();
                mTimePicker1.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(ManageQueue.this, R.color.orange));
                mTimePicker1.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(ManageQueue.this, R.color.orange));
                break;



            case R.id.leve:
                Calendar mcurrentTime2 = Calendar.getInstance();
                int hour2 = mcurrentTime2.get(Calendar.HOUR_OF_DAY);
                int minute2 = mcurrentTime2.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker2;
                mTimePicker2 = new TimePickerDialog(ManageQueue.this, R.style.abirStyle, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        leve.setVisibility(View.VISIBLE);
                        leve.setText(format.format(selectedHour) + ":" + format.format(selectedMinute));
                    }
                }, hour2, minute2, false);//Yes 24 hour time
                mTimePicker2.setTitle("Select Time");
                mTimePicker2.show();
                mTimePicker2.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(ManageQueue.this, R.color.orange));
                mTimePicker2.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(ManageQueue.this, R.color.orange));
                break;


            case R.id.lmor:
                Calendar mcurrentTime3 = Calendar.getInstance();
                int hour3 = mcurrentTime3.get(Calendar.HOUR_OF_DAY);
                int minute3 = mcurrentTime3.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker3;
                mTimePicker3 = new TimePickerDialog(ManageQueue.this, R.style.abirStyle, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        lmor.setVisibility(View.VISIBLE);
                        lmor.setText( format.format(selectedHour) + ":" + format.format(selectedMinute));
                    }
                }, hour3, minute3, false);//Yes 24 hour time
                mTimePicker3.setTitle("Select Time");
                mTimePicker3.show();
                mTimePicker3.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(ManageQueue.this, R.color.orange));
                mTimePicker3.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(ManageQueue.this, R.color.orange));
                break;


        }

 */
}