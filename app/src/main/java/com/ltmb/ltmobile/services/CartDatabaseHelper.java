package com.ltmb.ltmobile.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
    private static final String COLUMN_TOPPING = "topping"; // Lưu JSON

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

    // Chuyển List<Map<String, Object>> thành chuỗi JSON
    private String toppingsToJson(List<Map<String, Object>> toppings) {
        JSONArray jsonArray = new JSONArray();
        for (Map<String, Object> topping : toppings) {
            JSONObject jsonObject = new JSONObject(topping);
            jsonArray.put(jsonObject);
        }
        return jsonArray.toString();
    }

    // Chuyển chuỗi JSON từ SQLite thành List<Map<String, Object>>
    private List<Map<String, Object>> jsonToToppings(String json) {
        List<Map<String, Object>> toppings = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Map<String, Object> map = new HashMap<>();
                for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
                    String key = it.next();
                    map.put(key, jsonObject.get(key));
                }
                toppings.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return toppings;
    }

    public void addToCart(CartItem item) {
        SQLiteDatabase db = this.getWritableDatabase();

        String toppingsJson = toppingsToJson(item.getToppings());

        // Log kiểm tra dữ liệu trước khi thêm vào giỏ hàng
        Log.d("DB_DEBUG", "Adding Item - Name: " + item.getName() + ", Restaurant: " + item.getRestaurantId() + ", Topping JSON: " + toppingsJson);

        // Tìm xem món đã có trong giỏ hàng chưa (dựa trên tên món, nhà hàng, topping)
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_QUANTITY +
                        " FROM " + TABLE_CART +
                        " WHERE " + COLUMN_NAME + "=? AND " + COLUMN_RESTAURANT_ID + "=? AND " + COLUMN_TOPPING + "=?",
                new String[]{item.getName(), item.getRestaurantId(), toppingsJson});

        if (cursor.moveToFirst()) {
            // Nếu món đã có trong giỏ hàng, cập nhật số lượng
            int currentQuantity = cursor.getInt(0);
            int newQuantity = currentQuantity + item.getQuantity();

            ContentValues values = new ContentValues();
            values.put(COLUMN_QUANTITY, newQuantity);

            int rowsUpdated = db.update(TABLE_CART, values, COLUMN_NAME + "=? AND " + COLUMN_RESTAURANT_ID + "=? AND " + COLUMN_TOPPING + "=?",
                    new String[]{item.getName(), item.getRestaurantId(), toppingsJson});

            Log.d("DB_DEBUG", "Updated quantity: " + newQuantity + ", Rows affected: " + rowsUpdated);
        } else {
            // Nếu chưa có trong giỏ hàng, thêm mới
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, item.getName());
            values.put(COLUMN_QUANTITY, item.getQuantity());
            values.put(COLUMN_PRICE, item.getPrice());
            values.put(COLUMN_RESTAURANT_ID, item.getRestaurantId());
            values.put(COLUMN_IMAGE_URL, item.getImageUrl());
            values.put(COLUMN_TOPPING, toppingsJson);

            long newRowId = db.insert(TABLE_CART, null, values);
            Log.d("DB_DEBUG", "Inserted new item, Row ID: " + newRowId);
        }
        cursor.close();
        db.close();
    }


    public List<CartItem> getCartItems(String restaurantId) {
        List<CartItem> cartItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CART + " WHERE " + COLUMN_RESTAURANT_ID + "=?", new String[]{restaurantId});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("image_url"));
                String toppingsJson = cursor.getString(cursor.getColumnIndexOrThrow("topping"));

                // Chuyển đổi JSON toppings thành List<Map<String, Object>>
                List<Map<String, Object>> toppings = CartItem.jsonToToppings(toppingsJson);

                cartItems.add(new CartItem(id, name, quantity, price, restaurantId, imageUrl, toppings));
            }
            cursor.close();
        }
        db.close();
        return cartItems;
    }


    public double getTotalPrice(String restaurantId) {
        SQLiteDatabase db = this.getReadableDatabase();
        double total = 0;
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_QUANTITY + ", " + COLUMN_PRICE + ", " + COLUMN_TOPPING +
                        " FROM " + TABLE_CART + " WHERE " + COLUMN_RESTAURANT_ID + "=?",
                new String[]{restaurantId});

        if (cursor.moveToFirst()) {
            do {
                int quantity = cursor.getInt(0);
                double price = cursor.getDouble(1);
                List<Map<String, Object>> toppings = jsonToToppings(cursor.getString(2));

                double toppingTotal = 0;
                for (Map<String, Object> topping : toppings) {
                    if (topping.containsKey("price")) {
                        toppingTotal += Double.parseDouble(topping.get("price").toString());
                    }
                }

                total += (price + toppingTotal) * quantity;
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return total;
    }
    // Cập nhật số lượng sản phẩm trong giỏ hàng
    // Cập nhật số lượng và topping của sản phẩm trong giỏ hàng
    public void updateCartItem(CartItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("quantity", item.getQuantity());
        values.put("topping", toppingsToJson(item.getToppings())); // Chuyển topping thành JSON trước khi lưu

        db.update("cart", values, "id = ? AND restaurant_id = ? AND topping = ?",
                new String[]{
                        String.valueOf(item.getId()),
                        String.valueOf(item.getRestaurantId()),
                        toppingsToJson(item.getToppings())
                });
        db.close();
    }


    // Xóa sản phẩm khỏi giỏ hàng
    public void removeCartItem(CartItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("cart", "id = ? AND restaurant_id = ? AND topping = ?",
                new String[]{
                        String.valueOf(item.getId()),
                        String.valueOf(item.getRestaurantId()),
                        toppingsToJson(item.getToppings())
                });
        db.close();
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
    public void clearCart(String restaurantId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, COLUMN_RESTAURANT_ID + "=?", new String[]{restaurantId});
        db.close();
    }
    public List<Map<String, Object>> getCartItemsAsMap(String restaurantId) {
        List<CartItem> cartItems = getCartItems(restaurantId);
        List<Map<String, Object>> cartItemsMap = new ArrayList<>();

        for (CartItem item : cartItems) {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("id", item.getId());
            itemMap.put("name", item.getName());
            itemMap.put("quantity", item.getQuantity());
            itemMap.put("price", item.getPrice());
            itemMap.put("imageUrl", item.getImageUrl());
            itemMap.put("toppings", item.getToppings()); // Lưu toppings dạng Map

            cartItemsMap.add(itemMap);
        }
        return cartItemsMap;
    }

}
