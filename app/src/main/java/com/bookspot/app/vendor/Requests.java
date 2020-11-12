package com.bookspot.app.vendor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
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
    static int tkn_no ;
    String reqs;
    String req[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        if(BookingOn.notificationManager != null)
            BookingOn.notificationManager.cancel(1);

        reqs = BookingOn.rq;
        req = reqs.split(";");

        recyclerView = (RecyclerView) findViewById(R.id.req_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(Requests.this));
        recyclerView.setHasFixedSize(true);

        for(String r : req) {
            Container_Class.NewBooking newBooking = convertStringToNB(r);
            list.add(newBooking);
        }

        Adapter adapter = new Adapter(list);
        recyclerView.setAdapter(adapter);

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

            holder.tkn.setText("Token No. "+ newBooking.getTkn());

            holder.decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reqs.replace(req[position] + ";", " ");
                    DatabaseReference rq = FirebaseDatabase.getInstance().getReference("vendors/"+ SplashScreen.vendor.getUID());
                    rq.child("req").setValue(null);

                    DatabaseReference cust = FirebaseDatabase.getInstance().getReference("customers/" +newBooking.getUID());
                    cust.child("services").child(SplashScreen.vendor.getUID()).child("st").setValue(3);
                    finish();
                }
            });

            holder.accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DatabaseReference nb = FirebaseDatabase.getInstance().getReference("orders/" + SplashScreen.vendor.getUID());
                    nb.child("order").child(newBooking.getUID()).setValue(newBooking);
                    nb.child("tkn").setValue(newBooking.getTkn());

                    Container_Class.CustomerSideOrder order = new Container_Class.CustomerSideOrder(
                            SplashScreen.vendor.getFname(),
                            newBooking.getsType(),
                            newBooking.getbDate(),
                            newBooking.getbTime(),
                            SplashScreen.vendor.getUID()
                    );

                    DatabaseReference cust = FirebaseDatabase.getInstance().getReference("customers/" + newBooking.getUID());
                    cust.child("services").child(SplashScreen.vendor.getUID()).setValue(order);
                    cust.child("services").child(SplashScreen.vendor.getUID()).child("st").setValue(2);

                    DatabaseReference rq = FirebaseDatabase.getInstance().getReference("vendors/"+ SplashScreen.vendor.getUID());
                    rq.child("req").setValue(null);
                    finish();
                }
            });

        }

        @Override
        public int getItemCount() {
            return list.size();
        }


        class ProductViewHolder extends RecyclerView.ViewHolder {

            TextView tkn;
            MaterialButton accept, decline;

            public ProductViewHolder(View itemView) {
                super(itemView);

                accept = itemView.findViewById(R.id.accept);
                decline = itemView.findViewById(R.id.decline);
                tkn = itemView.findViewById(R.id.token_no);
            }
        }
    }


    private Container_Class.NewBooking convertStringToNB(String req) {
        String[] members = req.split(",");

        return new Container_Class.NewBooking(
                Integer.parseInt(members[7]),
                Integer.parseInt(members[8]),
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