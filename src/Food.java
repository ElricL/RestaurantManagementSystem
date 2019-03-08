import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * A Food class. A Food Object contains has a name, price, type, and a list of ingredients that is
 * needed to create it.
 */
public class Food implements Serializable {

  private String name;
  private boolean isReady;
  private String type;
  private double price;
  private ArrayList<String> ingredients;
  private boolean isDiscounted;

  /**
   * A Food object has its own unique name, base price, type, and list of ingredients required to
   * cook it. Unless specified, Food has no subtractions or additions, thus is only priced according
   * to its base price. Only additions of ingredients affects a Food's price Only a Cook can prepare
   * a Food of the same type
   *
   * @param name this Food's name
   * @param price this Food's base price, which does not include any additions
   * @param type this Food's type, "main", "appetizer", or "desert"
   * @param ingredients the ingredients needed to prepare this Food
   */
  Food(String name, double price, String type, ArrayList<String> ingredients) {
    this.name = name;
    this.price = price;
    this.type = type;
    this.ingredients = ingredients;
    this.isReady = false;
    this.isDiscounted = false;
  }

  /** @return name of this food. */
  public String getName() {
    return name;
  }

  double getDiscountedPrice(){
    return this.price * 0.9;
  }

  /** @return price of this food. */
  double getPrice() {
    return this.price;
  }

  /** @return list of ingredients this food has */
  ArrayList<String> getIngredients() {
    return ingredients;
  }

  /** Sets this Food to be ready. */
  void setReady() {
    this.isReady = true;
  }

  /**
   * Checks if this Food is ready.
   *
   * @return true if this Food is ready
   */
  boolean getReady() {
    return this.isReady;
  }

  public void setPrice(double price){
    this.price = price;
  }

  /** Sets the food true if discounted */
  void setIsDiscounted(){this.isDiscounted = true;}

  /**
   * Checks if the food is discounted
   * @return true if the food is discounted
   */
  boolean checkIsDiscounted(){return this.isDiscounted;}


  /**
   * Returns the type of this Food. Only a Cook of this type can prepare this Food.
   *
   * @return the String type of this Food
   */
  String getType() {
    return this.type;
  }

  /** Returns true this this Food is equivalent to Food other. */
  @Override
  public boolean equals(Object o) {
    if (o instanceof Food) {
      Collections.sort(((Food) o).getIngredients());
      Collections.sort(this.getIngredients());
      if (((Food) o).getIngredients().equals(this.getIngredients())) {
        return this.name.equals(((Food) o).name);
      }
    }
    return false;
  }

  /**
   * Returns the String representation of this Food.
   *
   * @return String representation of food
   */
  @Override
  public String toString() {
//    return "Food("
//        + this.name
//        + ", "
//        + Double.toString(this.price)
//        + ", "
//        + this.type
//        + ", "
//        + ingredients.toString()
//        + ")";
    return getName();
  }

  /**
   * Adds ingredient to the ingredients list
   * @param ingredient ingredient being added
   */
  void addIngredient(String ingredient) {
    this.ingredients.add(ingredient);
  }

  /**
   * Ingredient that needs to be removed
   * @param ingredient ingredient needed to be removed
   * @return the ingredient
   */
  String remIngredient(String ingredient) {
    if (!this.ingredients.contains(ingredient)) {
      return "Cannot remove " + ingredient;
    } else {
      this.ingredients.remove(ingredient);
      return "Removed " + ingredient;
    }
  }

}
