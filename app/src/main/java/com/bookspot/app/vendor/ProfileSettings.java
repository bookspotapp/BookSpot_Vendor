package com.bookspot.app.vendor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;

public class ProfileSettings extends AppCompatActivity implements View.OnClickListener {

    ImageView back, img;
    EditText fname, cno, email, add;
    TextView save, add_img;
    String image;
    int GET_IMAGE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        createNotificationChannel();
        initializeit();

        back = (ImageView) findViewById(R.id.back_arrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initializeit() {
        back = (ImageView) findViewById(R.id.back_arrow);
        img = (ImageView) findViewById(R.id.camera);
        fname = (EditText) findViewById(R.id.firm_name);
        email = (EditText) findViewById(R.id.email);
        cno = (EditText) findViewById(R.id.contact);
        add = (EditText) findViewById(R.id.address);
        save = (TextView) findViewById(R.id.save);
        add_img = (TextView) findViewById(R.id.add_img);

        add_img.setOnClickListener(this);
        save.setOnClickListener(this);

        img.setImageBitmap(decodeFromFirebaseBase64(SplashScreen.vendor.getImage()));
        fname.setText(SplashScreen.vendor.getFname());
        email.setText(SplashScreen.vendor.getEmail());
        cno.setText(SplashScreen.vendor.getCno());
        add.setText(SplashScreen.vendor.getAdd());
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.add_img:
                checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, GET_IMAGE);
                break;

            case R.id.save:
                String Email = email.getText().toString();
                String Cno = cno.getText().toString();
                String Fname = fname.getText().toString();
                String Add = add.getText().toString();

                if(Email.isEmpty())
                    email.setError("Enter your Email");
                if(Cno.isEmpty())
                    cno.setError("Enter your Contact No.");
                if(Fname.isEmpty())
                    fname.setError("Enter your Firm Name");
                if(Add.isEmpty())
                    add.setError("Enter your Firm Address");
                if(image.isEmpty())
                    Toast.makeText(ProfileSettings.this, "Please select an image or\n logo of your Firm", Toast.LENGTH_SHORT).show();

                if(!Email.isEmpty() && !Cno.isEmpty() && !Fname.isEmpty() && !Add.isEmpty() && image.isEmpty()){
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("vendors/" + SplashScreen.vendor.getUID());
                    ref.child("email").setValue(Email);
                    ref.child("cno").setValue(Cno);
                    ref.child("fname").setValue(Fname);
                    ref.child("img").setValue(image);
                    ref.child("add").setValue(Add);

                    Toast.makeText(ProfileSettings.this, "Data Saved", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    public void checkPermission(String permission, int requestCode) {

        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(ProfileSettings.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(ProfileSettings.this, new String[]{permission}, requestCode);
        } else {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), GET_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            // Checking whether user granted the permission or not.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(ProfileSettings.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), GET_IMAGE);
            } else {
                Toast.makeText(ProfileSettings.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
                finish();
            }
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
            img.setImageBitmap(bmp);
            image = changeBitmapToStrinng(bmp);
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                ProfileSettings.this.getContentResolver().openFileDescriptor(uri, "r");
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

    public static Bitmap decodeFromFirebaseBase64(String image){
        byte[] decodedByteArray = Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
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