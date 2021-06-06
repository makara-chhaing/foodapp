package com.example.foodapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.foodapp.Entity.Food;
import com.example.foodapp.Util.Util;

import java.io.IOException;

public class NewFoodActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_READ_FOLDERS = 1;
    ImageView imageView;
TextView imageText, title, description, location, time;
Uri imageUri;
Bitmap imageBitmap;
int userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_food);
        imageView = findViewById(R.id.iv_addNewFood);
        imageText = findViewById(R.id.tv_add_image_id);
        title = findViewById(R.id.et_title_id);
        description = findViewById(R.id.et_description_id);
        location = findViewById(R.id.et_location);
        time = findViewById(R.id.et_pickup);
        userId = getIntent().getIntExtra(Util.USER_ID,0);

    }

    public void addImage(View v){
        try {
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                    new AlertDialog.Builder(this)
                            .setTitle("Permission needed!")
                            .setMessage("This is required!")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(NewFoodActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_FOLDERS);
                                }
                            })
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                    .create()
                    .show();
                }else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_FOLDERS);
                }
            }else {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            }

        }catch (Exception e){
            Log.e(Util.DEBUG, "Error Adding Image!");
        }
    }

    public void addFood(View v){
        try {
            if(imageBitmap!=null){
                Food food = new Food(title.getText().toString());
                food.setImgBitmap(imageBitmap);
                food.setDescription(description.getText().toString());
                food.setOwnerID(userId);
                food.setUserID(userId);
                Util.fooduser_db.addFood(food);
                startActivity(new Intent(this, HomeActivity.class));
            }
        } catch (Exception e) {
            Log.d("result: ",  " :Error addfoof?");
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISSION_REQUEST_READ_FOLDERS){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted!", Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data != null && data.getData()!=null){
            imageUri=data.getData();
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageView.setImageBitmap(imageBitmap);
                imageText.setText("");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}