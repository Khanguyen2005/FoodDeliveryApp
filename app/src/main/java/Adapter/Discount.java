package Adapter;


import com.google.firebase.Timestamp;

public class Discount {
    private String id, code, description,type;
    private Timestamp startDate, endDate;
    private Number minOrder,value;

    public Discount(String id, String code, String description, String type, Timestamp startDate, Timestamp endDate, Number minOrder, Number value){
        this.id = id;
        this.code = code;
        this.description = description;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.minOrder = minOrder;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public Number getMinOrder() {
        return minOrder;
    }

    public void setMinOrder(Number minOrder) {
        this.minOrder = minOrder;
    }

    public Number getValue() {
        return value;
    }

    public void setValue(Number value) {
        this.value = value;
    }
}
