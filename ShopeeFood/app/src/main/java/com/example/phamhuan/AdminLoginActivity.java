package com.example.phamhuan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.phamhuan.ModelClass.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminLoginActivity extends AppCompatActivity {

        private EditText adminphone,adminpass;
        private Button adminlogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        adminphone = findViewById(R.id.adminPhone);
        adminpass = findViewById(R.id.adminPassword);
        adminlogin = findViewById(R.id.adminSignIn);

        adminlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adminsignin();
            }
        });
    }

    private String Name,Password;
    private void adminsignin() {
        Name = adminphone.getText().toString().trim();
        Password = adminpass.getText().toString().trim();

        //check input valid
        if(Name.isEmpty() || Name.length() < 10){
            Toast.makeText(AdminLoginActivity.this, "Số điện thoại đăng nhập không đúng định dạng.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(Password.isEmpty() || Password.length() < 6){
            Toast.makeText(AdminLoginActivity.this, "Mật khẩu trống hoặc chưa đúng định dạng.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!Name.equals("0968646031")){
            Toast.makeText(AdminLoginActivity.this, "Bạn không phải admin. Vui lòng đăng nhập người dùng!", Toast.LENGTH_SHORT).show();
            return;
        }

        signin();
    }

    private void signin() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Xin chờ ..."); progressDialog.show();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference table_user = database.getReference(Constant.UserBucket);
        table_user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(adminphone.getText().toString()).exists()) {
                    progressDialog.dismiss();
                    User user = dataSnapshot.child(adminphone.getText().toString()).getValue(User.class);
                    assert user != null;
                    user.setPhone(adminphone.getText().toString());
                    if (user.getPassword().equals(adminpass.getText().toString())) {
                        Utils.setValue(AdminLoginActivity.this,Constant.IsLogin,Constant.IsLogin);
                        Utils.setValue(AdminLoginActivity.this,Constant.Mobile,adminphone.getText().toString());
                        Utils.setValue(AdminLoginActivity.this,Constant.Password,adminpass.getText().toString());
                        Constant.AdminLogin = "true";
                        Toast.makeText(AdminLoginActivity.this, "Chào mừng Quản Trị Viên đến với ứng dụng!" , Toast.LENGTH_SHORT).show();
                        Intent home = new Intent(AdminLoginActivity.this, HomeActivity.class);
                        Utils.currentUser = user; startActivity(home); finish();
                    } else {
                        Utils.showToast(AdminLoginActivity.this, "Mật khẩu không đúng!");
                    }
                } else {
                    progressDialog.dismiss();
                    Utils.showToast(AdminLoginActivity.this, "Lỗi không xác định!");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Utils.showToast(AdminLoginActivity.this, databaseError.getMessage());
            }
        });
    }
}