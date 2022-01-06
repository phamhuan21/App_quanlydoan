package com.example.phamhuan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.phamhuan.R;
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

public class AddRestaurant extends AppCompatActivity {

    Toolbar toolbar;
    private EditText name,address,time,charge;
    private TextView status,typerestaurant;
    private ImageView imageView;
    private Button addrestaurant,pickimage,addtype;

    private ArrayList<String> type = new ArrayList<String>();
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
        setContentView(R.layout.add_restaurant);
        toolbar = findViewById(R.id.add_product_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        progressDialog = new ProgressDialog(AddRestaurant.this);
        //add restaurant
        name = findViewById(R.id.add_name_product);
        address = findViewById(R.id.add_desc_product);
        time = findViewById(R.id.add_time);
        status = findViewById(R.id.add_status);
        imageView = findViewById(R.id.img_name_1);
        pickimage = findViewById(R.id.img_icon);
        charge = findViewById(R.id.add_distance);
        addrestaurant = findViewById(R.id.add_restaurant);
        addtype = findViewById(R.id.btn_addtype);
        typerestaurant = findViewById(R.id.add_type);
        pickimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });
        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventDialog();
            }
        });
        typerestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showevent();
            }
        });
        addtype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addtoarray();
            }
        });
        addrestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkinput();
            }
        });

    }

    private void showevent() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Danh mục nhà hàng")
                .setItems(Constant.type, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String categorytv = Constant.type[i];
                        typerestaurant.setText(categorytv);

                    }
                })
                .show();
    }

    private String Name,Address,Time,Status,Charge,TypeRestaurant;

    private void addtoarray() {
        TypeRestaurant = typerestaurant.getText().toString().trim();
        if(TypeRestaurant.isEmpty()){
            Toast.makeText(this, "Danh mục nhà hàng không được để trống!", Toast.LENGTH_SHORT).show();
            return;
        }
        for(int i=0;i < type.size();i++){
            if(type.get(i).equals(TypeRestaurant)){
                Toast.makeText(this, "Error!!! Danh mục nhà hàng đã được thêm trước đó...!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        type.add(typerestaurant.getText().toString());
        Toast.makeText(this, "Đã thêm danh mục nhà hàng : " + typerestaurant.getText().toString(), Toast.LENGTH_SHORT).show();
        typerestaurant.setText("");
    }


    private void checkinput() {
        Name = name.getText().toString().trim();
        Address = address.getText().toString().trim();
        Time = time.getText().toString().trim();
        Charge = charge.getText().toString().trim();
        Status = status.getText().toString().trim();

        if(Name.isEmpty()){
            Toast.makeText(this, "Tên nhà hàng không được để trống!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(Address.isEmpty()){
            Toast.makeText(this, "Địa chỉ không được để trống!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(Time.isEmpty()){
            Toast.makeText(this, "Thời gian không được để trống!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(Charge.isEmpty()){
            Toast.makeText(this, "Bạn chưa thêm khoảng cách nhà hàng!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(Status.isEmpty()){
            Toast.makeText(this, "Trạng thái nhà hàng không được để trống!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(type.isEmpty()){
            Toast.makeText(this, "Bạn chưa thêm loại nhà hàng!", Toast.LENGTH_SHORT).show();
            return;
        }

        addtofirebase();

    }

    private void addtofirebase() {
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
                                    Integer charge_num = Integer.parseInt(charge.getText().toString());
                                    //createNewPost(imageUrl);


                                    //update to db

                                    for(int i = 0; i <type.size();i++){
                                        if(type.get(i).equals("Phở")){
                                            final HashMap<String, Object> cartMap = new HashMap<>();

                                            cartMap.put("Charge", charge_num);
                                            cartMap.put("Description", address.getText().toString());
                                            cartMap.put("Distance", time.getText().toString() + "min");
                                            cartMap.put("Image", imageUrl);
                                            cartMap.put("Name", name.getText().toString());
                                            cartMap.put("Status", status.getText().toString());
                                            cartMap.put("Type", "Phở");
                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Phở");
                                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    int count = (int) snapshot.getChildrenCount();

                                                    reference.child(String.valueOf(count)).updateChildren(cartMap)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    progressDialog.dismiss();
                                                                    Toast.makeText(AddRestaurant.this, "Success!", Toast.LENGTH_SHORT).show();

                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {

                                                                    Toast.makeText(AddRestaurant.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Toast.makeText(AddRestaurant.this, "" +error.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });


                                        }
                                        if(type.get(i).equals("Cơm")){
                                            final HashMap<String, Object> cartMaps = new HashMap<>();

                                            cartMaps.put("Charge", charge_num);
                                            cartMaps.put("Description", address.getText().toString());
                                            cartMaps.put("Distance", time.getText().toString() + "min");
                                            cartMaps.put("Image", imageUrl);
                                            cartMaps.put("Name", name.getText().toString());
                                            cartMaps.put("Status", status.getText().toString());
                                            cartMaps.put("Type", "Cơm");
                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Cơm");
                                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    int count = (int) snapshot.getChildrenCount();

                                                    reference.child(String.valueOf(count)).updateChildren(cartMaps)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    Toast.makeText(AddRestaurant.this, "Success!", Toast.LENGTH_SHORT).show();


                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {

                                                                    Toast.makeText(AddRestaurant.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Toast.makeText(AddRestaurant.this, "" +error.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }

                                        if(type.get(i).equals("Trà sữa")){
                                            final HashMap<String, Object> cartMapss = new HashMap<>();

                                            cartMapss.put("Charge", charge_num);
                                            cartMapss.put("Description", address.getText().toString());
                                            cartMapss.put("Distance", time.getText().toString() + "min");
                                            cartMapss.put("Image", imageUrl);
                                            cartMapss.put("Name", name.getText().toString());
                                            cartMapss.put("Status", status.getText().toString());
                                            cartMapss.put("Type", "Trà sữa");
                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Trà sữa");
                                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    int count = (int) snapshot.getChildrenCount();

                                                    reference.child(String.valueOf(count)).updateChildren(cartMapss)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    Toast.makeText(AddRestaurant.this, "Success!", Toast.LENGTH_SHORT).show();


                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {

                                                                    Toast.makeText(AddRestaurant.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Toast.makeText(AddRestaurant.this, "" +error.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }

                                        if(type.get(i).equals("Đồ ăn vặt")){
                                            final HashMap<String, Object> cartMapsss = new HashMap<>();

                                            cartMapsss.put("Charge", charge_num);
                                            cartMapsss.put("Description", address.getText().toString());
                                            cartMapsss.put("Distance", time.getText().toString() + "min");
                                            cartMapsss.put("Image", imageUrl);
                                            cartMapsss.put("Name", name.getText().toString());
                                            cartMapsss.put("Status", status.getText().toString());
                                            cartMapsss.put("Type", "Đồ ăn vặt");
                                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Đồ ăn vặt");
                                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    int count = (int) snapshot.getChildrenCount();

                                                    reference.child(String.valueOf(count)).updateChildren(cartMapsss)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    Toast.makeText(AddRestaurant.this, "Success!", Toast.LENGTH_SHORT).show();


                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {

                                                                    Toast.makeText(AddRestaurant.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Toast.makeText(AddRestaurant.this, "" +error.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }
                                    }

                                    //add to all " Tất cả "
                                        final HashMap<String, Object> cartMapp = new HashMap<>();

                                        cartMapp.put("Charge", charge_num);
                                        cartMapp.put("Description", address.getText().toString());
                                        cartMapp.put("Distance", time.getText().toString() + "min");
                                        cartMapp.put("Image", imageUrl);
                                        cartMapp.put("Name", name.getText().toString());
                                        cartMapp.put("Status", status.getText().toString());
                                        cartMapp.put("Type", "Tất cả");
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Tất cả");
                                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                int count = (int) snapshot.getChildrenCount();

                                                reference.child(String.valueOf(count)).updateChildren(cartMapp)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                Toast.makeText(AddRestaurant.this, "Success!", Toast.LENGTH_SHORT).show();
                                                                clear();


                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {

                                                                Toast.makeText(AddRestaurant.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Toast.makeText(AddRestaurant.this, "" +error.getMessage(), Toast.LENGTH_SHORT).show();
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
        charge.setText("");
        address.setText("");
        time.setText("");
        imageView.setImageResource(R.drawable.profileimg);
        status.setText("");
    }

    private void eventDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Trạng thái nhà hàng")
                .setItems(Constant.event, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String categorytv = Constant.event[i];
                        status.setText(categorytv);

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