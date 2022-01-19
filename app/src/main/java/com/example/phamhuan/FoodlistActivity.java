package com.example.phamhuan;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.example.phamhuan.ModelClass.Cart;
import com.example.phamhuan.ModelClass.Food;
import com.example.phamhuan.ModelClass.Kitchen;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class FoodlistActivity extends AppCompatActivity {

    Context context;
    private int finalcount = 0;
    private int finalprice = 0;
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<Food,FoodViewHolder> adapter;
    private ArrayList<String> getkey = new ArrayList<>();
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foodlist);
        Toolbar toolbar = findViewById(R.id.toolbar);
        progressDialog = new ProgressDialog(FoodlistActivity.this);
        setSupportActionBar(toolbar); Utils.showCart = false;
        Objects.requireNonNull(getSupportActionBar()).setTitle(Utils.categoryData.getName());
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setSubtitle(Utils.categoryData.getDescription());
        if(Constant.AdminLogin.equals("true")){
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FoodlistActivity.this,HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
//        TextView foodprice = findViewById(R.id.foodprice);
//        foodprice.setText(String.valueOf(Utils.categoryData.getDefaultPrice()) + "₫");
//        Button mi = findViewById(R.id.minu); Button ad = findViewById(R.id.ad);
//        final EditText cnt = findViewById(R.id.cartcoun); final int[] coun = {0}; final int[] pric = {0};
//        cnt.setText("0");
//        mi.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(Utils.categoryData.getStatus().toUpperCase().equals("OPEN")) {
//                    if (coun[0] > 0) {
//                        coun[0] = coun[0] - 1;
//                        pric[0] = coun[0] * Utils.categoryData.getDefaultPrice();
//                        finalcount = finalcount - 1; finalprice = finalprice - Utils.categoryData.getDefaultPrice();
//                        cnt.setText(String.valueOf(coun[0])); calculate();
////                        Cart cart = new Cart("Special Thali","https://i.ibb.co/zHhRBSz/410px-Thali-svg.png",Utils.categoryData.getDefaultPrice(),coun[0],Utils.categoryData.getName());
////                        CartList.addToCart(FoodlistActivity.this,"Special Thali",cart);
//                    }
//                } else {Utils.showToast(FoodlistActivity.this,"Not Open");}
//            }
//        });
//        ad.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(Utils.categoryData.getStatus().toUpperCase().equals("OPEN")) {
//                    coun[0] = coun[0] + 1;
//                    pric[0] = coun[0] * Utils.categoryData.getDefaultPrice();
//                    finalcount = finalcount + 1; finalprice = finalprice + Utils.categoryData.getDefaultPrice();
//                    cnt.setText(String.valueOf(coun[0])); calculate();
////                    Cart cart = new Cart("Special Thali","https://i.ibb.co/zHhRBSz/410px-Thali-svg.png",Utils.categoryData.getDefaultPrice(),coun[0],Utils.categoryData.getName());
////                    CartList.addToCart(FoodlistActivity.this,"Special Thali",cart);
//                } else {Utils.showToast(FoodlistActivity.this,"Not Open");}
//            }
//        });
        recyclerView = findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        loadListFood(Utils.categoryData.getName());
        adapter.startListening();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        public TextView FoodName;
        public ImageView FoodImage;
        public TextView FoodPrice;
        public TextView FoodQuantity;
        public Button Minus,Plus;
        public EditText FoodCount;
        public ImageButton Delete,Edit;
        public FoodViewHolder(View itemView) {
            super(itemView);
            FoodName = itemView.findViewById(R.id.food_name);
            FoodImage = itemView.findViewById(R.id.food_image);
            FoodPrice = itemView.findViewById(R.id.food_price);
            FoodQuantity = itemView.findViewById(R.id.food_quantity);
            Minus = itemView.findViewById(R.id.minus);
            Plus = itemView.findViewById(R.id.add);
            FoodCount = itemView.findViewById(R.id.cartcount);
            Delete = itemView.findViewById(R.id.delete_food);
            Edit = itemView.findViewById(R.id.edit_food);
        }
    }

    private void loadListFood(String kitchen) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(kitchen);
        databaseReference.keepSynced(false);
        DatabaseReference personsRef = FirebaseDatabase.getInstance().getReference().child(kitchen);
        Query personsQuery = personsRef.orderByKey();
        FirebaseRecyclerOptions<Food> personsOptions = new FirebaseRecyclerOptions.Builder<Food>().setQuery(personsQuery,Food.class).build();
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(personsOptions) {
            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_view, parent, false);
                return new FoodViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final FoodViewHolder viewHolder, final int i, @NonNull final Food model) {
                viewHolder.FoodName.setText(model.getName());
                Glide.with(getBaseContext()).load(model.getImage()).into(viewHolder.FoodImage);
                viewHolder.FoodPrice.setText( String.valueOf(model.getPrice()) + "₫");
                viewHolder.FoodQuantity.setText(model.getQuantity());
                final int[] count = {0}; final int[] price = {0};
                if(Constant.AdminLogin.equals("true")){
                    viewHolder.Edit.setVisibility(View.VISIBLE);
                    viewHolder.Delete.setVisibility(View.VISIBLE);
                }
                viewHolder.Edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DatabaseReference db = FirebaseDatabase.getInstance().getReference(Utils.categoryData.getName());
                        ValueEventListener eventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                    String name = ds.getKey();
                                    getkey.add(name);
                                    Intent intent = new Intent(FoodlistActivity.this,EditFoodActivity.class);
                                    intent.putExtra("keyvalue",getkey.get(0));
                                    intent.putExtra("Name",viewHolder.FoodName.getText().toString());
                                    intent.putExtra("Price",String.valueOf(model.getPrice()));
                                    intent.putExtra("Image",model.getImage());
                                    intent.putExtra("Restaurant",Utils.categoryData.getName());
                                    startActivity(intent);
                                    onBackPressed();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(FoodlistActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        };
                        db.addListenerForSingleValueEvent(eventListener);

                    }
                });
                viewHolder.Delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(FoodlistActivity.this);
                        builder.setTitle("Xóa món ăn")
                                .setMessage("Bạn có muốn xóa món ăn này khỏi menu ?")
                                .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setPositiveButton("Ok", null)
                                .show();
                        Button positivebutton = builder.show().getButton(AlertDialog.BUTTON_POSITIVE);
                        positivebutton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                progressDialog.setMessage("Vui lòng chờ ....");
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.show();

                                DatabaseReference db = FirebaseDatabase.getInstance().getReference(Utils.categoryData.getName());
                                ValueEventListener eventListener = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                                            String name = ds.getKey();
                                            getkey.add(name);
                                            db.child(getkey.get(0).toString()).removeValue()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            getkey.clear();
                                                            progressDialog.dismiss();
                                                            finish();

                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(FoodlistActivity.this, "" +e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Toast.makeText(FoodlistActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                };
                                db.addListenerForSingleValueEvent(eventListener);




                            }
                        });
                    }

                });
                viewHolder.FoodCount.setText("0");
                viewHolder.Minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Utils.categoryData.getStatus().toUpperCase().equals("OPEN")) {
                            if (count[0] > 0) {
                                count[0] = count[0] - 1;
                                price[0] = (int) (count[0] * model.getPrice());
                                finalcount = finalcount - 1; finalprice = (int) (finalprice - model.getPrice());
                                viewHolder.FoodCount.setText(String.valueOf(count[0])); calculate();
                                Cart cart = new Cart(model.getName(),model.getImage(),model.getPrice(),count[0],Utils.categoryData.getName());
                                CartList.addToCart(FoodlistActivity.this,model.getName(),cart);
                            }
                        } else {Utils.showToast(FoodlistActivity.this,"Not Open");}
                    }
                });
                viewHolder.Plus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Utils.categoryData.getStatus().toUpperCase().equals("OPEN")) {
                            count[0] = count[0] + 1;
                            price[0] = (int) (count[0] * model.getPrice());
                            finalcount = finalcount + 1; finalprice = (int) (finalprice + model.getPrice());
                            viewHolder.FoodCount.setText(String.valueOf(count[0])); calculate();
                            Cart cart = new Cart(model.getName(),model.getImage(),model.getPrice(),count[0],Utils.categoryData.getName());
                            CartList.addToCart(FoodlistActivity.this,model.getName(),cart);
                        } else {Utils.showToast(FoodlistActivity.this,"Not Open");}
                    }
                });
            }

        };
        recyclerView.setAdapter(adapter);
    }


    private void calculate() {
        LinearLayout cart = findViewById(R.id.cart);
        TextView item = findViewById(R.id.item);
        TextView amount = findViewById(R.id.amount);
        Button goCart = findViewById(R.id.checkout);
        goCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showCart = true;
                FoodlistActivity.super.onBackPressed();
            }
        });
        if(finalcount>0 && finalprice>0){
            item.setText(String.valueOf(finalcount) + " Món");
            amount.setText("Tổng cộng " + String.valueOf(finalprice) +"₫");
            cart.setVisibility(View.VISIBLE);
        } else {
            cart.setVisibility(View.GONE);
        }
    }

}