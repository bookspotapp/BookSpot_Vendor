package com.bookspot.app.vendor;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Categories extends AppCompatActivity {

    ImageView back ;
    RecyclerView categories_rv;

    ArrayList categories = new ArrayList<>(Arrays.asList("Gyms & Turfs", "Restaurants", "Salons", "Doctors", "Diagnostic Centres", "Service Centres", "Banks", "Retails", "Government Offices"));
    ArrayList images = new ArrayList<>(Arrays.asList(R.drawable.gym, R.drawable.restraunt, R.drawable.salon, R.drawable.doctor, R.drawable.diagnostic, R.drawable.service, R.drawable.bank, R.drawable.retail, R.drawable.govt));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        back = (ImageView) findViewById(R.id.back_arrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        categories_rv = (RecyclerView) findViewById(R.id.categories_rv);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL); // set Horizontal Orientation
        categories_rv.setLayoutManager(gridLayoutManager);
        Adapter adapter = new Adapter(categories);
        categories_rv.setAdapter(adapter);
    }

    public class Adapter extends RecyclerView.Adapter<Adapter.ProductViewHolder> {
        private String rv;
        private List<String> list;


        public Adapter(List<String> list) {
            this.list = list;
            this.rv = rv;
        }

        @Override
        public Adapter.ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //inflating and returning our view holder
            LayoutInflater inflater = LayoutInflater.from(Categories.this);
            View view;
            view = inflater.inflate(R.layout.categories_card, parent , false);

            return new Adapter.ProductViewHolder(view);
        }

        @Override
        public void onBindViewHolder(Adapter.ProductViewHolder holder, int position) {
            final String product = list.get(position);

            holder.name.setText(product);
            holder.imageView.setImageResource((Integer) images.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Categories.this, Service.class);
                    intent.putExtra("category", product);
                    startActivity(intent);
                }
            });
        }


        @Override
        public int getItemCount() {
            return list.size();
        }


        class ProductViewHolder extends RecyclerView.ViewHolder {

            TextView name ;
            ImageView imageView;

            public ProductViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.name);
                imageView = itemView.findViewById(R.id.image);
            }
        }
    }

}