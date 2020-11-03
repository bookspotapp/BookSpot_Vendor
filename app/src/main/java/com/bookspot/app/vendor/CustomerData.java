package com.bookspot.app.vendor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CustomerData extends AppCompatActivity {

    RecyclerView customer_data;
    ImageView back;
    TextView time;
    List<Container_Class.NewBooking> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_data);

        back = (ImageView) findViewById(R.id.back_arrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        time = (TextView) findViewById(R.id.time);


        customer_data = (RecyclerView) findViewById(R.id.customer_data);
        customer_data.setLayoutManager(new LinearLayoutManager(CustomerData.this));
        customer_data.setHasFixedSize(true);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("orders/" + SplashScreen.vendor.getUID() + "/order");
        Query query = ref.orderByChild("tkn");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list = new ArrayList<>();
                for(DataSnapshot ds : snapshot.getChildren()){
                    list.add(ds.getValue(Container_Class.NewBooking.class));
                }
                Adapter adapter = new Adapter(list);
                customer_data.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public class Adapter extends RecyclerView.Adapter<CustomerData.Adapter.ProductViewHolder> {

        //we are storing all the products in a list
        private List<Container_Class.NewBooking> list;


        public Adapter(List<Container_Class.NewBooking> list) {
            this.list = list;
        }

        @Override
        public CustomerData.Adapter.ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(CustomerData.this);
            View view = inflater.inflate(R.layout.customer_data_card, parent , false);
            return new CustomerData.Adapter.ProductViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CustomerData.Adapter.ProductViewHolder holder, final int position) {
            //getting the product of the specified position
            final Container_Class.NewBooking newBooking = list.get(position);

            holder.tkn.setText(  String.valueOf(newBooking.getTkn()));
            holder.type.setText( newBooking.getsType());
            holder.time.setText( newBooking.getbTime());
            holder.sbk.setText( String.valueOf(newBooking.getSbk() ));
        }

        @Override
        public int getItemCount() {
            return list.size();
        }


        class ProductViewHolder extends RecyclerView.ViewHolder {

            TextView tkn, type, time, sbk;

            public ProductViewHolder(View itemView) {
                super(itemView);

                tkn = itemView.findViewById(R.id.tkn_no);
                type = itemView.findViewById(R.id.service_type);
                time = itemView.findViewById(R.id.booking_time);
                sbk = itemView.findViewById(R.id.seats_booked);
            }
        }
    }

}