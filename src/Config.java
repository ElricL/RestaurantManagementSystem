import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * A Config class that can statically create configuration files for Restaurant. RestaurantManager
 * will call Config to create files to create Menu and save file if the files don't already exist.
 * The class will only be used for its static method, instance of object will not be needed.
 */
public class Config {

  /**
   * The method that RestaurantManager will be calling at the beginning of program's run.
   * CreateConfiguration() will be calling buildDefaultFood() and buildIngredientsToPrice() to build
   * corresponding files. An empty save file will be created in the method. The method will not
   * create new files if the config files already exist.
   */
  public static void createConfiguration() throws IOException {
    buildDefaultFood();
    buildIngredientsToPrice();
  }

  /**
   * BuildDefaultFood() will create DefaultFood.txt if file does not exist. The config file will be
   * created with default food information for menu.
   */
  private static void buildDefaultFood() throws IOException {
    File save = new File("FoodItems.txt");
    if (!save.exists()) {
      save.createNewFile();
      String saveName = "FoodItems.txt";

      try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(saveName, true))) {

        fileWriter.write(
            "Classic Burger | 6.00 | Main | beef patty, lettuce, tomato, cheese, bun |");
        fileWriter.newLine();
        fileWriter.write(
            "Monster Burger | 10.00 | Main | beef patty, beef patty, "
                + "lettuce, tomato, cheese, cheese, bun |");
        fileWriter.newLine();
        fileWriter.write("Poutine | 4.00 | Appetizer | fries, cheese, gravy, ground beef |");
        fileWriter.newLine();
        fileWriter.write("Green Tea Ice Cream | 3.00 | Dessert | gt scoop, gt scoop |");
        fileWriter.newLine();
      }
    }
  }

  /**
   * BuildDefaultFood() will create buildIngredientsToPrice.txt if file does not exist. The config
   * file will be created with default ingredient information for menu.
   */
  private static void buildIngredientsToPrice() throws IOException {
    File save = new File("IngredientsToPrice.txt");
    if (!save.exists()) {
      save.createNewFile();
      String saveName = "IngredientsToPrice.txt";

      try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(saveName, true))) {

        fileWriter.write("cheese | 1.00");
        fileWriter.newLine();
        fileWriter.write("beef patty | 2.00");
        fileWriter.newLine();
        fileWriter.write("lettuce | 0.20");
        fileWriter.newLine();
        fileWriter.write("tomato | 0.30");
        fileWriter.newLine();
        fileWriter.write("bun | 0.70");
        fileWriter.newLine();
        fileWriter.write("fries | 2.00");
        fileWriter.newLine();
        fileWriter.write("gravy | 1.00");
        fileWriter.newLine();
        fileWriter.write("gt scoop | 2.00");
        fileWriter.newLine();
        fileWriter.write("ground beef | 1.50");
        fileWriter.newLine();
      }
    }
  }
}
