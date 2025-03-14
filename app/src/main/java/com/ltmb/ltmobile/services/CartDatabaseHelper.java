package com.ltmb.ltmobile.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Adapter.CartItem;

public class CartDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "CartDB";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_CART = "cart";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_RESTAURANT_ID = "restaurant_id";
    private static final String COLUMN_IMAGE_URL = "image_url";
    private static final String COLUMN_TOPPING = "topping";

    public CartDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_CART + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_QUANTITY + " INTEGER, " +
                COLUMN_PRICE + " REAL, " +
                COLUMN_RESTAURANT_ID + " TEXT, " +
                COLUMN_IMAGE_URL + " TEXT, " +
                COLUMN_TOPPING + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        onCreate(db);
    }

    public void addToCart(CartItem item) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Kiểm tra xem món ăn đó có tồn tại trong cùng nhà hàng với cùng topping hay không
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_QUANTITY +
                        " FROM " + TABLE_CART +
                        " WHERE " + COLUMN_ID + "=? AND " + COLUMN_RESTAURANT_ID + "=? AND " + COLUMN_TOPPING + "=?",
                new String[]{String.valueOf(item.getId()), item.getRestaurantId(), item.getTopping()});

        if (cursor.moveToFirst()) {
            // Nếu món đã có trong giỏ với cùng topping -> Cập nhật số lượng
            int currentQuantity = cursor.getInt(0);
            int newQuantity = currentQuantity + item.getQuantity();

            ContentValues values = new ContentValues();
            values.put(COLUMN_QUANTITY, newQuantity);

            db.update(TABLE_CART, values, COLUMN_ID + "=? AND " + COLUMN_RESTAURANT_ID + "=? AND " + COLUMN_TOPPING + "=?",
                    new String[]{String.valueOf(item.getId()), item.getRestaurantId(), item.getTopping()});
        } else {
            // Nếu món chưa có hoặc topping khác -> Thêm mới vào giỏ hàng
            ContentValues values = new ContentValues();
            values.put(COLUMN_ID, item.getId());
            values.put(COLUMN_NAME, item.getName());
            values.put(COLUMN_QUANTITY, item.getQuantity());
            values.put(COLUMN_PRICE, item.getPrice());
            values.put(COLUMN_RESTAURANT_ID, item.getRestaurantId());
            values.put(COLUMN_IMAGE_URL, item.getImageUrl());
            values.put(COLUMN_TOPPING, item.getTopping());

            db.insert(TABLE_CART, null, values);
        }

        cursor.close();
        db.close();
    }



    // Lấy danh sách món trong giỏ hàng
    public List<CartItem> getCartItems(String restaurantId) {
        List<CartItem> cartItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CART + " WHERE " + COLUMN_RESTAURANT_ID + "=?",
                new String[]{restaurantId});

        if (cursor.moveToFirst()) {
            do {
                CartItem item = new CartItem(
                        cursor.getInt(0), // ID
                        cursor.getString(1), // Name
                        cursor.getInt(2), // Quantity
                        cursor.getDouble(3), // Price
                        cursor.getString(4), // Restaurant ID
                        cursor.getString(5), // Image URL
                        cursor.getString(6) // Topping
                );
                cartItems.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return cartItems;
    }

    // Cập nhật số lượng món ăn trong giỏ hàng (chỉ cập nhật đúng món với topping tương ứng)
    public void updateCartItem(int itemId, String restaurantId, int newQuantity, String topping) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_QUANTITY, newQuantity);

        db.update(TABLE_CART, values, COLUMN_ID + "=? AND " + COLUMN_RESTAURANT_ID + "=? AND " + COLUMN_TOPPING + "=?",
                new String[]{String.valueOf(itemId), restaurantId, topping});
        db.close();
    }

    // Xóa một món khỏi giỏ hàng
    public void removeCartItem(int itemId, String restaurantId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, COLUMN_ID + "=? AND " + COLUMN_RESTAURANT_ID + "=?",
                new String[]{String.valueOf(itemId), restaurantId});
        db.close();
    }

    // Xóa toàn bộ giỏ hàng theo từng nhà hàng
    public void clearCart(String restaurantId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, COLUMN_RESTAURANT_ID + "=?", new String[]{restaurantId});
        db.close();
    }

    // Tính tổng tiền của giỏ hàng
    public double getTotalPrice(String restaurantId) {
        SQLiteDatabase db = this.getReadableDatabase();
        double total = 0;
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_QUANTITY + ", " + COLUMN_PRICE + " FROM " + TABLE_CART + " WHERE " + COLUMN_RESTAURANT_ID + "=?",
                new String[]{restaurantId});

        if (cursor.moveToFirst()) {
            do {
                int quantity = cursor.getInt(0);
                double price = cursor.getDouble(1);


                total += quantity * price ;
            } while (cursor.moveToNext());
        }
        cursor.close();
        return total;
    }
    public int getNextId() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT MAX(id) FROM cart", null);

        int nextId = 1;
        if (cursor.moveToFirst() && cursor.getInt(0) > 0) {
            nextId = cursor.getInt(0) + 1;
        }
        cursor.close();
        return nextId;
    }
}
