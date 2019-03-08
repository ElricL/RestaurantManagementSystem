import java.io.Serializable;
import java.util.ArrayList;

/**
 * A class that represents Cook of a restaurant. A Cook is responsible for preparing the food,
 * checking the status of the order, and checking if the given order is completely filled. Cook also
 * makes request for ingredients that need to be restocked in requests.txt file.
 */
public class Cook extends Employee implements Serializable {

  /**
   * Constructor for Cook class. Each cook has their own id and what type of cook they are. Cook has
   * access to the kitchen.
   *
   * @param id cook id
   * @param type cook type
   * @param kitchen kitchen associated with restaurant
   */
  Cook(String id, String type, Kitchen kitchen) {
    super(id, kitchen);
    setType(type);
    setJob("Cook");
  }

  /**
   * Method for Cook to prepare the food from the given order. Cook makes a request for the
   * ingredients that need to be restocked after using it for preparing the food
   *
   * @param order the order to prepare
   */
  public String prepFood(Order order) {
    StringBuilder out = new StringBuilder();
    if (order != null && order.hasSeen(getId())) {
      for (Food food : order.getFoods()) {
        if (food.getReady()) {
          String status = food.getName() + " is already prepared";
          out.append(status);
          out.append("\n");
          RestaurantLog.entry("Warning", status + String.format("%n"));
        }
        else if (getType().equals(food.getType()) && !food.getReady()) {
          food.setReady();
          String status = toString() + " prepared " + food.getName() + " from Order: " + order.getId();
          out.append(status);
          out.append("\n");
          RestaurantLog.entry("Fine", status + String.format("%n"));
        } else if (!getType().equals(food.getType())) {
          String status = toString() + " of type " + getType() + " can't prepare " + food.getName()
                  + " of type " + food.getType();
          out.append(status);
          out.append("\n");
          RestaurantLog.entry("Warning", status + String.format("%n"));
        }
      }
      if (orderFilledCheck(order)) {
        order.setFilled();
        String status = "Order: " + order.getId() + " is ready.";
        out.append(status);
        out.append("\n");

      } else {
        String status = "Order: " + order.getId() + " is not ready";
        out.append(status);
        out.append("\n");
      }
    } else {
      out.append("Cook cannot prepare order that has not been seen");
    }
    return out.toString();
  }

  /**
   * A method for Cook class to check if the order is completely ready.
   *
   * @param order the order to be checked
   * @return a boolean to check it is filled
   */
  private boolean orderFilledCheck(Order order) {
    // Return True iff all the food are prepared in the given order
    for (int i = 0; i < order.getFoods().size(); i++) {
      if (!order.getFoods().get(i).getReady()) {
        return false;
      }
    }
    RestaurantLog.entry("Info","Order: " + order.getId() + " has been filled" + String.format("%n"));
    return true;
  }

  /**
   * A method to check if the Cook has seen the order.
   *
   * @param order the order to check
   */
  String hasSeenOrder(Order order) {
    ArrayList<Order> prevOrder = getKitchen().getPrevOrders(order.getId());
    int totalOrder = prevOrder.size();
    int i = 0;
    while(i< totalOrder) {
      Order curOrder = prevOrder.get(i);
      if (!curOrder.hasSeen(this.getId())) {
        String warning = "Can't set this order to seen. Order" + curOrder.getId() + " was not seen by " + toString();
        RestaurantLog.entry("Warning", warning + String.format("%n"));
        return warning;
      }
      i = i + 1;
    }
    order.setHasSeen(getId());
    String sees = this.toString() + " has seen Order: " + order.getId();
    RestaurantLog.entry("Fine", sees + String.format("%n"));
    return sees;
  }

  /**
   * String representation of Cook
   * @return the String Representation of Cook
   */
  public String toString() {
    return "Cook " + getId();
  }

  /**
   * Returns the list of Orders to prepare
   * @return list of orders
   */
  public ArrayList<Order> getOrdersToPrepare() {
    ArrayList<Order> ordersToPrepare = new ArrayList<>();
    for (Order order: getKitchen().getOrderList()) {
      if (order.containsType(getType())) {
        ordersToPrepare.add(order);
      }
    }
    return ordersToPrepare;
  }
}