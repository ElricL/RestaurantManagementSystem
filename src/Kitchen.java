import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.Serializable;

/**
 * A class that represents Kitchen. Kitchen is responsible for
 * adding and subtracting ingredients, and cook updates inventory
 * in kitchen
 */

public class Kitchen implements Serializable {
    private HashMap<String, ArrayList> ingredients;
    private ArrayList<Order> orderList;

    /**
     * Constructor for Kitchen class.
     */
    Kitchen(){
        ingredients = new HashMap<>();
        orderList = new ArrayList<>();
    }

    /**
     * A method in Kitchen that adds ingredients.
     * @param ingredient ingredient to add
     * @param quantity quantity of ingredient
     * @param threshold threshold of quantity
     */
    void addIngredients(String ingredient, Integer quantity, Integer threshold){
        if(!ingredients.containsKey(ingredient)){
            ArrayList<Integer> num = new ArrayList<>();
            num.add(quantity);
            num.add(threshold);
            ingredients.put(ingredient,num);
        }
    }

    /**
     * A method in Kitchen that subtracts ingredients.
     * @param ingredient ingredient to subtract
     * @param quantity quantity of ingredient
     */
    void subtractIngredients(String ingredient, Integer quantity) {
        if(ingredients.containsKey(ingredient)) {
            ArrayList<Integer> num = new ArrayList<>();
            int initialQuantity = (int)this.ingredients.get(ingredient).get(0);
            int finalQuantity = initialQuantity - quantity;
            num.add(finalQuantity);
            num.add((Integer) ingredients.get(ingredient).get(1));
            ingredients.put(ingredient, num);
        }
    }

    /**
     * A method in Kitchen that returns the quantity of ingredient.
     * @param ingredient the ingredient involved
     * @return int, quantity of ingredient
     */

    int getQuantity(String ingredient){
        if(ingredients.containsKey(ingredient)){
            return (int)ingredients.get(ingredient).get(0);
        }
        return 0;
    }

    /**
     * A method in Kitchen to return the threshold of the ingredient.
     * @param ingredient the ingredient involved
     * @return int, threshold of ingredient
     */
    int getThreshold(String ingredient){
        if(ingredients.containsKey(ingredient)){
            return (int)ingredients.get(ingredient).get(1);
        }
        return 0;
    }

    /**
     * A method to return the list of orders.
     * @return list of orders
     */
    ArrayList<Order> getOrderList() {
        return orderList;
    }

    /**
     * A method to return the HashMap of ingredients.
     * @return HashMap of ingredients
     */
    HashMap<String, ArrayList> getIngredients() {
        return ingredients;
    }

  /**
   * A method that updates the inventory and makes a request in the file to restock the ingredients.
   *
   * @param ingredient the ingredient involved
   */
  void updateInventory(String ingredient) {
        if((int)ingredients.get(ingredient).get(0) < (int)ingredients.get(ingredient).get(1)){
            int curQuantity = (int)ingredients.get(ingredient).get(0);
            int curThreshold = (int)ingredients.get(ingredient).get(1);
            RestaurantLog.entry("Info",ingredient + " currently has " + curQuantity + ", " +
                    "below threshold of " + curThreshold + String.format("%n"));
            boolean containsRequest = false;

            // First check if request is in file already
            try (BufferedReader fileReader = new BufferedReader(new FileReader("Request.txt"))) {
                String line = fileReader.readLine();
                while (line != null) {
                    if (line.contains(ingredient)) {
                        containsRequest = true;
                    }
                    line = fileReader.readLine();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            // If not in file, write the request
            if (!containsRequest) {
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter("Request.txt", true))) {
                    String request = ingredient + " is less than the threshold: Requesting 20";
                    fileWriter.write(request);
                    fileWriter.newLine();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }

    /**
     * A method that checks the order id.
     * @param id Order id
     * @return boolean to check correct order id
     */
    boolean checkOrderId(int id){
        for(int i = 0; i < getOrderList().size(); i++){
            if(getOrderList().get(i).getId() == id){
                return true;
            }
        }
        return false;

    }

    /**
     * A method the get the order from the list of orders.
     * @param id Order id
     * @return order
     */
    Order getOrder(int id){
        for(int i = 0; i<getOrderList().size(); i++){
            if(getOrderList().get(i).getId() == id){
                return getOrderList().get(i);
            }
        }
        return null;
    }

    ArrayList<Order> getPrevOrders(int orderID) {
        ArrayList<Order> prevOrders = new ArrayList<>();
        for (Order order : this.orderList) {
            if (order.getId() < orderID) {
                prevOrders.add(order);
            } else {
                break;
            }
        } return prevOrders;
    }

    /**
     * Checks if there's enough ingredient in the kitchen inventory to make the food.
     *
     * @param food food to be checked
     * @return true if there's enough ingredient
     */
    protected boolean isEnoughIngredients(Food food) {
        HashMap<String, Integer> ingredientMap = new HashMap<>();
        ArrayList<String> ingredientList = food.getIngredients();
        for (String ingredient : ingredientList) {
            if (ingredientMap.containsKey(ingredient)) {
                ingredientMap.put(ingredient, ingredientMap.get(ingredient) + 1);
            } else {
                ingredientMap.put(ingredient, 1);
            }
        }

        for (String ingredient : ingredientMap.keySet()) {
            if (!this.getIngredients().containsKey(ingredient)
                    || this.getQuantity(ingredient) < ingredientMap.get(ingredient)) {
                return false;
            }
        }

        for (String ingredient : ingredientMap.keySet()) {
            this.subtractIngredients(ingredient, ingredientMap.get(ingredient));
            this.updateInventory(ingredient);
            // Kitchen checks for threshold and make request if below
        }
        return true;
    }
}
