package Adapter;

public class Restaurant {
    private String id;
    private String name;
    private Number starRes,evaluateRes;
    private String imageUrl,backgroundImg;

    public Restaurant() {
    }

    public Restaurant(String id, String name, Number starRes, Number evaluateRes, String imageUrl, String backgroundImg) {
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

    public Number getStarRes() {
        return starRes == null  ? 5.0 : starRes;
    }


    public void setStarRes(Number starRes) {
        this.starRes = starRes;
    }

    public Number getEvaluateRes() {
        return evaluateRes;
    }

    public void setEvaluateRes(Number evaluateRes) {
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
