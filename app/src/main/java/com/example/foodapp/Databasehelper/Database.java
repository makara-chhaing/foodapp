package com.example.foodapp.Databasehelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.example.foodapp.Entity.Food;
import com.example.foodapp.Entity.User;
import com.example.foodapp.Util.Util;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {

    public Database(@Nullable Context context, @Nullable SQLiteDatabase.CursorFactory factory) {
        super(context, Util.DATABASE_NAME, factory, Util.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + Util.USER_TABLE_NAME + " (" + Util.USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Util.USERNAME + " TEXT, " + Util.NAME + " TEXT," + Util.ADDRESS + " TEXT," + Util.EMAIL + " TEXT,"
                + Util.PHONE + " INTEGER," +  Util.PASSWORD + " TEXT)";
        String CREATE_FOOD_TABLE = "CREATE TABLE " + Util.FOOD_TABLE_NAME + " (" + Util.FOOD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Util.FOOD_NAME + " TEXT, " + Util.FOOD_IMAGE + " BLOB," + Util.FOOD_DESCRIPTION + " TEXT, " + Util.OWNER_ID + " INTEGER, " + Util.USER_ID + " INTEGER)";
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_FOOD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_TABLE = "DROP TABLE IF EXISTS " + Util.USER_TABLE_NAME;
        db.execSQL(DROP_TABLE);
        DROP_TABLE = "DROP TABLE IF EXISTS " + Util.FOOD_TABLE_NAME;
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    public long addUser(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Util.USERNAME, user.getUsername());
        contentValues.put(Util.PASSWORD, user.getPassword());
        if (!user.getName().equals("")) contentValues.put(Util.NAME, user.getName());
        if (!user.getPhone().equals(""))contentValues.put(Util.PHONE, user.getPhone());
        if (!user.getEmail().equals(""))contentValues.put(Util.EMAIL, user.getEmail());
        if (!user.getAddress().equals(""))contentValues.put(Util.ADDRESS, user.getAddress());
        long newRow = db.insert(Util.USER_TABLE_NAME, null, contentValues);
        db.close();
        return newRow;
    }

    public long addFood(Food food){
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues foodContentValues = new ContentValues();
            foodContentValues.put(Util.FOOD_NAME, food.getName());
            Bitmap image = food.getImgBitmap();
            ByteArrayOutputStream imageOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100,imageOutputStream);
            byte[] imageByte = imageOutputStream.toByteArray();
            foodContentValues.put(Util.FOOD_IMAGE, imageByte);
            foodContentValues.put(Util.FOOD_DESCRIPTION, food.getDescription());
            foodContentValues.put(Util.USER_ID, food.getUserID());
            foodContentValues.put(Util.OWNER_ID, food.getOwnerID());
            long newRow = db.insert(Util.FOOD_TABLE_NAME, null, foodContentValues);
            db.close();
            return newRow;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return  0;
    }

    public long fetchUser(String username, String password){
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(Util.USER_TABLE_NAME,new String[]{Util.USER_ID}, Util.USERNAME + " =? AND " + Util.PASSWORD + " =? ",
                    new String[] {username, password}, null, null, null);
            long userId = 0;
            if (cursor != null && cursor.moveToFirst()) {
                userId = cursor.getLong(0);
                return userId;
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean delete(String name){
        SQLiteDatabase db = this.getWritableDatabase();
//        String delete = "DELETE FROM " + Util.FOOD_TABLE_NAME + " WHERE " + Util.FOOD_ID + " = '" + id + "'";
//        db.execSQL(delete);

        boolean result = db.delete(Util.FOOD_TABLE_NAME, Util.FOOD_NAME + "=?", new String[]{name}) > 0;
        db.close();
        return result;
    }

    public List<Food> fetchFood(int userID){
        List<Food> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + Util.FOOD_ID + "," + Util.FOOD_NAME + "," + Util.FOOD_IMAGE + "," + Util.FOOD_DESCRIPTION + "," + Util.OWNER_ID + " FROM " + Util.FOOD_TABLE_NAME +
                " JOIN " + Util.USER_TABLE_NAME + " ON " + Util.FOOD_TABLE_NAME + "." + Util.USER_ID + "=" +
                Util.USER_TABLE_NAME + "." + Util.USER_ID + " WHERE " + Util.USER_TABLE_NAME + "." +Util.USER_ID + "=" + userID;
        Cursor c = db.rawQuery(query, null);
        if(c.moveToNext()){
            do{
                int foodID = c.getInt(0);
                String name = c.getString(1);
                byte[] foodImg = c.getBlob (2);
                String des = c.getString(3);
                int owner = c.getInt(4);
                Food food = new Food(name);
                food.setUserID(userID);
                food.setDescription(des);
                Bitmap img = BitmapFactory.decodeByteArray(foodImg,0, foodImg.length);
                food.setImgBitmap(img);
                food.setOwnerID(owner);
                list.add(food);
            }while (c.moveToNext());
        }
        c.close();
        db.close();

        return list;
    }

    public List<Food> fetchAllFood(){
        List<Food> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + Util.FOOD_ID + "," + Util.FOOD_NAME + "," + Util.FOOD_IMAGE + "," + Util.FOOD_DESCRIPTION + "," + Util.OWNER_ID + " FROM " + Util.FOOD_TABLE_NAME +
                " JOIN " + Util.USER_TABLE_NAME + " ON " + Util.FOOD_TABLE_NAME + "." + Util.USER_ID + "=" +
                Util.USER_TABLE_NAME + "." + Util.USER_ID;
        Cursor c = db.rawQuery(query, null);
        if(c.moveToNext()){
            do{
                int foodID = c.getInt(0);
                String name = c.getString(1);
                byte[] foodImg = c.getBlob (2);
                String des = c.getString(3);
                int owner = c.getInt(4);
                Food food = new Food(name);
                food.setDescription(des);
                Bitmap img = BitmapFactory.decodeByteArray(foodImg,0, foodImg.length);
                food.setImgBitmap(img);
                food.setOwnerID(owner);
                list.add(food);
            }while (c.moveToNext());
        }
        c.close();
        db.close();

        return list;
    }
}
