import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A class that represents Manager of a restaurant. Manager is responsible for restocking the
 * kitchen inventory. Manager can read the requests for ingredients made by cook, generate list of
 * kitchen inventory and change the threshold of a certain ingredient.
 */
class Manager extends Employee implements Serializable {

  private Menu menu;

  /**
   * Constructor for Manager class. Each manager has their own id. Manager has an access to the
   * kitchen and menu. Manager can also read the Request.txt to see the requests made by the cook.
   *
   * @param id manager id
   * @param kitchen kitchen associated with the restaurant
   */

  Manager(String id, Kitchen kitchen, Menu menu) {
    super(id, kitchen);
    setJob("Manager");
    setType("Not Available");
    this.menu = menu;
  }

  /**
   * Method for manager to change the threshold of any given ingredient. An ingredient can have any
   * threshold in the kitchen inventory. Manager can change the value.
   *
   * @param ingredient the ingredient involved
   * @param threshold the new threshold for the ingredient
   */
  public String changeThreshold(String ingredient, String threshold) {
    if (threshold.chars().allMatch( Character::isDigit )) {
      Integer oldThreshold = getKitchen().getThreshold(ingredient);
      ArrayList<Integer> ingredientInfo = new ArrayList<>();
      ingredientInfo.add(getKitchen().getQuantity(ingredient));
      ingredientInfo.add(Integer.parseInt(threshold));
      getKitchen().getIngredients().replace(ingredient, ingredientInfo);
      String change = toString()
              + " changed threshold of "
              + ingredient
              + " from "
              + oldThreshold
              + " to "
              + threshold;
      RestaurantLog.entry("Fine", change + String.format("%n"));
      return change;
    }
    String err = "Cannot Change Treshold: Please enter an integer number.";
    RestaurantLog.entry("Warning", err + String.format("%n"));
    return err;
  }

  /**
   * Manager is responsible to manage kitchen inventory restock. An ingredient is restocked for 20
   * in quantity by default.
   *
   * @param ingredient the ingredient to restock
   */
  String restockInventory(String ingredient) {
    if (getKitchen().getIngredients().containsKey(ingredient)) {
      Integer oldQuantity = getKitchen().getQuantity(ingredient);
      Integer newQuantity = oldQuantity + 20;
      ArrayList<Integer> ingredientInfo = new ArrayList<>();
      ingredientInfo.add(newQuantity);
      ingredientInfo.add(getKitchen().getThreshold(ingredient));
      getKitchen().getIngredients().replace(ingredient, ingredientInfo);
      String restock =
              toString()
                      + " restocked "
                      + ingredient
                      + " from "
                      + oldQuantity
                      + " to "
                      + newQuantity;
      RestaurantLog.entry("Fine", restock + String.format("%n"));
      return restock;
    } else {
      ArrayList<Integer> ingredientInfo = new ArrayList<>();
      ingredientInfo.add(20);
      ingredientInfo.add(20);
      getKitchen().getIngredients().put(ingredient, ingredientInfo);

      String newIngredient = toString() + " stocked " + ingredient + " to " + 20;
      RestaurantLog.entry("Fine", newIngredient + String.format("%n"));
      return newIngredient;
    }
  }

  /**
   * This method is for manager to restock an ingredient in a specific quantity instead of the
   * default 20.
   *
   * @param ingredient ingredient to restock
   * @param quantity desired quantity
   */
  String restockInventory(String ingredient, String quantity) {
    if (quantity.chars().allMatch( Character::isDigit )) {

      if (getKitchen().getIngredients().containsKey(ingredient)) {
        int oldQuantity = getKitchen().getQuantity(ingredient);
        int newQuantity = oldQuantity + Integer.parseInt(quantity);
        ArrayList<Integer> ingredientInfo = new ArrayList<>();
        ingredientInfo.add(newQuantity);
        ingredientInfo.add(getKitchen().getThreshold(ingredient));
        getKitchen().getIngredients().replace(ingredient, ingredientInfo);
        String restock =
                toString()
                        + " restocked "
                        + ingredient
                        + " from "
                        + oldQuantity
                        + " to "
                        + newQuantity;
        RestaurantLog.entry("Fine", restock + String.format("%n"));
        return restock;
      } else {
        ArrayList<Integer> ingredientInfo = new ArrayList<>();
        ingredientInfo.add(Integer.parseInt(quantity));
        ingredientInfo.add(Integer.parseInt(quantity));
        getKitchen().getIngredients().put(ingredient, ingredientInfo);
        String restock = toString() + " stocked " + ingredient + " to " + quantity;
        RestaurantLog.entry("Fine", restock + String.format("%n"));
        return restock;
      }
    }
      String err = "Cannot Change Treshold: Please enter an integer number.";
      RestaurantLog.entry("Warning", err + String.format("%n"));
      return err;
  }

