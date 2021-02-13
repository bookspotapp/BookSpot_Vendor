package com.bookspot.app.vendor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.FirebaseApp;

import java.util.ArrayList;

public class BgReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        FirebaseApp.initializeApp(context);
        System.out.println("inside receiver uid = " + intent.getStringExtra("uid"));
        intent.setClass(context, BgService.class);
        BgService.enqueueWork(context, intent);
    }
}
