package com.example.phamhuan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.phamhuan.ModelClass.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private View login,register,forgot;
    private EditText logPhone,logPassword,regName,regPhone,regPassword,forPhone;
    private TextView forgotpass;
    TextView btnsigninforget;
    private long exitTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Main view
        login = findViewById(R.id.login);
        forgot = findViewById(R.id.forgot);
        register = findViewById(R.id.register);
        forgotpass = findViewById(R.id.forgetpass);
        // Login page view
        logPhone = findViewById(R.id.logPhone);
        logPassword = findViewById(R.id.logPassword);
        TextView goregister = findViewById(R.id.gosignup);
        TextView goforget = findViewById(R.id.txtforgot);
        Button btnLogin = findViewById(R.id.btnSignIn);
        TextView adminLogin = findViewById(R.id.adminlog);
        // Forget page view
        forPhone = findViewById(R.id.forPhone);
        Button btnForget = findViewById(R.id.btnForgot);
        btnsigninforget = findViewById(R.id.gosigninforget);
        // Register page view
        regName = findViewById(R.id.regName);
        regPhone = findViewById(R.id.regPhone);
        regPassword = findViewById(R.id.regPassword);
        TextView gologin = findViewById(R.id.gosignin);
        Button btnRegister = findViewById(R.id.btnSignUp);

        adminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AdminLoginActivity.class);
                startActivity(intent);
            }
        });
        gologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register.setVisibility(View.GONE);
                forgot.setVisibility(View.GONE);
                login.setVisibility(View.VISIBLE);
            }
        });
        goregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.setVisibility(View.GONE);
                forgot.setVisibility(View.GONE);
                register.setVisibility(View.VISIBLE);
            }
        });
        goforget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.setVisibility(View.GONE);
                register.setVisibility(View.GONE);
                forgot.setVisibility(View.VISIBLE);
            }
        });
        btnsigninforget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register.setVisibility(View.GONE);
                forgot.setVisibility(View.GONE);
                login.setVisibility(View.VISIBLE);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginProcess();
            }
        });
        btnForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgetProcess();
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerProcess();
            }
        });


    }

    private void loginProcess() {
        if (logPhone.getText().toString().equals("") || logPassword.getText().toString().equals("")) {
            Utils.showToast(this, "??i???n t???t c??? ?? tr???ng!");
        } else if (logPhone.getText().toString().length() != 10) {
            Utils.showToast(this, "?????nh d???ng s??? ??i???n tho???i sai!");
        } else if (logPassword.getText().toString().length() < 8) {
            Utils.showToast(this, "M???t kh???u ??t nh???t 8 k?? t???!");
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Xin ch??? ..."); progressDialog.show();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference table_user = database.getReference(Constant.UserBucket);
            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(logPhone.getText().toString().equals("0968646031")){
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Hi Admin, Vui l??ng ????ng nh???p v???i admin login.", Toast.LENGTH_SHORT).show();
                    } else {
                        if (dataSnapshot.child(logPhone.getText().toString()).exists()) {
                            progressDialog.dismiss();
                            User user = dataSnapshot.child(logPhone.getText().toString()).getValue(User.class);
                            assert user != null;
                            user.setPhone(logPhone.getText().toString());
                            if (user.getPassword().equals(logPassword.getText().toString())) {
                                Utils.setValue(MainActivity.this, Constant.IsLogin, Constant.IsLogin);
                                Utils.setValue(MainActivity.this, Constant.Mobile, logPhone.getText().toString());
                                Utils.setValue(MainActivity.this, Constant.Password, logPassword.getText().toString());
                                Intent home = new Intent(MainActivity.this, HomeActivity.class);
                                Utils.currentUser = user;
                                startActivity(home);
                                finish();
                            } else {
                                Utils.showToast(MainActivity.this, "S??? ??i???n tho???i ho???c m???t kh???u sai!");
                            }
                        } else {
                            progressDialog.dismiss();
                            Utils.showToast(MainActivity.this, "Vui l??ng ????ng k?? t??i kho???n!");
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Utils.showToast(MainActivity.this, databaseError.getMessage());
                }
            });
        }
    }

    private void registerProcess() {
        if (regPhone.getText().toString().equals("") || regPassword.getText().toString().equals("") || regName.getText().toString().equals("")) {
            Utils.showToast(this, "??i???n t???t c??? ?? tr???ng!");
        } else if (regPhone.getText().toString().length() != 10) {
            Utils.showToast(this, "?????nh d???ng s??? ??i???n tho???i sai!");
        } else if (regPassword.getText().toString().length() < 8) {
            Utils.showToast(this, "M???t kh???u ??t nh???t 8 k?? t???!");
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Xin ch??? ..."); progressDialog.show();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference table_user = database.getReference(Constant.UserBucket);
            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(Objects.requireNonNull(regPhone.getText()).toString()).exists()) {
                        progressDialog.dismiss();
                        Utils.showToast(MainActivity.this, "S??? ??i???n tho???i ???? t???n t???i!");
                    } else {
                        progressDialog.dismiss();
                        User user = new User(Objects.requireNonNull(regName.getText()).toString(), Objects.requireNonNull(regPassword.getText()).toString(),"","");
                        table_user.child(regPhone.getText().toString()).setValue(user);
                        Utils.showToast(MainActivity.this, "????ng k?? t??i kho???n th??nh c??ng.");
                        register.setVisibility(View.GONE);
                        forgot.setVisibility(View.GONE);
                        login.setVisibility(View.VISIBLE);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Utils.showToast(MainActivity.this, databaseError.getMessage());
                }
            });
        }
    }

    private void forgetProcess() {
        if (forPhone.getText().toString().equals("")) {
            Utils.showToast(this, "??i???n t???t c??? ?? tr???ng!");
        } else if (forPhone.getText().toString().length() != 10) {
            Utils.showToast(this, "?????nh d???ng s??? ??i???n tho???i sai!");
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Xin ch??? ..."); progressDialog.show();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference table_user = database.getReference(Constant.UserBucket);
            table_user.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(forPhone.getText().toString()).exists()) {
                        progressDialog.dismiss();
                        User user = dataSnapshot.child(forPhone.getText().toString()).getValue(User.class);
                        assert user != null;
                        user.setPhone(forPhone.getText().toString());
                        forgotpass.setText("M???t kh???u b???n l?? " + user.getPassword());
                    } else {
                        progressDialog.dismiss();
                        Utils.showToast(MainActivity.this, "T??i kho???n kh??ng t???n t???i!");
                        login.setVisibility(View.GONE);
                        forgot.setVisibility(View.GONE);
                        register.setVisibility(View.VISIBLE);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Utils.showToast(MainActivity.this, databaseError.getMessage());
                }
            });
        }
    }
    @Override
    public void onBackPressed() {

            exitApp();

    }

    private void exitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, "Ch???m l???n n???a ????? tho??t", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finishAffinity();
            finish();
        }
    }
}