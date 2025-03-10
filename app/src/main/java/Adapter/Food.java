package Adapter;

import java.io.Serializable;

public class Food implements Serializable {
    private String id;
    private Number price;
    private int quantitySold;
    private String name,imgUrl,description;
    public Food(String id, String name, int quantitySold, Number price, String imgUrl, String description){
        this.id = id;
        this.name = name;
        this.quantitySold = quantitySold;
        this.price = price;
        this.imgUrl = imgUrl;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(int quantitySold) {
        this.quantitySold = quantitySold;
    }

    public Number getPrice() {
        return price;
    }

    public void setPrice(Number price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
