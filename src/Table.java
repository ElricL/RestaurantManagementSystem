import java.util.ArrayList;
import java.util.HashMap;
import java.io.Serializable;

/**
 * A class that represents Table of restaurant. A Table Object contains table number, list of orders
 * and list of food to order.
 */
public class Table implements Serializable {

  private int tableNum;
  protected HashMap<String, ArrayList<Order>> orders;
  protected HashMap<String, ArrayList<Food>> foodsToOrder;
  protected boolean isOccupied;
  protected int numCustomers;

  /**
   * Constructor for Table class. Each table has its own table number. It has list of orders and
   * list of foods to order.
   *
   * @param tableNum table number
   */
  Table(int tableNum) {
    this.tableNum = tableNum;
    orders = new HashMap<>();
    foodsToOrder = new HashMap<>();

  }

  /**
   * Sets the Number of paying customers in the Table to their lists
   * of food to order and their current orders
   *
   * @param numBills the number of paying customers
   */
  String setCustomers(String numBills, String numCustomers) {
    int i;
    for (i = 1; i <= Integer.parseInt(numBills); i++) {
      foodsToOrder.put("Bill " + i, new ArrayList<>());
      orders.put("Bill " + i, new ArrayList<>());
    }
    setNumCustomers(Integer.parseInt(numCustomers));
    return "Number of customer is " + numCustomers + " and number of paying customers is " + numBills;
  }

  private void setNumCustomers(int numCustomers) {
    this.numCustomers = numCustomers;
  }

  /**
   * Get the table number.
   *
   * @return the table number
   */
  int getTableNum() {
    return tableNum;
  }

  /**
   * Add new order.
   *
   * @param order order object involved
   */
  void addOrder(Order order) {
    if (!this.orders.get(order.billId).contains(order)) {
      this.orders.get(order.billId).add(order);
    }
  }

  /**
   * Add food to the order.
   *
   * @param food food object involved
   */
  void addFoodOrder(Food food, String customerId) {
    foodsToOrder.get(customerId).add(food);
  }

  /**
   * List of foods to order.
   *
   * @return list of foods
   */
  ArrayList<Food> getFoodsToOrder() {
    ArrayList<Food> allFoods = new ArrayList<>();
    for (ArrayList<Food> foods : this.foodsToOrder.values()) {
      allFoods.addAll(foods);
    }
    return allFoods;
  }

  /**
   * Sets it to default.
   */
  void clearFoodsToOrder() {
    this.foodsToOrder = new HashMap<>();
  }

  /**
   * Get the bill.
   *
   * @return total price of the orders
   */
  double getBill(String customerId) {
    double total = 0.0;
    for (Order order : orders.get(customerId)) {
      if (order.isConfirmed()) {
        total += order.getBill();
      }
    }
    return total * 1.13; //TODO: Print out each foodname and its price and total price, etc
  }

  /**
   * Clears the table, sets order list and food to order list to default.
   */
  void clearOrders() {
    orders = new HashMap<>();
  }

  /**
   * @return true if there's pending requests
   */
  boolean hasRequests() {
    ArrayList<Food> totalList = new ArrayList<>();
    for (ArrayList<Food> list : this.foodsToOrder.values()) {
      totalList.addAll(list);
    }
    return totalList.size() > 0;
  }

  /**
   * Remove an order from table's orders
   *
   * @param order to be deleted
   */
  void removeOrder(Order order) {
    this.orders.get(order.billId).remove(order);
  }

  ArrayList<String> getPayingCustomersList() {
    ArrayList<String> customerList = new ArrayList<>(orders.keySet());
    return customerList;
  }

  public String toString() {
    return "Table " + getTableNum();
  }

  public String getReceipts() {
    StringBuilder bill = new StringBuilder();
    if (this.numCustomers >= 8) {
      bill.append("(Gratuity Tip been added)").append(String.format("%n")).append(String.format("%n"));
    }
    for (String payment : this.orders.keySet()) {
      bill.append(payment).append(String.format("%n"));
      double totalFoodPrice = 0;
      for (Order order : this.orders.get(payment)) {
        for (Food food : order.getFoods()) {
          bill.append(food).append(" ---- ").append(food.getPrice()).append(String.format("%n"));
          totalFoodPrice += food.getPrice();
        }
      }
      bill.append("Total: ").append(totalFoodPrice).append(String.format("%n")).append(String.format("%n"));
    }
    return String.valueOf(bill);
  }
}