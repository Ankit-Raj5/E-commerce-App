package com.example.ecommerceapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.icu.text.LocaleDisplayNames;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ecommerceapp.R;
import com.example.ecommerceapp.models.NewProductsModel;
import com.example.ecommerceapp.models.PopularProductsModel;
import com.example.ecommerceapp.models.ShowAllModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;

public class DetailedActivity extends AppCompatActivity {

    ImageView detailedImg,addItems,removeItems;
    TextView rating,name,description,price,quantity;
    Button addToCart,buyNow;
    int totalQuantity = 1;
    int totalPrice = 0;

    Toolbar toolbar;


    //New products
    NewProductsModel newProductsModel= null;

    //New products
    PopularProductsModel popularProductsModel= null;

    //Show all products
    ShowAllModel showAllModel = null;

    private FirebaseFirestore firestore;


    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);


        toolbar = findViewById(R.id.detailed_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        firestore = FirebaseFirestore.getInstance();

        final Object obj = getIntent().getSerializableExtra("detailed");
        if(obj instanceof NewProductsModel){

            newProductsModel = (NewProductsModel) obj;
        }else if(obj instanceof PopularProductsModel){

            popularProductsModel = (PopularProductsModel) obj;
        }
        else if(obj instanceof ShowAllModel){

            showAllModel = (ShowAllModel) obj;
        }


        auth = FirebaseAuth.getInstance();


        detailedImg = findViewById(R.id.detailed_img);
        addItems = findViewById(R.id.add_item);
        removeItems = findViewById(R.id.remove_item);
        rating = findViewById(R.id.rating);
        name = findViewById(R.id.detailed_name);
        description = findViewById(R.id.detailed_desc);
        price = findViewById(R.id.detailed_price);
        addToCart = findViewById(R.id.add_to_cart);
        buyNow = findViewById(R.id.buy_now);
        quantity = findViewById(R.id.quantity);


        if(newProductsModel != null){
            //New Products
            Glide.with(getApplicationContext()).load(newProductsModel.getImg_url()).into(detailedImg);
            name.setText(newProductsModel.getName());
            rating.setText(newProductsModel.getRating());
            description.setText(newProductsModel.getDescription());
            price.setText(newProductsModel.getPrice());

            totalPrice = newProductsModel.getPrice()*totalQuantity;
        }

        if(popularProductsModel != null){
            //Popular Products
            Glide.with(getApplicationContext()).load(popularProductsModel.getImg_url()).into(detailedImg);
            name.setText(popularProductsModel.getName());
            rating.setText(popularProductsModel.getRating());
            description.setText(popularProductsModel.getDescription());
            price.setText(popularProductsModel.getPrice());

            totalPrice = popularProductsModel.getPrice()*totalQuantity;
        }

        if(showAllModel != null){
            //Show All Products
            Glide.with(getApplicationContext()).load(showAllModel.getImg_url()).into(detailedImg);
            name.setText(showAllModel.getName());
            rating.setText(showAllModel.getRating());
            description.setText(showAllModel.getDescription());
//            price.setText(showAllModel.getPrice());

            totalPrice = showAllModel.getPrice()*totalQuantity;
        }


        //buy now

        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailedActivity.this,AddressActivity.class);

                if(newProductsModel !=null){

                    intent.putExtra("item",newProductsModel);
                }
                if(popularProductsModel != null){

                    intent.putExtra("item",popularProductsModel);
                }
                if(showAllModel != null){

                    intent.putExtra("item",showAllModel);
                }
                startActivity(intent);
            }
        });


        //add to cart
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
            }
        });

        addItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(totalQuantity<10){
                    totalQuantity++;
                    quantity.setText(String.valueOf(totalQuantity));

                    if(newProductsModel != null){
                        totalPrice = newProductsModel.getPrice()*totalQuantity;
                    }

                    if(popularProductsModel != null){
                        totalPrice = popularProductsModel.getPrice()*totalQuantity;
                    }

                    if(showAllModel != null){
                        totalPrice = showAllModel.getPrice()*totalQuantity;
                    }
                }
            }
        });

        removeItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(totalQuantity>1){
                    totalQuantity--;
                    quantity.setText(String.valueOf(totalQuantity));
                }
            }
        });
    }

    private void addToCart() {

        String saveCurrentTime,saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());


        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());


        final HashMap<String,Object>cartMap =  new HashMap<>();
        cartMap.put("productName",name.getText().toString());
        cartMap.put("productPrice",price.getText().toString());
        cartMap.put("currentTime",saveCurrentTime);
        cartMap.put("currentDate",saveCurrentDate);
        cartMap.put("Total Quantity",quantity.getText().toString());
        cartMap.put("Total price",totalPrice);

        firestore.collection("AddToCart").document(auth.getCurrentUser().getUid())
                .collection("User").add(cartMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {


                        Toast.makeText(DetailedActivity.this, "Added To Cart", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}