package com.bookspot.app.vendor;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class Verification extends AppCompatActivity {

    EditText otp;
    MaterialButton verify, resend;
    ImageView back;
    String OTP;
    private String verificationid;
    private FirebaseAuth mAuth;
    ProgressBar progressBar;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        //createNotificationChannel();
        back = (ImageView) findViewById(R.id.back_arrow);
        otp = (EditText) findViewById(R.id.otp);
        verify = (MaterialButton) findViewById(R.id.verify);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        resend = (MaterialButton) findViewById(R.id.resend);
        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationCode(Register.c_no);
            }
        });
        resend.setVisibility(View.INVISIBLE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                resend.setVisibility(View.VISIBLE);
            }
        }, 10000);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mCallBack= new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                System.out.println("\n inside mcallback code sent");
                verificationid = s;
            }

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                String code = phoneAuthCredential.getSmsCode();
                if (code != null){
                    System.out.println("\n inside mcallback onverification completed");
                }
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(Verification.this, e.getMessage(), Toast.LENGTH_LONG).show();
                System.out.println("\n inside mcallback verification failed");
            }
        };

        sendVerificationCode(Register.c_no);

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verify.setVisibility(View.INVISIBLE);
                OTP = otp.getText().toString();

                resend.setVisibility(View.INVISIBLE);

                if(OTP.isEmpty() && OTP.length() != 6)
                    otp.setError("Enter the correct OTP");
                else {
                    verifyCode(OTP);
                }
            }
        });
    }

    private void sendVerificationCode(String number){
        System.out.println("\n send verification code");
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                Verification.this,
                mCallBack
        );
    }

    private void verifyCode(String code){
        System.out.println("\n verify code");
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationid, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        System.out.println("\n signin with credentials");
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            final SharedPreferences sharedPreferences = Verification.this.getSharedPreferences("user", MODE_PRIVATE);
                            sharedPreferences.edit().putBoolean("logged", true).apply();
                            sharedPreferences.edit().putString("cno", Register.c_no).apply();
                            sharedPreferences.edit().putString("oname", Register.o_name).apply();
                            sharedPreferences.edit().putString("UID", mAuth.getCurrentUser().getUid()).apply();
                            sharedPreferences.edit().putBoolean("sound", true).apply();
                            sharedPreferences.edit().putBoolean("vibrate", true).apply();
                            sharedPreferences.edit().putBoolean("next", true).apply();

                            System.out.println("\n inside verification UID = "+ mAuth.getCurrentUser().getUid());

                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("vendors");
                            ref.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Container_Class.Vendor vendor = snapshot.getValue(Container_Class.Vendor.class);
                                    if(vendor != null && vendor.getServices() == null){
                                        SplashScreen.vendor = vendor;
                                        sharedPreferences.edit().putString("fname", vendor.getFname()).apply();
                                        sharedPreferences.edit().putString("add", vendor.getAdd()).apply();
                                        sharedPreferences.edit().putString("email", vendor.getEmail()).apply();
                                        sharedPreferences.edit().putString("website", vendor.getWebsite()).apply();
                                        sharedPreferences.edit().putString("addno", vendor.getAddno()).apply();
                                        Intent intent = new Intent(Verification.this, Categories.class);
                                        intent.putExtra("activity", "verification");
                                        startActivity(intent);
                                        finish();
                                    } else if(vendor != null && vendor.getServices() != null && vendor.getSdate() == null ) {
                                        SplashScreen.vendor = vendor;
                                        sharedPreferences.edit().putString("fname", vendor.getFname()).apply();
                                        sharedPreferences.edit().putString("add", vendor.getAdd()).apply();
                                        sharedPreferences.edit().putString("email", vendor.getEmail()).apply();
                                        sharedPreferences.edit().putString("website", vendor.getWebsite()).apply();
                                        sharedPreferences.edit().putString("addno", vendor.getAddno()).apply();
                                        sharedPreferences.edit().putString("cat", vendor.getCat()).apply();
                                        sharedPreferences.edit().putString("services", vendor.getServices()).apply();
                                        Intent intent = new Intent(Verification.this, FirstTime.class);
                                        intent.putExtra("activity", "verification");
                                        startActivity(intent);
                                        finish();
                                    } else if(vendor != null && vendor.getSdate() != null){
                                        SplashScreen.vendor = vendor;
                                        sharedPreferences.edit().putString("fname", vendor.getFname()).apply();
                                        sharedPreferences.edit().putString("add", vendor.getAdd()).apply();
                                        sharedPreferences.edit().putString("email", vendor.getEmail()).apply();
                                        sharedPreferences.edit().putString("website", vendor.getWebsite()).apply();
                                        sharedPreferences.edit().putString("addno", vendor.getAddno()).apply();
                                        sharedPreferences.edit().putString("cat", vendor.getCat()).apply();
                                        sharedPreferences.edit().putString("services", vendor.getServices()).apply();
                                        sharedPreferences.edit().putString("total_tokens", vendor.getTotal_tokens()).apply();
                                        sharedPreferences.edit().putString("sdate", vendor.getSdate()).apply();
                                        sharedPreferences.edit().putString("stime", vendor.getStime()).apply();
                                        sharedPreferences.edit().putString("ltiming", vendor.getLtiming()).apply();
                                        sharedPreferences.edit().putString("lat", String.valueOf(vendor.getLat())).apply();
                                        sharedPreferences.edit().putString("lng", String.valueOf(vendor.getLng())).apply();
                                        sharedPreferences.edit().putString("op", vendor.getOp()).apply();
                                        sharedPreferences.edit().putString("rat", "0,0,0,0,0").apply();

                                        setImageStrind();

                                        Intent intent = new Intent(Verification.this, MainActivity.class);
                                        intent.putExtra("activity", "verification");
                                        startActivity(intent);
                                        finish();
                                    }else{
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("vendors");
                                        SplashScreen.vendor.setLat(0.0);
                                        SplashScreen.vendor.setLng(0.0);
                                        reference.child(mAuth.getCurrentUser().getUid()).setValue(SplashScreen.vendor);
                                        SplashScreen.vendor = new Container_Class.Vendor(
                                                Register.c_no,
                                                Register.o_name,
                                                mAuth.getCurrentUser().getUid()
                                        );
                                        startActivity(new Intent(Verification.this, AddBusiness.class));
                                        finish();
                                    }

                                        System.out.println("\n inside verification" +
                                            "\n oname = "+ sharedPreferences.getString("oname", "") +
                                            "\n UID = " +sharedPreferences.getString("UID", "") +
                                            "\n fname = " +sharedPreferences.getString("fname", "") +
                                            "\n cno = " +sharedPreferences.getString("cno", "") +
                                            "\n add = " +sharedPreferences.getString("add", "") +
                                            "\n addno = " +sharedPreferences.getString("addno", "") +
                                            "\n email = " +sharedPreferences.getString("email", "") +
                                            "\n website = " +sharedPreferences.getString("website", "") +
                                            "\n cat = " +sharedPreferences.getString("cat", "")+
                                            "\n services = " +sharedPreferences.getString("services", "")+
                                            "\n total_tokens = " +sharedPreferences.getString("total_tokens", "")+
                                            "\n sdate = " +sharedPreferences.getString("sdate", "")+
                                            "\n stime = " +sharedPreferences.getString("stime", "")
                                    );

                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        } else {
                            Toast.makeText(Verification.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            verify.setVisibility(View.VISIBLE);
                        }
                    }

                    private void setImageStrind() {
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                        StorageReference photoReference= storageReference.child("vendors/" + SplashScreen.vendor.getUID() + "/logo.jpg");

                        final Bitmap[] bmp = new Bitmap[1];
                        final long ONE_MEGABYTE = 1024 * 1024;
                        photoReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                bmp[0] = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                SharedPreferences sp = Verification.this.getSharedPreferences("user", MODE_PRIVATE);
                                sp.edit().putString("img", changeBitmapToStrinng(bmp[0]));
                                SplashScreen.vendor.setImage(changeBitmapToStrinng(bmp[0]));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(getApplicationContext(), "No Such file or Path found!!", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    private String changeBitmapToStrinng(Bitmap bitmap) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
                        return imageEncoded;
                    }

                });
    }
/*
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Booking Alert";
            String description = "New Booking";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(SplashScreen.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setVibrationPattern(new long[] { 1000, 1500, 1000, 1500, 1000 });
            channel.enableVibration(true);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


 */
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Verification.super.onBackPressed();
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