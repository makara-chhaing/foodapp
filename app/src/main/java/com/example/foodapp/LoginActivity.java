package com.example.foodapp;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.foodapp.Databasehelper.Database;
import com.example.foodapp.Util.Util;

public class LoginActivity extends AppCompatActivity {

    TextView username, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(Util.fooduser_db == null){
            Log.d(Util.DEBUG, "Create Database");
            Util.fooduser_db = new Database(this,null);
        }

        username = findViewById(R.id.login_et_username);
        password = findViewById(R.id.login_et_password);
    }

    public void login(View v){
        String user = username.getText().toString();
        String pass = password.getText().toString();
        long result;
        if(!(user.equals("") || pass.equals(""))){
            result = Util.fooduser_db.fetchUser(user,pass);
            Log.d(Util.DEBUG, "result: " + result);
            if(result > 0){
                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra(Util.USER_ID, (int) result);
                Log.d(Util.DEBUG, result + " <- userID");
                startActivity(intent);
                finish();
            }else {
                Toast.makeText(this, "Incorrect Username or Password!", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_LONG).show();
        }

    }

    public void signUp(View v){
        startActivity(new Intent(this, SignupActivity.class));
    }
}