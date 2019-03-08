import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class representation of menu. A restaurant has a default menu set that the customers can order
 * from. A menu holds a map of available food to its price, map of ingredient to its price, map of
 * food to list of ingredients and map of name of food to the food object.
 */
public class Menu implements Serializable {
  protected HashMap<String, Double> ingredientToPrice;
  private HashMap<String, Food> foodMap; // add name to food lis
  public ArrayList<Food> specialFood;
  public ArrayList<Food> foodList;

  /** Constructor for menu. */
  Menu() {
    this.ingredientToPrice = new HashMap<>();
    this.foodMap = new HashMap<>();
    this.specialFood = new ArrayList<>();
    this.foodList = new ArrayList<>();
  }

  /**
   * Adds food object to this menu.
   *
   * @param food food object to be added to menu
   */
  void addFoodMenu(Food food) {
    this.foodMap.put(food.getName(), food);
    this.foodList.add(food);
  }

  /**
   * Registers the price of an ingredient into the menu.
   *
   * @param ingredient ingredient
   * @param price price to be set
   */
  void setIngredientToPrice(String ingredient, double price) {
    ingredientToPrice.put(ingredient, price);
  }

  /**
   * Returns the food object that is associated with the foodName.
   *
   * @param foodName name of food
   * @return food object associated with foodName
   */
  Food getFood(String foodName) { // need to obtain food from menu
    return this.foodMap.get(foodName);
  }

  /**
   * Checks if this menu has the ingredient, and then return its price.
   *
   * @param ingredient ingredient
   * @return the price of ingredient
   */
  double getIngredientPrice(String ingredient) {
    if (hasIngredient(ingredient)) {
      return ingredientToPrice.get(ingredient);
    } else {
      return 0; // 0 value returned
    }
  }

  /**
   * Checks if this menu has this ingredient.
   *
   * @param ingredient name of ingredient to be checked
   * @return true if ingredient is in menu
   */
  boolean hasIngredient(String ingredient) {
    return ingredientToPrice.containsKey(ingredient);
  }

  /**
   * Checks if this menu has this food.
   *
   * @param foodName name of food to be checked
   * @return true if food is in menu
   */
  boolean hasFood(String foodName) {
    return foodMap.containsKey(foodName);
  }

  ArrayList<String> getFoodNameList() {
    ArrayList<String> foodNameList = new ArrayList<>();
    for(String foodName: foodMap.keySet()) {
      foodNameList.add(foodMap.get(foodName).toString());
    }
    return foodNameList;
  }

  /**
   * Get the special food
   * @return the String representation of Special food
   */
  String getSpecialFood() {
    StringBuilder out = new StringBuilder();
    for (Food food:specialFood) {
      out.append(food.getName());
      out.append("\n");
    }
    return out.toString();
  }

  /**
   * A Method to generate the Menu
   * @return String representation of Menu
   */
  public String generateMenu() {
    StringBuilder menuStr = new StringBuilder();
    menuStr.append("---------------------------------- Menu -----------------------------------").append(
            String.format("%n"));
    menuStr.append("--------------------- Regular Menu ----------------------").append(
            String.format("%n"));
    for (Food food: this.foodList) {
      menuStr.append(food.getName()).append("----").append(food.getPrice()).append(String.format("%n"));
    }

    menuStr.append("--------------------- Discount Menu ----------------------").append(
            String.format("%n"));
    for (Food food: this.specialFood) {
      menuStr.append(food.getName()).append("----").append(food.getPrice()).append(String.format("%n"));
    }
    menuStr.append("---------------------------- End of Menu ---------------------------------");
    return menuStr.toString();
  }

}