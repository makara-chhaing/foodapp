package com.example.foodapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.foodapp.Adapter.FoodAdapter;
import com.example.foodapp.Util.Util;

public class MyListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    int userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);

        userID = getIntent().getIntExtra(Util.USER_ID, 0);

        recyclerView = findViewById(R.id.recyclerView2);
        try {
            FoodAdapter adapter = new FoodAdapter(this, Util.fooduser_db.fetchFood(userID));
            recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));
            recyclerView.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
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
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(Util.USER_ID,0);
        intent.putExtra("out",1);
        startActivity(intent);
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