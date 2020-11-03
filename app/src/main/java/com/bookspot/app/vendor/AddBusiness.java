package com.bookspot.app.vendor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddBusiness extends AppCompatActivity {

    EditText firm_name, address, email, website, phone;
    TextView proceed;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_business);

        initialize();

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String f_name = firm_name.getText().toString();
                String add = address.getText().toString();
                String Email = email.getText().toString();
                String web = website.getText().toString();
                String cno = phone.getText().toString();

                if(f_name.isEmpty())
                    firm_name.setError("Enter your firm name ");
                if(add.isEmpty())
                    address.setError("Enter your firm address ");
                if(Email.isEmpty())
                    email.setError("Enter your email ");

                if(!f_name.isEmpty() &&  !add.isEmpty() && !Email.isEmpty()){
                    SharedPreferences sharedPreferences = AddBusiness.this.getSharedPreferences("user", MODE_PRIVATE);
                    sharedPreferences.edit().putString("fname", f_name).apply();
                    sharedPreferences.edit().putString("add", add).apply();
                    sharedPreferences.edit().putString("email", Email).apply();
                    sharedPreferences.edit().putString("website", web).apply();
                    sharedPreferences.edit().putString("addno", cno).apply();

                    SplashScreen.vendor.setFname(f_name);
                    SplashScreen.vendor.setAddno(cno);
                    SplashScreen.vendor.setEmail(Email);
                    SplashScreen.vendor.setAdd(add);
                    SplashScreen.vendor.setWebsite(web);

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("vendors");
                    ref.child(SplashScreen.vendor.getUID()).setValue(SplashScreen.vendor);

                    startActivity(new Intent(AddBusiness.this, Categories.class));
                }
            }
        });
    }

    private void initialize() {
        firm_name = (EditText) findViewById(R.id.firm_name);
        address = (EditText) findViewById(R.id.address);
        email = (EditText) findViewById(R.id.email);
        website = (EditText) findViewById(R.id.website);
        phone = (EditText) findViewById(R.id.phone);
        proceed = (TextView) findViewById(R.id.proceed);
        back = (ImageView) findViewById(R.id.back_arrow);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                AddBusiness.super.onBackPressed();
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