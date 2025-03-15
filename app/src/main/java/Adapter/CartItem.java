package Adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartItem {
    private int id;
    private String name;
    private int quantity;
    private double price;
    private String restaurantId;
    private String imageUrl;
    private List<Map<String, Object>> toppings; // Danh sách topping dạng Map

    // Constructor
    public CartItem(int id, String name, int quantity, double price, String restaurantId, String imageUrl, List<Map<String, Object>> toppings) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.restaurantId = restaurantId;
        this.imageUrl = imageUrl;
        this.toppings = toppings != null ? toppings : new ArrayList<>();
    }

    // Getters và Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getRestaurantId() { return restaurantId; }
    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public List<Map<String, Object>> getToppings() { return toppings; }
    public void setToppings(List<Map<String, Object>> toppings) { this.toppings = toppings; }

    // Tính tổng tiền (bao gồm cả topping)
    public double getTotalPrice() {
        double toppingTotal = 0;
        for (Map<String, Object> topping : toppings) {
            toppingTotal += (double) topping.get("price");
        }
        return quantity * (price + toppingTotal);
    }

    // Chuyển CartItem thành Map để lưu vào SQLite hoặc Firebase
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("name", name);
        map.put("quantity", quantity);
        map.put("price", price);
        map.put("restaurantId", restaurantId);
        map.put("imageUrl", imageUrl);
        map.put("toppings", toppingsToJson());
        return map;
    }

    // Chuyển danh sách toppings thành chuỗi JSON
    private String toppingsToJson() {
        JSONArray jsonArray = new JSONArray();
        for (Map<String, Object> topping : toppings) {
            JSONObject jsonObject = new JSONObject(topping);
            jsonArray.put(jsonObject);
        }
        return jsonArray.toString();
    }

    // Chuyển chuỗi JSON thành danh sách Map
    public static List<Map<String, Object>> jsonToToppings(String json) {
        List<Map<String, Object>> toppings = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Map<String, Object> toppingMap = new HashMap<>();
                toppingMap.put("name", jsonObject.getString("name"));
                toppingMap.put("price", jsonObject.getDouble("price"));
                toppings.add(toppingMap);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return toppings;
    }
}