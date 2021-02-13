package com.bookspot.app.vendor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Requests extends AppCompatActivity {

    List<Container_Class.NewBooking> list;
    RecyclerView recyclerView;
    String r;
    String req[];
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        progressBar = findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) findViewById(R.id.req_rv);

        recyclerView.setVisibility(View.INVISIBLE);
        SharedPreferences sharedPreferences = Requests.this.getSharedPreferences("user", MODE_PRIVATE);
        SplashScreen.vendor.setUID(sharedPreferences.getString("UID", ""));
        System.out.println("inside requests uid = " + SplashScreen.vendor.getUID());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("vendors/"+ SplashScreen.vendor.getUID());
        ref.child("req").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list = new ArrayList<>();
                for(DataSnapshot ds : snapshot.getChildren()){
                    r = ds.getValue(String.class);
                    if(r != null && !r.isEmpty()){
                        Container_Class.NewBooking booking = convertStringToNB(r);
                        booking.setKey(ds.getKey());
                        list.add(booking);
                    }
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(Requests.this));
                recyclerView.setHasFixedSize(true);
                if(list != null) {
                    Adapter adapter = new Adapter(list);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }else
                    Toast.makeText(Requests.this, "Sorry! There is no New Booking", Toast.LENGTH_SHORT);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public class Adapter extends RecyclerView.Adapter<Adapter.ProductViewHolder> {

        //we are storing all the products in a list
        private List<Container_Class.NewBooking> list;


        public Adapter(List<Container_Class.NewBooking> list) {
            this.list = list;
        }

        @Override
        public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(Requests.this);
            View view = inflater.inflate(R.layout.req_card, parent , false);
            return new ProductViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ProductViewHolder holder, final int position) {
            //getting the product of the specified position
            final Container_Class.NewBooking newBooking = list.get(position);

            holder.date.setText("Date :- "+ newBooking.getbDate());
            holder.time.setText("Time Slot :- "+ newBooking.getbTime());

            holder.decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("inside decline uid = " +SplashScreen.vendor.getUID() + "newBooking key = " + newBooking.getKey());

                    DatabaseReference rq = FirebaseDatabase.getInstance().getReference("vendors/"+ SplashScreen.vendor.getUID());
                    rq.child("req").child(newBooking.getKey()).setValue(null);

                    DatabaseReference cust = FirebaseDatabase.getInstance().getReference("customers/" +newBooking.getUID());
                    cust.child("services").child(SplashScreen.vendor.getUID()).child("st").setValue(3);

                    if(list.size() == 1) {
                        startActivity(new Intent(Requests.this, MainActivity.class));
                        finish();
                    }else{
                        list.remove(position);
                        Adapter adapter = new Adapter(list);
                        recyclerView.setAdapter(adapter);
                    }
                }
            });

            holder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("inside decline uid = " +SplashScreen.vendor.getUID() + "newBooking key = " + newBooking.getKey());
                    int index ;
                    String time[] = newBooking.getbTime().split(" ");
                    if(time[3].equals("PM"))
                        index = 12;
                    else
                        index = 0;
                    index += Integer.parseInt(time[0]);


                    DatabaseReference referenceOfSch = FirebaseDatabase.getInstance().getReference("orders/" + SplashScreen.vendor.getUID());
                    referenceOfSch.child("sch").child(newBooking.getbDate()).child(String.valueOf(index)).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int token = 0;
                            if(snapshot.getValue(Integer.class) != null)
                                token = snapshot.getValue(Integer.class);
                            snapshot.getRef().setValue(token + 1);
                            newBooking.setTkn(token+1);

                            Container_Class.CustomerSideOrder order = new Container_Class.CustomerSideOrder(
                                    SplashScreen.vendor.getFname(),
                                    newBooking.getsType(),
                                    newBooking.getbDate(),
                                    newBooking.getbTime(),
                                    SplashScreen.vendor.getUID()
                            );

                            DatabaseReference nb = FirebaseDatabase.getInstance().getReference("orders/" + SplashScreen.vendor.getUID());
                            nb.child("order").child(newBooking.getUID()).setValue(newBooking);

                            DatabaseReference cust = FirebaseDatabase.getInstance().getReference("customers/" + newBooking.getUID());
                            cust.child("services").child(SplashScreen.vendor.getUID()).child("st").setValue(2);

                            DatabaseReference rq = FirebaseDatabase.getInstance().getReference("vendors/"+ SplashScreen.vendor.getUID());
                            rq.child("req").child(newBooking.getKey()).setValue(null);
                            rq.child("his").push().setValue(r);
                            if(list.size() == 1) {
                                startActivity(new Intent(Requests.this, MainActivity.class));
                                finish();
                            }else{
                                list.remove(position);
                                Adapter adapter = new Adapter(list);
                                recyclerView.setAdapter(adapter);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });

        }

        @Override
        public int getItemCount() {
            return list.size();
        }


        class ProductViewHolder extends RecyclerView.ViewHolder {

            TextView date, time;
            MaterialButton accept, decline;

            public ProductViewHolder(View itemView) {
                super(itemView);

                accept = itemView.findViewById(R.id.accept);
                decline = itemView.findViewById(R.id.decline);
                date = itemView.findViewById(R.id.date);
                time = itemView.findViewById(R.id.time);
            }
        }
    }


    private Container_Class.NewBooking convertStringToNB(String req) {
        String[] members = req.split(",");

        return new Container_Class.NewBooking(
                Integer.parseInt(members[7]),
                members[2],
                members[3],
                members[4],
                members[5],
                members[1],
                members[0],
                Boolean.parseBoolean(members[6])
        );
    }
}