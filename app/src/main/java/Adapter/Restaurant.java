package Adapter;

public class Restaurant {
    private String id;
    private String name;
    private String starRes;
    private String evaluateRes;
    private String imageUrl,backgroundImg;

    public Restaurant() {
    }

    public Restaurant(String id, String name, String starRes, String evaluateRes, String imageUrl, String backgroundImg) {
        this.id = id;
        this.name = name;
        this.starRes = starRes;
        this.evaluateRes = evaluateRes;
        this.imageUrl = imageUrl;
        this.backgroundImg = backgroundImg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStarRes() {
        return starRes;
    }

    public void setStarRes(String starRes) {
        this.starRes = starRes;
    }

    public String getEvaluateRes() {
        return evaluateRes;
    }

    public void setEvaluateRes(String evaluateRes) {
        this.evaluateRes = evaluateRes;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBackgroundImg() {
        return backgroundImg;
    }

    public void setBackgroundImg(String backgroundImg) {
        this.backgroundImg = backgroundImg;
    }
}