  /**
   * Method to generate a list of the kitchen inventory. Prints out each ingredient with its
   * quantity.
   */
  String generateInventory() {
    StringBuilder out = new StringBuilder();
    out.append("---------------------------Kitchen Inventory-----------------------------");
    out.append(System.lineSeparator());

    for (String ingredient : getKitchen().getIngredients().keySet()) {
      Integer quantity = getKitchen().getQuantity(ingredient);
      Integer threshold = getKitchen().getThreshold(ingredient);
      out.append(System.lineSeparator());
      String result = ingredient + ": quantity = " + quantity + ", threshold = " + threshold;
      out.append(result);
    }
    out.append(System.lineSeparator());
    out.append("----------------------------------------------------------");
    return out.toString();
  }

  /**
   * This method reads the requests made by the cook. It prints to the screen so the manager can
   * decide which ingredient to restock.
   */
  String readRequests() {
    StringBuilder out = new StringBuilder();
    try (BufferedReader fileReader = new BufferedReader(new FileReader("Request.txt"))) {
      String request = fileReader.readLine();
      while (request != null) {
        out.append(request);
        out.append("\n");
        request = fileReader.readLine();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return out.toString();
  }

  /**
   * Method that updates the menu with special food list
   * @param food food that needs to checked and updated
   */
  void updateSpecialFood(Food food){
    boolean containsFood = this.menu.specialFood.contains(food);
    if(!containsFood){
      HashMap<String, Integer> ingredientsToQuantity = new HashMap<>();
      ArrayList<String> foodIngredients = food.getIngredients();
      for (String ingredient: foodIngredients) {
        if (ingredientsToQuantity.containsKey(ingredient)) {
          int curQuantity = ingredientsToQuantity.get(ingredient);
          curQuantity += 1;
          ingredientsToQuantity.put(ingredient, curQuantity);
        }
        else {
        ingredientsToQuantity.put(ingredient, 1);
        }
      //Now we have mapping of how many of each ingredient is needed.
      }

      boolean overStocked = true;
      int i = 0;
      while (overStocked && i < foodIngredients.size()) {
        String curFoodIngredient = foodIngredients.get(i);
        int foodNeed = ingredientsToQuantity.get(curFoodIngredient);
        int kitchenHas = this.getKitchen().getQuantity(curFoodIngredient);
        if (kitchenHas/foodNeed < 30) {
          overStocked = false;
        }
        i += 1;
      }

      if (overStocked) {
        changeFoodPrice(food);
        this.menu.specialFood.add(food);
        this.menu.foodList.remove(food);
      }
      else {
      RestaurantLog.entry("Warning", "One or more of the ingredients is not overstocked"
              + String.format("%n"));
      }
    }
  }

  /**
   * Method to change the price of special food after discount
   * @param food food for which price needs to be changed
   */
  void changeFoodPrice(Food food){
    boolean containsFood = this.menu.specialFood.contains(food);
    if(containsFood == false && food.checkIsDiscounted() == false){
      food.setIsDiscounted();
      food.setPrice(food.getDiscountedPrice());
        RestaurantLog.entry("Fine", "The new price of the special food " + food + " is "
                +  food.getDiscountedPrice() + String.format("%n"));
    }
    else{
        RestaurantLog.entry("Warning", food + " is not a special food" + String.format("%n"));
    }
  }

  /**
   * Method to generate a list of foods with its original price and price after discount
   */
  void generateSpecialFood(){
      RestaurantLog.entry("Info", "====================Special Offer====================");
    for (Food curFood : menu.specialFood){
      double beforePrice = curFood.getPrice();
      double afterPrice = curFood.getDiscountedPrice();

      RestaurantLog.entry("Fine", curFood.getName() + "changed price from " + beforePrice + " to "
                + afterPrice + String.format("%n"));
    }
      RestaurantLog.entry("Info", "======================================================");

  }

  /**
   * Checks if there are any pending orders
   * @return if there are no pending orders
   */
  String seeOrders() {
    StringBuilder out = new StringBuilder();
    for (Order order: getKitchen().getOrderList()) {
      if (order.isFilled() && !order.isDelivered()) {
        out.append(order.toString());
        out.append("\n");
      }
    }
    if (out.toString().equals("")) {
      out.append("There are no pending orders.");
    }
    return out.toString();
  }

  /**
   * Returns the string representation of a Manager
   * @return String representation of Manager and their id
   */
  public String toString() {
    return "Manager " + getId();
  }
}


