package Adapter;

public class CartItem {
    private int id;
    private String name;
    private int quantity;
    private double price;
    private String restaurantId;
    private String imageUrl; // Hình ảnh món ăn
    private String topping; // Topping đã chọn (có thể để trống)


    public CartItem(int id, String name, int quantity, double price, String restaurantId, String imageUrl, String topping) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.restaurantId = restaurantId;
        this.imageUrl = imageUrl;
        this.topping = topping;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTopping() {
        return topping;
    }

    public void setTopping(String topping) {
        this.topping = topping;
    }

    public double getTotalPrice() {
        return quantity * price;
    }
}
