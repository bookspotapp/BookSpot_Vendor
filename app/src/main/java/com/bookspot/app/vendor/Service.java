package com.bookspot.app.vendor;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Service extends AppCompatActivity {

    TextView submit;
    ImageView back;

    ArrayList salon_services = new ArrayList<>(Arrays.asList("Hair care", "Skin care", "Make-up combos", "Hands & feet", "Spa & Massages", "Shower & shampoo", "Kids friendly"));
    ArrayList restraunt_services = new ArrayList<>(Arrays.asList("Breakfast/lunch/dinner", "Family dine-ins", "Take Away", "Indoor", "Rooftop", "Bar/lounge", "Self- service", "Happy hours", "Kids Friendly"));
    ArrayList gym_services = new ArrayList<>(Arrays.asList("Equipments", "Yoga", "Zumba", "Aerobics", "Dance"));
    ArrayList bank_services = new ArrayList<>(Arrays.asList("Talk to your executive", "Open account", "Transactions", "Deposits", "Other"));
    ArrayList retail_services = new ArrayList<>(Arrays.asList("Shop groceries", "Clothes", "General store"));
    ArrayList diagnostic_centres = new ArrayList<>(Arrays.asList("Consultation", "Blood Test", "Urine Test", "Sugar Test", "Full Body Checkup", "X-Ray", "CT Scan", "MRI", "ECG"));
    ArrayList service_centres = new ArrayList<>(Arrays.asList("Device Repair", "Vehicle Repair", "Car Wash"));
    ArrayList clinics = new ArrayList<>(Arrays.asList("General Physician", "Paediatrician", "Bed Available", "Wound Suturing"));
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

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.services_rv);
        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(getApplicationContext());
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL); // set Horizontal Orientation
        recyclerView.setLayoutManager(gridLayoutManager);
        Bundle bundle = getIntent().getExtras();

        String category = (String) bundle.get("category");
        switch(category){
            case "Salons":
                list = salon_services;
                break;

            case "Gyms":
                list = gym_services;
                break;

            case "Restraunts":
                list = restraunt_services;
                break;

            case "Clinics":
                list = clinics;
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
        }

        Adapter adapter = new Adapter(list);
        recyclerView.setAdapter(adapter);
    }

    public class Adapter extends RecyclerView.Adapter<Adapter.ProductViewHolder> {

        private String rv;
        private List<String> list;


        public Adapter(List<String> list) {
            this.list = list;
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
        public void onBindViewHolder(final ProductViewHolder holder, int position) {
            //getting the product of the specified position
            final String product = list.get(position);

            holder.right.setVisibility(View.INVISIBLE);
            holder.name.setText(product);
            holder.select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    services += product + ";";
                    holder.select.setVisibility(View.INVISIBLE);
                    holder.cardView.setVisibility(View.INVISIBLE);
                    holder.right.setVisibility(View.VISIBLE);
                }
            });
        }


        @Override
        public int getItemCount() {
            return list.size();
        }


         class ProductViewHolder extends RecyclerView.ViewHolder {

            TextView name , select ;
            ImageView right;
            CardView cardView;


            public ProductViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.name);
                select = itemView.findViewById(R.id.select_text);
                right = itemView.findViewById(R.id.right);
                cardView = itemView.findViewById(R.id.select_cv);
            }
        }
    }

}