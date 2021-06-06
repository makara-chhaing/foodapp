package com.example.foodapp;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.foodapp.Entity.User;
import com.example.foodapp.Util.Util;

public class SignupActivity extends AppCompatActivity {

    TextView username, password, name, email, phone, address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        username = findViewById(R.id.signup_et_username);
        password = findViewById(R.id.signup_et_password);
        name = findViewById(R.id.signup_et_name);
        email = findViewById(R.id.signup_et_email);
        phone = findViewById(R.id.signup_et_phone);
        address = findViewById(R.id.signup_et_address);
    }

    public void save(View v){
        String user = username.getText().toString();
        String pass = password.getText().toString();
        String e_mail = email.getText().toString();
        String fname = name.getText().toString();
        String add = address.getText().toString();
        String ph = phone.getText().toString();

        long result = 0;
        if(!(user.equals("") || pass.equals(""))){
            User u = new User(user, pass);
            u.setAddress(add);
            u.setName(fname);
            u.setEmail(e_mail);
            u.setPhone(ph);
            result = Util.fooduser_db.addUser(u);
            if(result > 0){
                Toast.makeText(this, "User Created!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }else {
                Toast.makeText(this, "Fail to create a new User!", Toast.LENGTH_LONG).show();
            }
        }


    }
}