import java.util.ArrayList;
import java.util.Objects;
import java.io.Serializable;

/**
 * A class that represents a server in a restaurant. Server is responsible to record orders made by
 * a table, sends them to the kitchen, and retrieve the food from the kitchen and sends it to the
 * respective table. A server is also expected to handle a returned order from a table. When the
 * customer has left, the server is responsible to clear the table.
 */
public class Server extends Employee implements Serializable {

  private Menu menu;
  protected static double totalBills;
  private ArrayList<Order> assignedOrders;


  /*
   * Constructor for a server class. Each server has their own id. Server has access to the kitchen
   * and menu. Server also has a list of orders assigned to them.
   *
   * @param id server id
   * @param kitchen kitchen associated with the restaurant
   * @param menu menu associated with the restaurant
   */
  Server(String id, Kitchen kitchen, Menu menu) {
    super(id, kitchen);
    setJob("Server");
    setType("Not Available");
    this.menu = menu;
    this.assignedOrders = new ArrayList<>();
  }

  public ArrayList<Order> getAssignedOrders() {
    return assignedOrders;
  }

  /**
   * Sets table occupancy and amount of paying customers
   *
   * @param table    the table to be set occupied
   * @param numBills the number of paying customers
   */
  String fillTable(Table table, String numBills, String numCustomers) {
    String status;
    if (!numBills.chars().allMatch(Character::isDigit) && !numCustomers.chars().allMatch(Character::isDigit)) {
      return "Please enter the right input.";
    } else if (Integer.parseInt(numBills) > Integer.parseInt(numCustomers)) {
      return "Please enter proper number of paying customers.";
    } else if (table.isOccupied) {
      status = String.format("Table %d is already occupied%n", table.getTableNum());
      RestaurantLog.entry("warning", status + String.format("%n"));
      return status;
    } else {
      status = table.setCustomers(numBills, numCustomers);
      RestaurantLog.entry("Fine", status + String.format("%n"));
      table.isOccupied = true;
      return status;
    }
  }

  /**
   * Records a food item order made from a table. The order consist of any subtraction or addition
   * of ingredient made by the table. If the order has been recorded, this function will return
   * true.
   *
   * @param table table associated with this food order
   * @param food  food to be ordered
   * @return true if order has been recorded
   */
  Boolean recordFoodOrder(Table table, Food food, String billId) {
    // need menu to return if food name is in menu
    if (table.isOccupied && table.foodsToOrder.keySet().contains(billId)) {
      if (!ServerHelper.checkOrdersReady(this.assignedOrders)) {
        if (menu.hasFood(food.getName())) {
          if (getKitchen().isEnoughIngredients(food)) {
            table.addFoodOrder(food, billId);
            RestaurantLog.entry("Fine", String.format(
                    "Server %s records an order of %s, " + "from Table %d%n",
                    getId(), food, table.getTableNum()));
            return true;
          } else {
            RestaurantLog.entry("warning", String.format("Cannot record food Order: " +
                    "Not enough ingredients for %s%n", food));
            return false;
          }
        } else {
          RestaurantLog.entry("warning", String.format("Cannot record food Order: %s not in menu. %n", food));
          return false;
        }
      } else {
        RestaurantLog.entry("warning", String.format("Cannot record food Order: " +
                "There pending Order(s) to be delivered. %n"));
        return false;
      }
    } else {
      RestaurantLog.entry("warning", String.format("Cannot record food Order: " +
              "Table is unoccupied or Bill does not exist.%n"));
      return false;
    }
  }

  /**
   * Deleted food from the order once the bill is prepared
   *
   * @param table  table that is occupied
   * @param food   food that was ordered
   * @param billId id of the bill that contains orders
   */
  void deleteFoodOrder(
          Table table, Food food, String billId) {
    if (table.isOccupied && table.foodsToOrder.keySet().contains(billId)) {
      if (table.foodsToOrder.get(billId).contains(food)) {
        table.foodsToOrder.get(billId).remove(food);
        for (String ingredient : food.getIngredients()) {
          getKitchen().addIngredients(ingredient, 1, getKitchen().getThreshold(ingredient));
        }
        RestaurantLog.entry("Fine", String.format("%s is deleted from %s's requests.%n", food, billId));
      } else {
        RestaurantLog.entry("warning", String.format("Cannot delete food Order: " +
                "%s is not in %s's requests.%n", food, billId));
      }
    } else {
      RestaurantLog.entry("warning", String.format("Cannot delete food Order: " +
              "Table is unoccupied or Bill does not exist.%n"));
    }
  }

