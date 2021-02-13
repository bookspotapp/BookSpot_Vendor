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
import com.google.android.material.chip.Chip;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.textfield.TextInputEditText;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class FirstTime extends AppCompatActivity implements View.OnClickListener, LocationListener {

    TextView f_name;
    EditText date_picker;
    MaterialButton pick_date, submit;
    String date, activity, img, total_token, timing = "";
    TextInputEditText tkn;
    ImageView add_image;
    Bitmap final_img;

    List<Chip> mor_chip, eve_chip;

    int GET_IMAGE = 786;
    int REQUEST_PERMISSION_LOCATION = 234;
    double lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time);

        initializeit();
        setOnClickListenersforChips();
    }

    private void setOnClickListenersforChips() {
        for(Chip chip : mor_chip){
            chip.setOnClickListener(this);
        }
        for(Chip chip : eve_chip){
            chip.setOnClickListener(this);
        }
    }

    private void initializeit() {
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
        tkn = (TextInputEditText) findViewById(R.id.tkn);
        pick_date = (MaterialButton) findViewById(R.id.pick_date);
        add_image = (ImageView) findViewById(R.id.add_img);

        f_name = (TextView) findViewById(R.id.firm_name);
        f_name.setText("Welcome " + SplashScreen.vendor.getFname());

        date_picker.setVisibility(View.INVISIBLE);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_PERMISSION_LOCATION);
            }
        });

        pick_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("pick_date clicked");

                DatePickerDialog picker;
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(FirstTime.this,
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
            }
        });

        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, GET_IMAGE);
            }
        });

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



    private void savethedata() {
        System.out.println("\n inside save the data ");
        total_token = tkn.getText().toString();
        if (total_token.isEmpty())
            tkn.setError("Enter no. of tokens");
        if (timing.isEmpty())
            Toast.makeText(FirstTime.this, "Please! Select the Firm Timings.", Toast.LENGTH_SHORT).show();
        if (date == null || date.isEmpty())
            Toast.makeText(FirstTime.this, "Please select a date to start", Toast.LENGTH_SHORT).show();
        if (img == null || img.isEmpty())
            Toast.makeText(FirstTime.this, "Please select a logo or image\n for your business", Toast.LENGTH_SHORT).show();
        if (lat == 0.0)
            Toast.makeText(FirstTime.this, "Please allow location Access\n to get your location", Toast.LENGTH_SHORT).show();

        if (!total_token.isEmpty() && date !=null && !date.isEmpty() &&  img !=null && !img.isEmpty() && lat != 0.0 && !timing.isEmpty()) {

            SharedPreferences sharedPreferences = FirstTime.this.getSharedPreferences("user", MODE_PRIVATE);
            sharedPreferences.edit().putString("img", img).apply();
            sharedPreferences.edit().putString("total_tokens", total_token).apply();
            sharedPreferences.edit().putString("sdate", date).apply();
            sharedPreferences.edit().putString("stime", timing).apply();
            sharedPreferences.edit().putString("lat", String.valueOf(lat)).apply();
            sharedPreferences.edit().putString("lng", String.valueOf(lng)).apply();
            sharedPreferences.edit().putString("rat", "0,0,0,0,0").apply();
            sharedPreferences.edit().putBoolean("sound", true).apply();
            sharedPreferences.edit().putBoolean("vibrate", true).apply();
            sharedPreferences.edit().putBoolean("next", true).apply();

            SplashScreen.vendor.setTotal_tokens(total_token);
            SplashScreen.vendor.setSdate(date);
            SplashScreen.vendor.setStime(timing);
            SplashScreen.vendor.setLat(lat);
            SplashScreen.vendor.setLng(lng);
            SplashScreen.vendor.setRat("0,0,0,0,0");

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("vendors");
            ref.child(SplashScreen.vendor.getUID()).setValue(SplashScreen.vendor);

            SplashScreen.vendor.setImage(img);

            saveDataToStorage();
            saveDataToSepDB();

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

        System.out.println("det lat = "+ det.getLat() + "det lng = " + det.getLng());

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