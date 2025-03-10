package Adapter;

public class Outstanding {
    private int id;
    private String rank,nameFood,price;
    public Outstanding(int id, String rank, String nameFood, String price){
        this.id = id;
        this.rank = rank;
        this.nameFood = nameFood;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
