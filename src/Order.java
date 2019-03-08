import java.util.ArrayList;
import java.io.Serializable;

/**
 * A class that represents an order. When the server has finalized an order, server will create an
 * order to be sent to the kitchen. An order can consist of multiple food item.
 */
public class Order implements Serializable {
  private static int numOrders = 1; // add static counter for id of Order
  private int id;
  private ArrayList<Food> foods;
  private int tableNum;
  private boolean isFilled;
  private boolean isDelivered;
  private boolean isConfirmed;
  private ArrayList<String> seenCooks;
  protected String billId; // Customer who made the Order

  /**
   * Constructor for an order. An order is associated to a table with tableNum.
   *
   * @param foods list of foods in this order
   * @param tableNum table number of the table associated with this order
   */
  Order(ArrayList<Food> foods, int tableNum, String billId) {
    this.foods = foods;
    this.id = numOrders;
    numOrders += 1;
    this.tableNum = tableNum;
    this.isDelivered = false;
    this.isFilled = false;
    this.isConfirmed = false;
    this.seenCooks = new ArrayList<>();
    this.billId = billId;
  }

  /**
   * Deletes food from this order's list of food.
   *
   * @param foodToDelete food object to delete
   */
  void deleteFood(Food foodToDelete) {
    for (Food food : foods) {
      if (food.equals(foodToDelete)) {
        foods.remove(food);
        break;
      }
    }
  }

  /**
   * Get the total bill of this order including the prices of all the food in the list.
   *
   * @return the total bill of this order
   */
  double getBill() {
    double sumBill = 0;
    for (Food currentFood : this.foods) {
      sumBill += currentFood.getPrice();
    }
    return sumBill;
  }

  /**
   * Get the table number associated with this order.
   *
   * @return the table number
   */
  int getTableNum() {
    return this.tableNum;
  }


  public ArrayList<String> getSeenCooks() {
    return this.seenCooks;
  }

  /**
   * Get the id associated with this order.
   *
   * @return the id
   */
  public int getId() {
    return this.id;
  }

  /**
   * Checks if this order has this food in its list.
   *
   * @param food food object to be checked
   * @return true if this order contains this food
   */
  boolean hasFood(Food food) {
    for (Food foodOrder : foods) {
      if (food.equals(foodOrder)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Get the list of foods this order currently has.
   *
   * @return list of foods
   */
  ArrayList<Food> getFoods() {
    return foods;
  }

  /**
   * Checks if this order has been seen by cook.
   *
   * @return true if cook has seen this order
   */
  boolean hasSeen(String cookId) {
    return this.seenCooks.contains(cookId);
  }

  /** Registers that this order has been seen by the cook. */
  void setHasSeen(String cookId) {
    this.seenCooks.add(cookId);
  }

  /**
   * Order is filled iff every food item in its list has been filled.
   *
   * @return true if order has been filled
   */
  boolean isFilled() {
    return isFilled;
  }

  /** Registers that this order has been filled. */
  void setFilled() {
    isFilled = true;
  }

  /**
   * Checks if the order has been delivered to its associated table.
   *
   * @return true if the order has been delivered
   */
  boolean isDelivered() {
    return isDelivered;
  }

  /**
   * Register that the order has been delivered to the table by the server after it has been filled.
   */
  void setDelivered() {
    isDelivered = true;
  }

  /**
   * Checks if the order made has been confirmed by the server.
   *
   * @return true if order is confirmed
   */
  boolean isConfirmed() {
    return isConfirmed;
  }

  /** Registers that the order has been confirmed by the server. */
  void setConfirmed() {
    isConfirmed = true;
  }

  /**
   * Return if an identical Food is unready or not.
   *
   * @param foodToCheck The food to be checked if unready
   * @return boolean
   */
  boolean checkFoodUnready(Food foodToCheck) {
    for (Food food : foods) {
      if (food.equals(foodToCheck)) {
        return !food.getReady();
      }
    }
    return false;
  }

  /**
   * The String representation of Order
   * @return the String representation of Order
   */
  public String toString() {
    return "Order " + getId() + " from Table " + getTableNum();
  }

  /**
   * Get the food list
   * @return the String representation of food list
   */
  public String getFoodList() {
    StringBuilder out = new StringBuilder();
    for (Food food : foods) {
      out.append(food.getName());
      out.append("\n");
    }
    return out.toString();
  }

  /**
   * Return true iff the Order equals the food type
   * @param type type of Food
   * @return Return true the food type equals the order
   */
  public Boolean containsType(String type) {
    for (Food food: foods) {
      if (food.getType().equals(type)) {
        return true;
      }
    }
    return false;
  }
}