import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.event.EventHandler;

import java.io.*;

// class RestaurantManager
// ----------------------------------------------------------------------
// The class RestaurantManager will be where the program will run. Running the main method will
// start
// the Restaurant managing system to keep track of events occurring in the restaurant.
// The class will contain all necessary classes to run the program.
public class RestaurantManager extends Application {
  private static Stage window;
  private Scenes setupScene = new Scenes();
  private Restaurant restaurant;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    Config.createConfiguration(); // Create config files for Restaurant if they do not exist
    Restaurant newRestaurant = new Restaurant();
    Server.totalBills = 0;

    try {
      if (new File("Restaurant.ser").exists()) {
        ObjectInputStream objectInputStream =
            new ObjectInputStream(new FileInputStream("Restaurant.ser"));
        this.restaurant = (Restaurant) objectInputStream.readObject();
        updateOldRestaurant(this.restaurant, newRestaurant);
      } else {
        restaurant = new Restaurant();
        updateOldRestaurant(this.restaurant, newRestaurant);
      }
    } catch (IOException | ClassNotFoundException io) {
      io.printStackTrace();
    }

    window = primaryStage;
    window.setScene(setupScene.getFirstScene(window, restaurant));
    new RestaurantLog();

    //      window.setScene(setupScene.getRecordOrderScene(server, "1", restaurant));
    window.setTitle("Restaurant Manager");
    window.show();

    window.setOnCloseRequest(
        new EventHandler<WindowEvent>() {
          public void handle(WindowEvent we) {
            try {
              ObjectOutputStream objectOutputStream =
                  new ObjectOutputStream(new FileOutputStream("Restaurant.ser"));
              objectOutputStream.writeObject(restaurant);
              RestaurantLog.entry(
                  "Info", "Total profit earned Today: " +
                              String.format("%.2f",Server.totalBills) + String.format("%n"));
            } catch (IOException io) {
              io.printStackTrace();
            }
          }
        });
  }

  public void updateOldRestaurant(Restaurant oldRes, Restaurant newRes) {
    oldRes.updateMenu(newRes.getMenu());
    for (String ingredient : newRes.getMenu().ingredientToPrice.keySet()) {
      if (!oldRes.getKitchen().getIngredients().keySet().contains(ingredient)) {
        oldRes.getKitchen().addIngredients(ingredient, 20, 20);
      }
    }
  }
}