  /**
   * Place order made by the table.
   *
   * @param table table associated with the table
   */
  String placeOrder(Table table) {
    String status;
    if (table.hasRequests()) {
      if (ServerHelper.checkOrdersReady(this.assignedOrders)) {
        status = String.format("Cannot place Order for Table %d: There's " +
                "pending Order(s) to be delivered %n", table.getTableNum());
        RestaurantLog.entry("warning", status);
        return status;
      } else {
        for (String bill : table.foodsToOrder.keySet()) {
          if (table.foodsToOrder.get(bill).size() > 0) {
            Order newOrder = new Order(table.foodsToOrder.get(bill), table.getTableNum(), bill);
            table.addOrder(newOrder);
            this.assignedOrders.add(newOrder);
            getKitchen().getOrderList().add(newOrder);
            table.foodsToOrder.put(bill, new ArrayList<>());
          }
        }
        status = String.format(
                "Server %s places final Orders from Table %d%n", getId(), table.getTableNum());
        RestaurantLog.entry("Fine", status);
        return status;
      }
    } else {
      status = String.format(
              "Cannot place Order for Table %d: Table has no pending requests %n", table.getTableNum());
      RestaurantLog.entry("warning", status);
      return status;
    }
  }

  /**
   * Delivers an order to the table. If an order has not been completely filled, it will not be
   * delivered.
   *
   * @param orderNum number of the order
   * @param table    table associated with the order
   */
  String deliverOrder(int orderNum, Table table) {
    String status;
    if (ServerHelper.isAssigned(orderNum, this.assignedOrders)) {
      Order delivery = ServerHelper.getAssigned(orderNum, this.assignedOrders);
      if (delivery != null) {

        if (delivery.isDelivered()) {
          status = String.format(
                  "Cannot deliver Order %d: Order has already been delivered. %n", orderNum);
          RestaurantLog.entry("warning", status);
          return status;

        } else if (!delivery.isFilled()) {
          return String.format("Cannot deliver Order %d: not ready yet. %n", orderNum);

        } else if (!(table.getTableNum() == delivery.getTableNum())) {
          status = String.format(
                  "Cannot deliver Order %d: Order not placed by " + "Table %d. %n",
                  orderNum, table.getTableNum());
          RestaurantLog.entry("warning", status);
          return status;

        } else if (delivery.isFilled()) {
          table.addOrder(delivery);
          delivery.setDelivered();
          getKitchen().getOrderList().remove(delivery);
          status = String.format(
                  "Server %s delivered Order %d to Table %d%n", getId(), orderNum, table.getTableNum());
          RestaurantLog.entry("Fine", status);
          return status;
        }
      }
    } else {
      status = String.format(
              "Cannot deliver Order %d: " + "does not exist. %n",
              orderNum);
      RestaurantLog.entry("warning", status);
      return status;
    }
    status = String.format(
            "Cannot deliver Order %d: " + "not assigned to Server %s%n",
            orderNum, getId());
    RestaurantLog.entry("warning", status);
    return status;
  }

  /**
   * Returns a food item as requested by the customer associated with the table. Server will check
   * if the food has actually been delivered to the table or not. Server will also check if there's
   * enough ingredient to make another instance of the food item. If the server successfully
   * returned the order, this method will return true.
   *
   * @param orderNum order number associated with this food and table
   * @param table    table associated with this order
   * @param food     food to be returned
   * @return true if server successfully returned this food item
   */
  String returnOrder(int orderNum, Table table, Food food) {
    Order order = ServerHelper.getAssigned(orderNum, this.assignedOrders);
    String status;
    if (order != null) {
      if (!ServerHelper.foodReturnCheck(table, food, orderNum, getId(), this.assignedOrders)) {
        return "Cannot return food";
      } else if (!order.isDelivered()) {
        status = String.format("Cannot return Order %d: not been delivered %n", orderNum);
        RestaurantLog.entry("warning", status);
        return status;
      } else if (!returnRecord(food, table, order.billId)) {
        status = String.format(
                "Cannot return Order %d: not enough ingredients for %s%n", orderNum, food);
        RestaurantLog.entry("warning", status);
        return status;
      } else {
        returnPlace(table);
        return "Food item is returned.";
      }
    }
    status = String.format("Cannot return Order %d: not assigned to Server ", orderNum) +
            getId() + String.format("%n");
    RestaurantLog.entry("warning", status);
    return status;
  }

  /**
   * Removes a requested item if it been ordered and has not been prepared.
   *
   * @param orderNum   The corresponding Order's number
   * @param foodCancel the requested item to be removed
   * @param table      the table that requesting removal of item
   */
  Boolean cancelFoodItem(int orderNum, Food foodCancel, Table table) {
    if (ServerHelper.isAssigned(orderNum, this.assignedOrders)) {
      Order order = ServerHelper.getAssigned(orderNum, this.assignedOrders);
      if (!(Objects.requireNonNull(order).getTableNum() == table.getTableNum())) {
        RestaurantLog.entry("warning", String.format("Cannot cancel food in Order %d: Wrong Table %n", orderNum));
        return false;
      } else if (!order.checkFoodUnready(foodCancel)) {
        RestaurantLog.entry("warning", String.format("Cannot cancel food in Order %d: " +
                "%s has already been prepared/does not exist %n", orderNum, foodCancel.getName()));
        return false;
      } else {
        order.deleteFood(foodCancel);
        if (order.getFoods().size() < 1) {
          getKitchen().getOrderList().remove(order);
          table.removeOrder(order);
        }
        RestaurantLog.entry("Fine", String.format("Server %s cancelled %s in Order %d %n",
                getId(), foodCancel.getName(), orderNum));
        return true;
      }
    } else {
      RestaurantLog.entry("warning", String.format("Cannot cancel food in Order %d: " +
              "not assigned to Server ", orderNum) + getId() + String.format("%n"));
      return false;
    }
  }

