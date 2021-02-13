package com.bookspot.app.vendor;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookingOff#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookingOff extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static SwitchCompat switch_on;
    public static TextView tv9, tv10;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BookingOff() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookingOff.
     */
    // TODO: Rename and change types and number of parameters
    public static BookingOff newInstance(String param1, String param2) {
        BookingOff fragment = new BookingOff();
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
        final View view = inflater.inflate(R.layout.fragment_booking_off, container, false);

        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        switch_on = (SwitchCompat) view.findViewById(R.id.turn_on_services);
        tv9 = (TextView) view.findViewById(R.id.textView9);
        tv10 = (TextView) view.findViewById(R.id.textView10);

        if(sharedPreferences.getBoolean("booking", false)){
            switch_on.setChecked(true);
            tv9.setVisibility(View.INVISIBLE);
            tv10.setVisibility(View.INVISIBLE);
            switch_on.setVisibility(View.INVISIBLE);
            Fragment fragment = new BookingOn();
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_up,  // enter
                            R.anim.slide_down
                    )
                    .replace(R.id.booking, fragment)
                    .addToBackStack("off")
                    .commit();
        }

        switch_on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sharedPreferences.edit().putBoolean("booking", true).apply();

                    DatabaseReference statusRef = FirebaseDatabase.getInstance()
                            .getReference("det/vendors/"+ SplashScreen.vendor.getCat()
                                    + "/" + SplashScreen.vendor.getUID());
                    statusRef.child("status").setValue(1);

                    Fragment fragment = new BookingOn();
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
        return view;
    }
}