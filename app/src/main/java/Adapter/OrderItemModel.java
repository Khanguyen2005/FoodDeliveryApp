package Adapter;

import java.util.List;
import java.util.Map;

public class OrderItemModel {
    private String name;
    private String imageUrl;
    private int quantity;
    private double price;
    private List<Map<String, Object>> toppings;

    public OrderItemModel() {
        // Constructor rỗng cho Firebase
    }

    public OrderItemModel(String name, String imageUrl, int quantity, double price, List<Map<String, Object>> toppings) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
        this.price = price;
        this.toppings = toppings;
    }

    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public List<Map<String, Object>> getToppings() { return toppings; }
}
