package com.bookspot.app.vendor;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import static com.bookspot.app.vendor.SplashScreen.CHANNEL_ID;

public class BgService extends JobIntentService implements LifecycleObserver {
    private static NotificationManagerCompat notificationManager;
    private static Context ctx;
    private static String vendorUID;


    public static void enqueueWork(final Context context, Intent i) {
        final boolean s, v, n;
        ctx = context;
        final SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        s = sharedPreferences.getBoolean("sound", false);
        v = sharedPreferences.getBoolean("vibrate", false);
        n = sharedPreferences.getBoolean("next", false);

        System.out.print("v = " + String.valueOf(v));
        System.out.print("s = " + String.valueOf(s));
        System.out.print("n = " + String.valueOf(n));

        final Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + ctx.getPackageName()  + "/" + R.raw.tone);
        vendorUID = i.getStringExtra("uid");

        System.out.println("inside bgservice before creating intent vendorUID = " + vendorUID);
        Intent intent = new Intent(context.getApplicationContext(), Requests.class);
        intent.putExtra("uid", vendorUID);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(), CHANNEL_ID)
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

        //getting dat aof reqs and launching requests.class
        final DatabaseReference req = FirebaseDatabase.getInstance().getReference("vendors/"+ vendorUID);
        req.child("req").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                System.out.println("uid = " + vendorUID);
                System.out.println("\n inside the onDataChange snapsjot = " + snapshot);
                if(snapshot != null) {
                    if(snapshot.hasChildren()) {
                        if(n) {
                            notificationManager = NotificationManagerCompat.from(context);
                            int notificationId = 1;
                            notificationManager.notify(notificationId,
                                    builder.build());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    protected void onHandleWork(@NonNull Intent i) {
        System.out.println("inside the onHandleWork");
    }
}
