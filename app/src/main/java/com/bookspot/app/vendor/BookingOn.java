package com.bookspot.app.vendor;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.bookspot.app.vendor.Container_Class.*;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static android.view.View.inflate;
import static androidx.recyclerview.widget.RecyclerView.HORIZONTAL;
import static com.bookspot.app.vendor.SplashScreen.CHANNEL_ID;

public class BookingOn extends Fragment implements LifecycleObserver {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    SwitchCompat switch_off;
    List<Container_Class.NewBooking> list;
    View view;
    RecyclerView recyclerView;
    TextView tv1;
    CardView cv1;
    LinearLayoutManager layoutManager;
    public  static NotificationManagerCompat notificationManager;

    public BookingOn() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_booking_on, container, false);

        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);


        /*
        s = sharedPreferences.getBoolean("sound", false);
        v = sharedPreferences.getBoolean("vibrate", false);
        n = sharedPreferences.getBoolean("next", false);

        System.out.print("v = " + String.valueOf(v));
        System.out.print("s = " + String.valueOf(s));
        System.out.print("n = " + String.valueOf(n));

        Intent intent = new Intent(getContext(), Requests.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);


        final NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity().getApplicationContext(), CHANNEL_ID)
                .setWhen(System.currentTimeMillis());

        builder.setSmallIcon(R.drawable.logo)
                .setContentTitle("New Booking Alert!")
                .setContentText("You have received a new booking....")
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if(!s){
            builder.setNotificationSilent();
        }

        if(v) {
            System.out.println("v is true");
            builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        }
        */


        // switch on to off
        switch_off = view.findViewById(R.id.turn_off_services);
        switch_off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked) {
                    sharedPreferences.edit().putBoolean("booking", false).apply();

                    DatabaseReference statusRef = FirebaseDatabase.getInstance()
                            .getReference("det/vendors/"+ SplashScreen.vendor.getCat()
                                    + "/" + SplashScreen.vendor.getUID());
                    statusRef.child("status").setValue(0);

                    Fragment fragment = new BookingOff();
                    getFragmentManager().beginTransaction()
                            .setCustomAnimations(
                                    R.anim.slide_up,  // enter
                                    R.anim.slide_down
                            )
                            .replace(R.id.booking, fragment)
                            .commit();
                }
            }
        });


        //getting dat aof reqs and launching requests.class
        final DatabaseReference req = FirebaseDatabase.getInstance().getReference("vendors/"+ SplashScreen.vendor.getUID());
        req.child("req").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot != null) {
                    if(snapshot.hasChildren()) { // checking whether req branch has children or not
                        Intent intent = new Intent(getActivity(), Requests.class);
                        intent.putExtra("uid", SplashScreen.vendor.getUID());
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        recyclerView = (RecyclerView) view.findViewById(R.id.orders_rv);
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setVisibility(View.INVISIBLE);

        cv1 = (CardView) view.findViewById(R.id.cv1);
        tv1 = (TextView) view.findViewById(R.id.tv1);

        DatabaseReference orders = FirebaseDatabase.getInstance().getReference("orders/" + SplashScreen.vendor.getUID() +"/order");
        Query query = orders.orderByChild("tkn");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot != null) {
                    list = new ArrayList<>();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        NewBooking newBooking = ds.getValue(Container_Class.NewBooking.class);

                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        String formattedDate = df.format(Calendar.getInstance().getTime());

                        if(newBooking.getbDate().equals(formattedDate))
                            list.add(newBooking);
                    }
                    if(list.size() > 0) {
                        recyclerView.setVisibility(View.VISIBLE);
                        tv1.setVisibility(View.INVISIBLE);
                        cv1.setVisibility(View.INVISIBLE);
                        Collections.sort(list, new SortNewBookingbytime());
                        Adapter adapter = new Adapter(list);
                        recyclerView.setAdapter(adapter);
                    }
                }else{
                    recyclerView.setVisibility(View.INVISIBLE);
                    tv1.setVisibility(View.VISIBLE);
                    cv1.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    public class Adapter extends RecyclerView.Adapter<BookingOn.Adapter.ProductViewHolder> {

        //we are storing all the products in a list
        private List<Container_Class.NewBooking> list;


        public Adapter(List<Container_Class.NewBooking> list) {
            this.list = list;
        }

        @Override
        public BookingOn.Adapter.ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.orders_card, parent , false);
            return new BookingOn.Adapter.ProductViewHolder(view);
        }

        @Override
        public void onBindViewHolder(BookingOn.Adapter.ProductViewHolder holder, final int position) {
            //getting the product of the specified position
            final Container_Class.NewBooking newBooking = list.get(position);

            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customers/" + newBooking.getUID() +"/services");
            final DatabaseReference order = FirebaseDatabase.getInstance().getReference("orders/" + SplashScreen.vendor.getUID()+ "/order");
            final DatabaseReference tkn = FirebaseDatabase.getInstance().getReference("orders/" + SplashScreen.vendor.getUID());

            String tkn_value = newBooking.getbTime().split(" ")[0] + newBooking.getTkn();
            System.out.println("tkn_value = " + tkn_value);

            holder.tkn.setText("Token No. :- " + tkn_value);
            holder.cdt.setText("Time Slot :- "+ newBooking.getbTime());
            holder.cname.setText("Customer Name :- " + newBooking.getName());
            holder.cno.setText("Customer No. :- "+ newBooking.getCno());
            holder.next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerView.getLayoutManager().scrollToPosition(layoutManager.findLastVisibleItemPosition() + 1);
                }
            });
            holder.done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index ;
                    String s[] = newBooking.getbTime().split(" ");
                    if(s[3].equals("PM"))
                        index = 12;
                    else
                        index = 0;
                    index += Integer.parseInt(s[0]);

                    String tkn_value = index + "-" + newBooking.getTkn();
                    list.remove(position);
                    ref.child(SplashScreen.vendor.getUID()).child("st").setValue(4);
                    tkn.child("tkn_d").setValue(tkn_value);
                    order.child(newBooking.getUID()).setValue(null);
                    recyclerView.setAdapter(new Adapter(list));
                    if(list.size() == 1) {
                        recyclerView.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
                        tv1.setVisibility(View.VISIBLE);
                        cv1.setVisibility(View.VISIBLE);
                    }
                }
            });
            holder.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index ;
                    String s[] = newBooking.getbTime().split(" ");
                    if(s[3].equals("PM"))
                        index = 12;
                    else
                        index = 0;
                    index += Integer.parseInt(s[0]);

                    String tkn_value = index + "-" + newBooking.getTkn();
                    list.remove(position);
                    ref.child(SplashScreen.vendor.getUID()).child("st").setValue(3);
                    tkn.child("tkn_d").setValue(tkn_value);
                    order.child(newBooking.getUID()).setValue(null);
                    recyclerView.setAdapter(new Adapter(list));
                    if(list.size() == 1) {
                        recyclerView.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
                        tv1.setVisibility(View.VISIBLE);
                        cv1.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }


        class ProductViewHolder extends RecyclerView.ViewHolder {

            TextView tkn, cname, cno, cdt;
            MaterialButton done, cancel, next;

            public ProductViewHolder(View itemView) {
                super(itemView);

                tkn = itemView.findViewById(R.id.token_no);
                cdt = itemView.findViewById(R.id.token_date);
                cname = itemView.findViewById(R.id.customer_name);
                cno = itemView.findViewById(R.id.contact_no);
                done = itemView.findViewById(R.id.done);
                cancel = itemView.findViewById(R.id.cancel);
                next = itemView.findViewById(R.id.next);
            }
        }
    }



}