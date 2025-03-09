package Adapter;
import java.util.List;
public class Category {
    private String name,id;
    private List<Food> listFood;
    public Category(String id,String name, List<Food> listFood){
        this.id = id;
        this.name = name;
        this.listFood = listFood;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Food> getListFood() {
        return listFood;
    }

    public void setListFood(List<Food> listFood) {
        this.listFood = listFood;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
