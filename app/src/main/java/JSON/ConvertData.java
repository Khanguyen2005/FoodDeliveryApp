package JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Adapter.Category;
import Adapter.Food;
import Adapter.Outstanding;
import Adapter.Restaurant;

public class ConvertData {
    public static List<Food> convertToFoodList(List<Map<String, Object>> dishList) {
        List<Food> foodList = new ArrayList<>();
        for (Map<String, Object> dish : dishList) {
            String id = (String) dish.get("id");
            String name = (String) dish.get("name");
            int quantitySold = dish.get("quantitySold") != null ? ((Long) dish.get("quantitySold")).intValue() : 0;
            double price = dish.get("price") != null ? ((Number) dish.get("price")).doubleValue() : 0.0;
            String description = (String) dish.get("description");
            String imgUrl = (String) dish.get("image");

            Food food = new Food(id, name, quantitySold, price,imgUrl,description);
            foodList.add(food);
        }
        return foodList;
    }
    public static List<Outstanding> convertToOutstandingList(List<Map<String, Object>> dishList) {
        List<Outstanding> outstandingsList = new ArrayList<>();
        for (Map<String, Object> dish : dishList) {
            String id = (String) dish.get("id");
            String name = (String) dish.get("name");
            int quantitySold = dish.get("quantitySold") != null ? ((Long) dish.get("quantitySold")).intValue() : 0;
            double price = dish.get("price") != null ? ((Number) dish.get("price")).doubleValue() : 0.0;
            String imgUrl = (String) dish.get("image");
            String rank = (String) dish.get("rank");

            Outstanding outstanding = new Outstanding(id, rank, name, price, imgUrl, quantitySold);
            outstandingsList.add(outstanding);
        }
        return outstandingsList;
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
    public static List<Restaurant> convertToRestaurantList(List<Map<String, Object>> restaurantList) {
        List<Restaurant> restaurants = new ArrayList<>();
        for (Map<String, Object> restaurantData : restaurantList) {
            String id = (String) restaurantData.get("id");
            String name = (String) restaurantData.get("name");
            Number starRes = restaurantData.get("starRes") != null ? Double.parseDouble(restaurantData.get("starRes").toString()) : 5.0;
            Number evaluate = restaurantData.get("evaluate") != null ? Integer.parseInt(restaurantData.get("evaluate").toString()) : 5.0;
            String imageUrl = (String) restaurantData.get("imageUrl");
            String backgroundImageUrl = (String) restaurantData.get("backgroundImageUrl");

            Restaurant restaurant = new Restaurant(id, name, starRes, evaluate,imageUrl, backgroundImageUrl);
            restaurants.add(restaurant);
        }
        return restaurants;
    }

}
