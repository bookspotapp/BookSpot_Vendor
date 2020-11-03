package com.bookspot.app.vendor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;

import java.util.List;

public class ViewYourRatings extends AppCompatActivity {

    ImageView back;
    RatingBar service, punctuality, hygeine, covid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_your_ratings);

        initializeit();
    }

    private void initializeit() {
        back = (ImageView) findViewById(R.id.back_arrow);
        service = (RatingBar) findViewById(R.id.service_quality);
        punctuality = (RatingBar) findViewById(R.id.punctuality);
        hygeine = (RatingBar) findViewById(R.id.hygeine);
        covid = (RatingBar) findViewById(R.id.covid_norms);

        String[] rat = SplashScreen.vendor.getRat().split(",");

        service.setRating(Float.parseFloat(rat[0]));
        hygeine.setRating(Float.parseFloat(rat[1]));
        punctuality.setRating(Float.parseFloat(rat[2]));
        covid.setRating(Float.parseFloat(rat[3]));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}