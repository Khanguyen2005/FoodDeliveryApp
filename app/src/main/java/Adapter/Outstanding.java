package Adapter;

public class Outstanding {
    private String id;
    private String rank,nameFood,image, restaurantId, categoryId;
    private int quantitySold;
    private Number price;

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public Outstanding(String id, String rank, String nameFood, Number price, String image, int quantitySold , String restaurantId, String categoryId){
        this.id = id;
        this.rank = rank;
        this.nameFood = nameFood;
        this.price = price;
        this.image = image;
        this.quantitySold = quantitySold;
        this.restaurantId = restaurantId;
        this.categoryId = categoryId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getNameFood() {
        return nameFood;
    }

    public void setNameFood(String nameFood) {
        this.nameFood = nameFood;
    }

    public Number getPrice() {
        return price;
    }

    public void setPrice(Number price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(int quantitySold) {
        this.quantitySold = quantitySold;
    }
}
