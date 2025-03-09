package JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Adapter.Category;
import Adapter.Food;

public class ConvertData {
    public static List<Food> convertToFoodList(List<Map<String, Object>> dishList) {
        List<Food> foodList = new ArrayList<>();
        for (Map<String, Object> dish : dishList) {
            String id = (String) dish.get("id");
            String name = (String) dish.get("name");
            double quantitySold = dish.get("quantitySold") != null ? ((Long) dish.get("quantitySold")).intValue() : 0;
            double price = dish.get("price") != null ? ((Number) dish.get("price")).doubleValue() : 0.0;
            String description = (String) dish.get("description");
            String imgUrl = (String) dish.get("image");

            Food food = new Food(id, name, quantitySold, price,imgUrl,description);
            foodList.add(food);
        }
        return foodList;
    }
    public static List<Category> convertToCategoryList(List<Map<String, Object>> categoryList) {
        List<Category> categories = new ArrayList<>();
        for (Map<String, Object> categoryData : categoryList) {
            String id = (String) categoryData.get("id");
            String name = (String) categoryData.get("name");
            List<Food> foods = new ArrayList<>();

            if (categoryData.containsKey("foods")) {
                List<Map<String, Object>> foodMaps = (List<Map<String, Object>>) categoryData.get("foods");
                foods = convertToFoodList(foodMaps);
            }

            Category category = new Category(id, name, foods);
            categories.add(category);
        }
        return categories;
    }
}
