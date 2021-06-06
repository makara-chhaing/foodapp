package com.example.foodapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.foodapp.Adapter.FoodAdapter;
import com.example.foodapp.Databasehelper.Database;
import com.example.foodapp.Util.Util;

public class HomeActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    int userID, out;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if(Util.fooduser_db == null){
            Log.d(Util.DEBUG, "Create Database");
            Util.fooduser_db = new Database(this,null);
        }
        Intent intent = getIntent();
        if(intent.getIntExtra("out",0)==1){
            signOut();
        }
        sharedPreferences = getSharedPreferences(Util.LOGIN_STATE, MODE_PRIVATE);
        if(intent.getIntExtra(Util.USER_ID, 0)==0){
            if(sharedPreferences.getInt(Util.USER_ID, 0) == 0){
                startActivity(new Intent(this, LoginActivity.class));
            }else {
                userID = sharedPreferences.getInt(Util.USER_ID, 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(Util.USER_ID, userID);
                editor.commit();
            }
        }else {
            userID = intent.getIntExtra(Util.USER_ID, 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(Util.USER_ID, userID);
            editor.commit();
        }
        recyclerView = findViewById(R.id.recyclerView);
        try {
            FoodAdapter adapter = new FoodAdapter(this, Util.fooduser_db.fetchAllFood());
            recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));
            recyclerView.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addNewFood(View v){
        Intent intent = new Intent(this, NewFoodActivity.class);
        intent.putExtra(Util.USER_ID, userID);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.foodmenu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.account:
                openAccount();
                return true;
            case R.id.home:
                openHome();
                return true;
            case R.id.mylist:
                openMylist();
                return true;
            case R.id.signout:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void signOut() {
        sharedPreferences = getSharedPreferences(Util.LOGIN_STATE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Util.USER_ID, 0);
        editor.commit();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void openMylist() {
        Intent intent = new Intent(this, MyListActivity.class);
        intent.putExtra(Util.USER_ID, userID);
        startActivity(intent);
        finish();
    }

    private void openHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(Util.USER_ID, userID);
        startActivity(intent);
        finish();
    }

    private void openAccount() {

    }
}