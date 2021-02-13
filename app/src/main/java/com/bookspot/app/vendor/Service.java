package com.bookspot.app.vendor;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Service extends AppCompatActivity {

    TextView submit;
    ImageView back;
    RecyclerView recyclerView;

    ArrayList salon_services = new ArrayList<>(Arrays.asList("Hair Cut", "Hair Styling", "Hair Wash", "Hair Colour", "Beard", "Hair Cut + Beard", "Facial & Clean-up", "Bleach", "Eyebrow", "Waxing", "Threading",
                                                            "Manicuure/Pedicure", "Nail Treatment", "Nail art", "Nail Extensions", "Head Massage", "Body Massage"));

    ArrayList restraunt_services = new ArrayList<>(Arrays.asList("Breakfast/lunch/dinner", "Family dine-ins", "Take Away", "Indoor", "Rooftop", "Bar/lounge", "Self-service", "Happy hours", "Kids Friendly", "Chinese",
                                                            "Italian", "Continental", "Indian", "Thai", "Mughlai", "Mexican"));

    ArrayList gym_services = new ArrayList<>(Arrays.asList("Cricket", "Football", "Other"));

    ArrayList bank_services = new ArrayList<>(Arrays.asList("Talk to your executive", "Open account", "Transactions", "Deposits", "Other"));

    ArrayList retail_services = new ArrayList<>(Arrays.asList("Shop groceries", "Clothes", "General store"));

    ArrayList diagnostic_centres = new ArrayList<>(Arrays.asList("Blood Test", "Urine Test", "Sugar Test", "X-Ray", "CT Scan", "MRI Scan", "HIV", "Sonography", "Full Body Checkup"));

    ArrayList service_centres = new ArrayList<>(Arrays.asList("Device Repair", "Vehicle Repair", "Car Wash"));

    ArrayList govt_offices = new ArrayList<>(Arrays.asList("Billing", "Registration", "Complaint"));

    ArrayList doctors = new ArrayList<>(Arrays.asList( "Family Physician", "Vet", "Orthopaedic", "Dentist", "Gynaecologist", "Darmetologist", "Cardiologist", "ENT", "Eye-Specialist", "Dietitian & Nutritionalist",
                                                    "Neurologist", "Psychatrist", "Physiotherapist", "gastroenterologist", "Allergists/Immunologist"));
    ArrayList<String> list;
    String services = new String();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        back = (ImageView) findViewById(R.id.back_arrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        submit = (TextView) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cat = getIntent().getStringExtra("category");
                SharedPreferences sharedPreferences = Service.this.getSharedPreferences("user", MODE_PRIVATE);
                if(services.isEmpty())
                    Toast.makeText(Service.this, "\n Please select atleast one servie", Toast.LENGTH_SHORT).show();
                else{
                    sharedPreferences.edit().putString("cat", cat).apply();
                    sharedPreferences.edit().putString("services", services).apply();

                    SplashScreen.vendor.setCat(cat);
                    SplashScreen.vendor.setServices(services);

                    //Adding data to the firebase
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("vendors");
                    ref.child(SplashScreen.vendor.getUID()).setValue(SplashScreen.vendor);

                    startActivity(new Intent(Service.this, Confirmation.class));
                    finishAffinity();
                }
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.services_rv);
        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(getApplicationContext());
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL); // set Horizontal Orientation
        recyclerView.setLayoutManager(gridLayoutManager);
        Bundle bundle = getIntent().getExtras();

        String category = (String) bundle.get("category");
        switch(category){
            case "Salons":
                list = salon_services;
                break;

            case "Gyms & Turfs":
                list = gym_services;
                break;

            case "Restaurants":
                list = restraunt_services;
                break;

            case "Doctors":
                list = doctors;
                break;

            case "Diagnostic Centres":
                list = diagnostic_centres;
                break;

            case  "Service Centres":
                list = service_centres;
                break;

            case  "Retails":
                list = retail_services;
                break;

            case  "Banks":
                list = bank_services;
                break;

            case  "Government Offices":
                list = govt_offices;
                break;
        }

        Adapter adapter = new Adapter(list, services);
        recyclerView.setAdapter(adapter);
    }

    public class Adapter extends RecyclerView.Adapter<Adapter.ProductViewHolder> {

        private String service;
        private List<String> list;


        public Adapter(List<String> list, String service) {
            this.list = list;
            this.service = service;
        }

        @Override
        public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //inflating and returning our view holder
            LayoutInflater inflater = LayoutInflater.from(Service.this);
            View view;
            view = inflater.inflate(R.layout.services_card, parent , false);
            return new ProductViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ProductViewHolder holder, final int position) {
            //getting the product of the specified position
            final String product = list.get(position);

            holder.name.setText(product);
            if(service.contains(product)){
                holder.right.setVisibility(View.VISIBLE);
                holder.select.setVisibility(View.INVISIBLE);
            }else{
                holder.select.setVisibility(View.VISIBLE);
                holder.right.setVisibility(View.INVISIBLE);
            }
            holder.select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    services += product + ";";
                    System.out.println("child text = "+ product + " services = " + services);
                    Adapter adapter1 = new Adapter(list, services);
                    recyclerView.setAdapter(adapter1);
                    recyclerView.smoothScrollToPosition(position);
                }
            });
        }


        @Override
        public int getItemCount() {
            return list.size();
        }


         class ProductViewHolder extends RecyclerView.ViewHolder {

            TextView name ;
            MaterialButton select ;
            ImageView right;


            public ProductViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.name);
                select = itemView.findViewById(R.id.select_text);
                right = itemView.findViewById(R.id.right);
            }
        }
    }

}