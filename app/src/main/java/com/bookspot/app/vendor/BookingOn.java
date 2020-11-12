package com.bookspot.app.vendor;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
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

import java.util.ArrayList;
import java.util.List;

import static androidx.recyclerview.widget.RecyclerView.HORIZONTAL;
import static com.bookspot.app.vendor.SplashScreen.CHANNEL_ID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookingOn#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookingOn extends Fragment {

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
    public static String rq;
    boolean s, v, n;


    public BookingOn() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookingOn.
     */
    // TODO: Rename and change types and number of parameters
    public static BookingOn newInstance(String param1, String param2) {
        BookingOn fragment = new BookingOn();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_booking_on, container, false);

        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        s = sharedPreferences.getBoolean("sound", false);
        v = sharedPreferences.getBoolean("vibrate", false);
        n = sharedPreferences.getBoolean("next", false);

        Intent intent = new Intent(getContext(), Requests.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("New Booking Alert!")
                .setContentText("You have received a new booking....")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if(!s)
            builder.setNotificationSilent();
        if(v)
            builder.setVibrate(new long[] { 1000, 500, 1000, 500, 1000 });



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

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("orders/"+ SplashScreen.vendor.getUID() );
                    ref.child("order").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                getActivity().onBackPressed();
                                BookingOff.tv9.setVisibility(View.VISIBLE);
                                BookingOff.tv10.setVisibility(View.VISIBLE);
                                BookingOff.switch_on.setVisibility(View.VISIBLE);
                                BookingOff.switch_on.setChecked(false);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });


        //getting dat aof reqs and launching requests.class
        final DatabaseReference req = FirebaseDatabase.getInstance().getReference("vendors/"+ SplashScreen.vendor.getUID());
        req.child("req").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot != null) {
                    String reqs = snapshot.getValue(String.class);
                    if(reqs != null && reqs.length() > 6) {
                        if(n) {
                            notificationManager = NotificationManagerCompat.from(getActivity());
                            int notificationId = 1;
                            notificationManager.notify(notificationId, builder.build());
                        }
                         rq = reqs;
                        Intent intent = new Intent(getActivity(), Requests.class);
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
                        list.add(ds.getValue(Container_Class.NewBooking.class));
                    }
                    if(list.size() > 0) {
                        recyclerView.setVisibility(View.VISIBLE);
                        tv1.setVisibility(View.INVISIBLE);
                        cv1.setVisibility(View.INVISIBLE);
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

            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customers/" + newBooking.getUID());
            final DatabaseReference order = FirebaseDatabase.getInstance().getReference("orders/" + SplashScreen.vendor.getUID()+ "/order");
            holder.tkn.setText("Token No. :- " + newBooking.getTkn());
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
                    list.remove(position);
                    ref.child(newBooking.getsType()).setValue("done");
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
                    list.remove(position);
                    ref.child(newBooking.getsType()).setValue("declined");
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

            TextView tkn, cname, cno;
            MaterialButton done, cancel, next;

            public ProductViewHolder(View itemView) {
                super(itemView);

                tkn = itemView.findViewById(R.id.token_no);
                cname = itemView.findViewById(R.id.customer_name);
                cno = itemView.findViewById(R.id.contact_no);
                done = itemView.findViewById(R.id.done);
                cancel = itemView.findViewById(R.id.cancel);
                next = itemView.findViewById(R.id.next);
            }
        }
    }



}