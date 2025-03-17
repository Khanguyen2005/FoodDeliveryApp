package Adapter;

import java.util.List;

public class OrderModel {
    private String userId;
    private String restaurantId;  // ⚡ Giữ lại ID của nhà hàng
    private String restaurantName; // ⚡ Lưu thêm tên nhà hàng
    private double totalPrice;
    private List<OrderItemModel> items;

    public OrderModel() {
        // Constructor rỗng cho Firebase
    }

    public OrderModel(String userId, String restaurantId, String restaurantName, double totalPrice, List<OrderItemModel> items) {
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.totalPrice = totalPrice;
        this.items = items;
    }

    public String getUserId() { return userId; }
    public String getRestaurantId() { return restaurantId; }  // ⚡ Getter cho ID
    public String getRestaurantName() { return restaurantName; } // ⚡ Getter cho tên
    public double getTotalPrice() { return totalPrice; }
    public List<OrderItemModel> getItems() { return items; }
}