  /**
   * Confirms order made by the table.
   *
   * @param orderNum order number associated to this table
   * @param table    table associated to this order
   */
  void confirmOrder(int orderNum, Table table) {
    if (ServerHelper.isAssigned(orderNum, this.assignedOrders)) {
      Order order = ServerHelper.getAssigned(orderNum, this.assignedOrders);
      if (order != null) {
        order.setConfirmed();
        this.assignedOrders.remove(order);
        RestaurantLog.entry("Fine", String.format(
                "Server %s confirms Order %d in Table %d%n", getId(), orderNum, table.getTableNum()));
      }
    } else {
      RestaurantLog.entry("warning", String.format(
              "Cannot confirm Order %d: not assigned to Server %s%n",
              orderNum, getId()));
    }
  }

  /**
   * Clears the table when the customers have left. Updates the total revenue of restaurant.
   *
   * @param table table to be cleared
   */
  String clearTable(Table table) {
    if (table.isOccupied) {
      table.isOccupied = false;
      StringBuilder bills = new StringBuilder(String.format("Server %s has cleared Table %d: ",
              getId(), table.getTableNum()));
      String comma = "";
      for (String bill : table.orders.keySet()) {
        bills.append(comma);
        bills.append(bill).append(" - ").append(String.format("%.2f", table.getBill(bill) * 1.15));
        totalBills += table.getBill(bill) * 1.15;
        comma = ", ";
      }
      if (table.numCustomers >= 8) {
        bills.append(" - (gratuity tip)");
      }
      bills.append(String.format(" - Total: %.2f %n", totalBills));
      RestaurantLog.entry("Fine", String.valueOf(bills));
      table.clearOrders();
      table.clearFoodsToOrder();
      table.numCustomers = 0;
      return bills.toString();
    } else {
      String status = String.format("Cannot clear Table: Table %d is already unoccupied%n",
              table.getTableNum());
      RestaurantLog.entry("warning", status);
      return status;
    }
  }

  /**
   * Creates the food object to be ordered. Also specifies the list of extra ingredients to be
   * added. or subtacted.
   *
   * @param food Food to be created
   * @return the food object
   */
  Food createFoodItem(
          Food food) {
    return new Food(food.getName(), food.getPrice(), food.getType(), food.getIngredients());
  }

  /**
   * Deletes the food from Order when it has successfully been returned.
   *
   * @param orderNum orderNum associated with order
   * @param food     food to be deleted from order
   * @param reason   reason why food is returned
   */
  void deleteOldFoodOrder(int orderNum, Food food, String reason) {
    Order orderToEdit = ServerHelper.getAssigned(orderNum, this.assignedOrders);
    if (orderToEdit != null) {
      orderToEdit.deleteFood(food);
      RestaurantLog.entry("Fine", String.format("%s in Order %d was returned: %s%n",
              food.getName(), orderNum, reason));
    }
  }

  /**
   * record orders for return function
   * @param food food to be recorded
   * @param table table where orders are being recorded from
   * @param billId the bill where orders are being recorded to
   * @return if can record orders or not
   */
  private boolean returnRecord(Food food, Table table, String billId) {
    if (menu.hasFood(food.getName())) {
      if (getKitchen().isEnoughIngredients(food)) {
        table.addFoodOrder(food, billId);
        RestaurantLog.entry("Fine", String.format(
                "Server %s records an order of %s, " + "from Table %d%n",
                getId(), food, table.getTableNum()));
        return true;
      } else {
        RestaurantLog.entry("warning", String.format("Cannot record food Order: " +
                "Not enough ingredients for %s%n", food));
        return false;
      }
    } else {
      RestaurantLog.entry("warning", String.format("Cannot record food Order: %s not in menu. %n", food));
      return false;
    }
  }

  /**
   * placeo order for return function
   * @param table the table to be placing orders
   * @return status
   */
  private String returnPlace(Table table) {
    String status;
    if (table.hasRequests()) {
      for (String bill : table.foodsToOrder.keySet()) {
        if (table.foodsToOrder.get(bill).size() > 0) {
          Order newOrder = new Order(table.foodsToOrder.get(bill), table.getTableNum(), bill);
          table.addOrder(newOrder);
          this.assignedOrders.add(newOrder);
          getKitchen().getOrderList().add(newOrder);
          table.foodsToOrder.put(bill, new ArrayList<>());
        }
      }
      status = String.format(
              "Server %s places final Orders from Table %d%n", getId(), table.getTableNum());
      RestaurantLog.entry("Fine", status);
      return status;
    } else {
      status = String.format(
              "Cannot place Order for Table %d: Table has no pending requests %n", table.getTableNum());
      RestaurantLog.entry("warning", status);
      return status;
    }
  }
}