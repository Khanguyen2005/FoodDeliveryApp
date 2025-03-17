package Adapter;

import java.util.List;

public class OrderModel {
    private String userId;
    private String restaurantId;
    private double totalPrice;
    private List<OrderItemModel> items; // ⚡ Thay đổi kiểu dữ liệu của items

    public OrderModel() {
        // Constructor rỗng cho Firebase
    }

    public OrderModel(String userId, String restaurantId, double totalPrice, List<OrderItemModel> items) {
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.totalPrice = totalPrice;
        this.items = items;
    }

    public String getUserId() { return userId; }
    public String getRestaurantId() { return restaurantId; }
    public double getTotalPrice() { return totalPrice; }
    public List<OrderItemModel> getItems() { return items; }
}
