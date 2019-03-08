import java.util.ArrayList;

public class ServerHelper {

    /**
     * Checks if the order associated with this orderNum is assigned to this server.
     *
     * @param orderNum order num associated with an order
     * @return true if order is assigned to server
     */
    protected static boolean isAssigned(int orderNum, ArrayList<Order> assignedOrders) {
        for (Order order : assignedOrders) {
            if (orderNum == order.getId()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the order associated to this orderNum that is assigned to this server.
     *
     * @param orderNum associated with an order
     * @return the order with this orderNum
     */
    protected static Order getAssigned(int orderNum, ArrayList<Order> assignedOrders) {
        for (Order order : assignedOrders) {
            if (orderNum == order.getId()) {
                return order;
            }
        }
        return null;
    }

    /**
     * Checks if food can be returned. Firstly, this method will check if food is associated with the
     * orders assigned to server. Then, will check if the order with this orderNum has this food. Will
     * return true if everything is true.
     *
     * @param table table associated with the return request
     * @param food food associated with the return request
     * @param orderNum order number associated with the food
     * @return true if food can be returned
     */
    protected static boolean foodReturnCheck(Table table, Food food, int orderNum, String serverId,
                                             ArrayList<Order> assignedOrders) {
        Order order = getAssigned(orderNum, assignedOrders);
        if (order != null) {
            if (isAssigned(orderNum, assignedOrders) && table.getTableNum() == order.getTableNum()) {
                if (order.hasFood(food)) {
                    return true;
                } else {
                    RestaurantLog.entry("warning", String.format(
                            "Can't return Order %d: Order does not contain %s%n", orderNum, food.getName()));
                    return false;
                }
            } else {
                RestaurantLog.entry("warning", String.format(
                        "Can't return Order %d: Not assigned to Server %s or table %d%n",
                        orderNum, serverId, table.getTableNum()));
                return false;
            }
        } else {
            RestaurantLog.entry("warning", String.format("Can't return Order %d: " +
                    "not assigned to Server ", orderNum) + serverId + String.format("%n"));
            return false;
        }
    }

    /**
     * Return whether any assigned Orders are ready to be delivered
     *
     * @param assignedOrders Server's assigned Orders
     * @return boolean
     */
    protected static boolean checkOrdersReady(ArrayList<Order> assignedOrders) {
        for (Order order : assignedOrders) {
            if (order.isFilled() && !order.isDelivered()) {
                return true;
            }
        }
        return false;
    }
}
