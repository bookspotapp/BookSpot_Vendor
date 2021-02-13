package com.bookspot.app.vendor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Extra extends AppCompatActivity {

    List<Container_Class.Vendor> vendorList;
    DatabaseReference vendorRef, detRef ;
    MaterialButton bt;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra);

        bt = (MaterialButton) findViewById(R.id.bt);
        img = (ImageView) findViewById(R.id.img);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vendorRef = FirebaseDatabase.getInstance().getReference("vendors");
                vendorRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        vendorList = new ArrayList<>();
                        for(DataSnapshot ds : snapshot.getChildren()){
                            Container_Class.Vendor vendor = ds.getValue(Container_Class.Vendor.class);
                            vendorList.add(vendor);
                        }
                        transferDataToDet(vendorList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
    }

    private void transferDataToDet(List<Container_Class.Vendor> vendorList) {
        detRef = FirebaseDatabase.getInstance().getReference("det/vendors");

        for(Container_Class.Vendor vendor : vendorList){
            Container_Class.Det det = new Container_Class.Det(
              vendor.getFname(),
              vendor.getUID(),
              vendor.getLat(),
              vendor.getLng()
            );
            if(vendor.getImage() != null && !vendor.getImage().equals("")) {
                Bitmap bmp = decodeFromFirebaseBase64(vendor.getImage());
                img.setImageBitmap(bmp);
                saveDataToStorage(bmp, vendor.getUID());
            }else{
                System.out.println("\n Failure! doesn't have image of id = "+ det.getUid());
                Toast.makeText(Extra.this, "Failure! doesn't have image", Toast.LENGTH_SHORT).show();
            }
            if(vendor.getCat() == null)
                System.out.println("\n Failure! doesn't have cat = "+ det.getUid());
            else
                detRef.child(vendor.getCat()).child(det.getUid()).setValue(det);
        }
    }

    public static Bitmap decodeFromFirebaseBase64(String image){
        byte[] decodedByteArray = Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }

    private void saveDataToStorage(Bitmap final_img, final String uid) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final_img.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://vendor-22662.appspot.com");
        StorageReference logoRef =  storageRef.child("vendors/"+ uid + "/logo.jpg");

        UploadTask uploadTask = logoRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                exception.printStackTrace();
                System.out.println("\n Failure! storage exception of id = "+ uid);
                System.out.println("\n Failure! storage exception");
                Toast.makeText(Extra.this, "Failure", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                System.out.println("\n Image has been added to the storage of id = "+ uid);
                Toast.makeText(Extra.this, "Success", Toast.LENGTH_SHORT).show();
            }
        });
    }
}