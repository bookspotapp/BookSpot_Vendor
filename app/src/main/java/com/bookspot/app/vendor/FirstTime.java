package com.bookspot.app.vendor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bookspot.app.vendor.SplashScreen;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.RangeSlider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class FirstTime extends AppCompatActivity implements View.OnClickListener, LocationListener {

    TextView f_name;
    EditText date_picker, t_token, fmor, lmor, feve, leve;
    MaterialButton pick_date, submit, bt_fmor, bt_lmor, bt_feve, bt_leve;
    String date, activity, img, total_token;
    ImageView add_image;
    Bitmap final_img;
    int GET_IMAGE = 786;
    int REQUEST_PERMISSION_LOCATION = 234;
    double lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time);

        initializeit();


    }

    private void initializeit() {
        activity = getIntent().getStringExtra("activity");
        if (activity != null && activity.equals("verification")) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("vendors/" + SplashScreen.vendor.getUID());
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Container_Class.Vendor vendor = snapshot.getValue(Container_Class.Vendor.class);

                    SharedPreferences sharedPreferences = FirstTime.this.getSharedPreferences("user", MODE_PRIVATE);
                    sharedPreferences.edit().putString("add", vendor.getAdd()).apply();
                    sharedPreferences.edit().putString("email", vendor.getEmail()).apply();
                    sharedPreferences.edit().putString("website", vendor.getWebsite()).apply();
                    sharedPreferences.edit().putString("addno", vendor.getAddno()).apply();
                    sharedPreferences.edit().putString("cat", vendor.getCat()).apply();
                    sharedPreferences.edit().putString("services", vendor.getServices()).apply();
                    sharedPreferences.edit().putString("rat", vendor.getRat()).apply();
                    sharedPreferences.edit().putBoolean("sound", true).apply();
                    sharedPreferences.edit().putBoolean("vibrate", true).apply();
                    sharedPreferences.edit().putBoolean("next", true).apply();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        submit = (MaterialButton) findViewById(R.id.submit);
        date_picker = (EditText) findViewById(R.id.day);
        t_token = (EditText) findViewById(R.id.tokens);
        fmor = (EditText) findViewById(R.id.fmor);
        lmor = (EditText) findViewById(R.id.lmor);
        feve = (EditText) findViewById(R.id.feve);
        leve = (EditText) findViewById(R.id.leve);
        bt_feve = (MaterialButton) findViewById(R.id.bt_feve);
        bt_fmor = (MaterialButton) findViewById(R.id.bt_fmor);
        bt_leve = (MaterialButton) findViewById(R.id.bt_leve);
        bt_lmor = (MaterialButton) findViewById(R.id.bt_lmor);
        pick_date = (MaterialButton) findViewById(R.id.pick_date);
        add_image = (ImageView) findViewById(R.id.add_img);

        f_name = (TextView) findViewById(R.id.firm_name);
        f_name.setText("Welcome " + SplashScreen.vendor.getFname());

        date_picker.setVisibility(View.INVISIBLE);
        feve.setVisibility(View.INVISIBLE);
        fmor.setVisibility(View.INVISIBLE);
        leve.setVisibility(View.INVISIBLE);
        lmor.setVisibility(View.INVISIBLE);

        bt_leve.setOnClickListener(this);
        bt_fmor.setOnClickListener(this);
        bt_feve.setOnClickListener(this);
        bt_lmor.setOnClickListener(this);
        submit.setOnClickListener(this);
        pick_date.setOnClickListener(this);
        add_image.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_img:
                checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, GET_IMAGE);
                break;

            case R.id.pick_date:
                System.out.println("pick_date clicked");

                DatePickerDialog picker;
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                pick_date.setVisibility(View.INVISIBLE);
                                date_picker.setVisibility(View.VISIBLE);
                                date_picker.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                date = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                            }
                        }, year, month, day);
                picker.show();
                picker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(FirstTime.this, R.color.orange));
                picker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(FirstTime.this, R.color.orange));
                break;

            case R.id.bt_feve:
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(FirstTime.this, R.style.abirStyle, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        feve.setVisibility(View.VISIBLE);
                        bt_feve.setVisibility(View.INVISIBLE);
                        feve.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
                mTimePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(FirstTime.this, R.color.orange));
                mTimePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(FirstTime.this, R.color.orange));
                break;

            case R.id.bt_fmor:
                Calendar mcurrentTime1 = Calendar.getInstance();
                int hour1 = mcurrentTime1.get(Calendar.HOUR_OF_DAY);
                int minute1 = mcurrentTime1.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker1;
                mTimePicker = new TimePickerDialog(FirstTime.this, R.style.abirStyle, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        fmor.setVisibility(View.VISIBLE);
                        bt_fmor.setVisibility(View.INVISIBLE);
                        fmor.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour1, minute1, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
                mTimePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(FirstTime.this, R.color.orange));
                mTimePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(FirstTime.this, R.color.orange));
                break;

            case R.id.bt_leve:
                Calendar mcurrentTime2 = Calendar.getInstance();
                int hour2 = mcurrentTime2.get(Calendar.HOUR_OF_DAY);
                int minute2 = mcurrentTime2.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker2;
                mTimePicker = new TimePickerDialog(FirstTime.this, R.style.abirStyle, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        leve.setVisibility(View.VISIBLE);
                        bt_leve.setVisibility(View.INVISIBLE);
                        leve.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour2, minute2, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
                mTimePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(FirstTime.this, R.color.orange));
                mTimePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(FirstTime.this, R.color.orange));
                break;

            case R.id.bt_lmor:
                Calendar mcurrentTime3 = Calendar.getInstance();
                int hour3 = mcurrentTime3.get(Calendar.HOUR_OF_DAY);
                int minute3 = mcurrentTime3.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker3;
                mTimePicker = new TimePickerDialog(FirstTime.this, R.style.abirStyle, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        lmor.setVisibility(View.VISIBLE);
                        bt_lmor.setVisibility(View.INVISIBLE);
                        lmor.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour3, minute3, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
                mTimePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(FirstTime.this, R.color.orange));
                mTimePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(FirstTime.this, R.color.orange));
                break;

            case R.id.submit:
                checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_PERMISSION_LOCATION);
                break;
        }
    }

    private void savethedata() {
        System.out.println("\n inside save the data ");
        total_token = t_token.getText().toString();
        if (total_token.isEmpty())
            t_token.setError("Enter no. of tokens");
        if (date == null || date.isEmpty())
            Toast.makeText(FirstTime.this, "Please select a date to start", Toast.LENGTH_SHORT).show();
        if (img == null || img.isEmpty())
            Toast.makeText(FirstTime.this, "Please select a logo or image\n for your business", Toast.LENGTH_SHORT).show();
        if (lat == 0.0)
            Toast.makeText(FirstTime.this, "Please allow location Access\n to get your location", Toast.LENGTH_SHORT).show();

        if (!total_token.isEmpty() && date !=null && !date.isEmpty() &&  img !=null && !img.isEmpty() && lat != 0.0
                && fmor != null && !fmor.getText().toString().isEmpty()
                && feve != null && !feve.getText().toString().isEmpty()
                && leve != null && !leve.getText().toString().isEmpty()
                && lmor != null && !lmor.getText().toString().isEmpty()) {

            String timming = fmor.getText().toString() + " - " + leve.getText().toString();
            String ltiming;


            if (lmor.getText().toString().equals(feve.getText().toString()))
                ltiming = "no";
            else
                ltiming = feve.getText().toString() + " - " + lmor.getText().toString();
            ;

            SharedPreferences sharedPreferences = FirstTime.this.getSharedPreferences("user", MODE_PRIVATE);
            sharedPreferences.edit().putString("img", img).apply();
            sharedPreferences.edit().putString("total_token", total_token).apply();
            sharedPreferences.edit().putString("sdate", date).apply();
            sharedPreferences.edit().putString("stime", timming).apply();
            sharedPreferences.edit().putString("ltiming", ltiming).apply();
            sharedPreferences.edit().putString("lat", String.valueOf(lat)).apply();
            sharedPreferences.edit().putString("lng", String.valueOf(lng)).apply();
            sharedPreferences.edit().putString("rat", "0,0,0,0,0").apply();
            sharedPreferences.edit().putBoolean("sound", true).apply();
            sharedPreferences.edit().putBoolean("vibrate", true).apply();
            sharedPreferences.edit().putBoolean("next", true).apply();

            SplashScreen.vendor.setTotal_tokens(total_token);
            SplashScreen.vendor.setSdate(date);
            SplashScreen.vendor.setStime(timming);
            SplashScreen.vendor.setLtiming(ltiming);
            SplashScreen.vendor.setLat(lat);
            SplashScreen.vendor.setLng(lng);
            SplashScreen.vendor.setRat("0,0,0,0,0");

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("vendors");
            ref.child(SplashScreen.vendor.getUID()).setValue(SplashScreen.vendor);

            SplashScreen.vendor.setImage(img);

            saveDataToStorage();
            saveDataToSepDB();
            
            DatabaseReference orders = FirebaseDatabase.getInstance().getReference("orders/" + SplashScreen.vendor.getUID());
            orders.child("tkn").setValue(0);
            orders.child("tkn_d").setValue(0);

            startActivity(new Intent(FirstTime.this, MainActivity.class));
        } else {
            Toast.makeText(FirstTime.this, "Please select timings of your firm", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveDataToSepDB() {
        Container_Class.Det det = new Container_Class.Det(
                SplashScreen.vendor.getFname(),
                SplashScreen.vendor.getUID(),
                SplashScreen.vendor.getLat(),
                SplashScreen.vendor.getLng());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("det");
        ref.child("vendors").child(SplashScreen.vendor.getCat()).child(SplashScreen.vendor.getUID()).setValue(det);
        System.out.println("\n details has been saved to the seperate section of database");
    }

    private void saveDataToStorage() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final_img.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://vendor-22662.appspot.com");
        StorageReference logoRef =  storageRef.child("vendors/"+ SplashScreen.vendor.getUID() + "/logo.jpg");

        UploadTask uploadTask = logoRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                exception.printStackTrace();
                System.out.println("\n Failure in storage exception = ");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                System.out.println("\n Image has been added to the storage");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();

            Bitmap bmp = null;
            try {
                bmp = getBitmapFromUri(selectedImage);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            final_img = bmp;
            add_image.setImageBitmap(bmp);
            img = changeBitmapToStrinng(bmp);
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                FirstTime.this.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        return image;
    }

    private String changeBitmapToStrinng(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        return imageEncoded;
    }


    // Function to check and request permission

    public void checkPermission(String permission, int requestCode) {

        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(FirstTime.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(FirstTime.this, new String[]{permission}, requestCode);
        } else {
            if (requestCode == REQUEST_PERMISSION_LOCATION) {
                GpsTracker gpsTracker = new GpsTracker(FirstTime.this);
                lat = gpsTracker.getLatitude();
                lng = gpsTracker.getLongitude();
                System.out.println("lat = "+ lat + " lng = " + lng);
                savethedata();
            } else {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), GET_IMAGE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == GET_IMAGE) {

            // Checking whether user granted the permission or not.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(FirstTime.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), GET_IMAGE);
            } else {
                Toast.makeText(FirstTime.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(FirstTime.this, "Location Permission Granted", Toast.LENGTH_SHORT).show();

                LocationManager manager = (LocationManager) FirstTime.this.getSystemService(Context.LOCATION_SERVICE);
                boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (statusOfGPS) {
                    System.out.println("\n inside chk");
                    GpsTracker gpsTracker = new GpsTracker(FirstTime.this);
                    lat = gpsTracker.getLatitude();
                    lng = gpsTracker.getLongitude();
                    System.out.println("lat = "+ lat + " lng = " + lng);
                    savethedata();
                }else{
                    Toast.makeText(FirstTime.this, "Please enable your GPS\nfrom setings to proceed", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(FirstTime.this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        System.out.println("\n location changed = "+ location);
        lat = location.getLatitude();
        lng = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                FirstTime.super.onBackPressed();
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