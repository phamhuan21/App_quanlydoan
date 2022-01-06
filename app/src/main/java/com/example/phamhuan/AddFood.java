package com.example.phamhuan;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

public class AddFood extends AppCompatActivity {

    Toolbar toolbar;
    private EditText name,price;
    private TextView listrestaurant;
    private ImageView imageView;
    private Button addfood,pickimage;

    private ArrayList<String> list = new ArrayList<String>();
    private static final int RESULT_OK = -1;
    // permission image
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;
    //image pick constant
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;
    //permission array
    private String[] cameraPermissions;
    private String[] storagePermissions;
    private Uri image_uri;
    private UploadTask uploadTask;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        toolbar = findViewById(R.id.add_product_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // event when click home button
                finish();
            }
        });

        progressDialog = new ProgressDialog(AddFood.this);
        //add restaurant
        name = findViewById(R.id.food_name);
        price = findViewById(R.id.food_price);
        imageView = findViewById(R.id.img_name_1);
        pickimage = findViewById(R.id.btn_img);
        addfood = findViewById(R.id.add_food);
        listrestaurant = findViewById(R.id.list_retaurants);

        pickimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });
        // get list from firebase
        listrestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetListRestaurant();

            }
        });

        addfood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewFood();
            }
        });


    }
    private String Name,Price,ListRestaurant;
    private void AddNewFood() {
        Name = name.getText().toString().trim();
        Price = price.getText().toString().trim();
        ListRestaurant = listrestaurant.getText().toString().trim();

        if(Name.isEmpty()){
            Toast.makeText(this, "Tên món ăn không được để trống!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(Price.isEmpty()){
            Toast.makeText(this, "Giá món ăn không được để trống!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(ListRestaurant.isEmpty()){
            Toast.makeText(this, "Tên nhà hàng không được để trống!", Toast.LENGTH_SHORT).show();
            return;
        }

        addtodatabase();
    }

    private void addtodatabase() {
        progressDialog.setMessage("Vui lòng chờ ....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        String timestamp = "" + System.currentTimeMillis();

        String filePathAndName = "restaurant/" + "" + timestamp;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
        if(image_uri != null && !image_uri.equals(Uri.EMPTY)) {
            uploadTask = storageReference.putFile(image_uri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    if (taskSnapshot.getMetadata() != null) {
                        if (taskSnapshot.getMetadata().getReference() != null) {
                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    Integer price_num = Integer.parseInt(price.getText().toString());

                                    //update to db

                                    final HashMap<String, Object> cartMap = new HashMap<>();
                                    cartMap.put("Name",name.getText().toString());
                                    cartMap.put("Price",price_num);
                                    cartMap.put("Image",imageUrl);

                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(listrestaurant.getText().toString());
                                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            int count = (int) snapshot.getChildrenCount();

                                            reference.child(String.valueOf(count)).updateChildren(cartMap)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(AddFood.this, "Success!", Toast.LENGTH_SHORT).show();
                                                            clear();


                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                            Toast.makeText(AddFood.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(AddFood.this, "" +error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });


                                }
                            });
                        }
                    }


                }
            });



        } else {
            Toast.makeText(this, "Bạn chưa thêm ảnh cho nhà hàng!", Toast.LENGTH_SHORT).show();
        }


    }

    private void clear() {
        name.setText("");
        price.setText("");
        listrestaurant.setText("");
        imageView.setImageResource(R.mipmap.logo_circle);
    }

    private void GetListRestaurant() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tất cả");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot namesnapshot: snapshot.getChildren()){
                    list.add(namesnapshot.child("Name").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddFood.this, "" +error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Danh sách nhà hàng")
                .setItems(list.toArray(new String[0]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String categorytv = list.get(i);
                        listrestaurant.setText(categorytv);


                    }
                })
                .show();


    }

    private void showImagePickDialog() {
        String[] options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            if (checkCameraPermission()) {
                                pickFromCamera();
                            } else {
                                requestCameraPermission();
                            }
                        } else {
                            if (checkStoragePermission()) {
                                pickFromGallery();
                            } else {
                                requestStoragePermission();
                            }
                        }
                    }
                })
                .show();
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_Image_Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Image_Description");

        image_uri =  getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted) {
                        pickFromCamera();
                    } else {
                        Toast.makeText(this, "Quyền truy cập camera và bộ nhớ là bắt buộc !", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(this, "Quyền truy cập bộ nhớ là bắt buộc !", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {

            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                image_uri = data.getData();
                imageView.setImageURI(image_uri);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                imageView.setImageURI(image_uri);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}