package JSON;

import com.google.firebase.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Adapter.Category;
import Adapter.Discount;
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
            String restaurantId = (String) dish.get("restaurantId"); // Anh Khá thêm
            String categoryId = (String) dish.get("categoryId"); // Anh Khá thêm

            Outstanding outstanding = new Outstanding(id, rank, name, price, imgUrl, quantitySold, restaurantId, categoryId);
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
    public static List<Discount> convertToDiscountList(List<Map<String, Object>> discountList) {
        List<Discount> discounts = new ArrayList<>();

        for (Map<String, Object> discountData : discountList) {
            String discountId = (String) discountData.get("discountId");
            String code = (String) discountData.get("code");
            String description = (String) discountData.get("description");
            String type = (String) discountData.get("type");
            Number value = discountData.get("value") != null ? (Number) discountData.get("value") : 0;
            Number minOrder = discountData.get("min_order") != null ? (Number) discountData.get("min_order") : 0;
            Timestamp startDate = discountData.get("start_date") instanceof Timestamp ? (Timestamp) discountData.get("start_date") : null;
            Timestamp endDate = discountData.get("end_date") instanceof Timestamp ? (Timestamp) discountData.get("end_date") : null;
            Discount discount = new Discount(discountId, code, description, type, startDate, endDate, minOrder, value);
            discounts.add(discount);
        }
        return discounts;
    }
}
