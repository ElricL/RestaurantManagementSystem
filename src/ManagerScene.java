import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class represents Manager Scenes
 */
public class ManagerScene {
    /**
     * This method returns the Manager Scene
     * @param window Stage that is being used
     * @param manager Manager that is working
     * @param restaurantScene restaurant scene that is being used
     * @return Manager Scene
     */
    public Scene getManagerScene(Stage window, Manager manager, Scene restaurantScene, Restaurant restaurant) {

        Text title = new Text("Manager");
        title.setStyle("-fx-font: 40 Calibri; -fx-base: #b6e7c9;");

        Button viewInventoryButton = new Button("Inventory");
        viewInventoryButton.setStyle("-fx-font: 30 arial; -fx-base: #dee2e0;");
        viewInventoryButton.setOnAction(e -> window.setScene(getScene("Inventory", manager, window,
                restaurantScene, restaurant)));

        Button viewRequestsButton = new Button("Requests");
        viewRequestsButton.setStyle("-fx-font: 30 arial; -fx-base: #dee2e0;");
        viewRequestsButton.setOnAction(e -> window.setScene(getScene("Requests", manager, window,
                restaurantScene, restaurant)));

        Button viewOrdersList = new Button("Orders");
        viewOrdersList.setStyle("-fx-font: 30 arial; -fx-base: #dee2e0;");
        viewOrdersList.setOnAction(e -> window.setScene(getScene("Orders", manager, window,
                restaurantScene,restaurant)));

        Button updateSpecials = new Button("Update Specials");
        updateSpecials.setOnAction(e -> {
            ArrayList<Food> normalFood = restaurant.getMenu().foodList;
            int specNum = restaurant.getMenu().specialFood.size();
            for (Food food: normalFood) {
                manager.updateSpecialFood(food);
            }

            if (restaurant.getMenu().specialFood.size() > specNum) {
                Food newFood = restaurant.getMenu().specialFood.get(restaurant.getMenu().specialFood.size() - 1);
                ScenesHelper.getMessageScene(newFood.toString() + " was added to Special Menu");
            } else {
                ScenesHelper.getMessageScene("No Food was added to Special Menu");
            }

        });

        // Layout for buttons upper scene
        HBox buttonsLayout = new HBox();
        buttonsLayout.setSpacing(10);
        buttonsLayout.setAlignment(Pos.CENTER);
        buttonsLayout.getChildren().addAll(viewInventoryButton, viewRequestsButton, viewOrdersList, updateSpecials);

        // Layout for Change Threshold
        GridPane changeThresholdLayout = getIngredientQuantitySetup("Change Threshold", manager);

        // Layout for Restock Ingredient
        GridPane restockIngredientLayout = getIngredientQuantitySetup("Restock Ingredient", manager);

        // Layout for both Change Threshold and Restock Ingredient
        HBox innerLayout = new HBox();
        innerLayout.setSpacing(10);
        innerLayout.setAlignment(Pos.CENTER);
        innerLayout.getChildren().addAll(changeThresholdLayout, restockIngredientLayout);

        // Back button to bring back to previous scene
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> window.setScene(restaurantScene));

        // Inner layout for this scene
        BorderPane innerSceneLayout = new BorderPane();
        innerSceneLayout.setTop(buttonsLayout);
        innerSceneLayout.setCenter(innerLayout);

        // Overall layout for this scene
        BorderPane sceneLayout = new BorderPane();
        sceneLayout.setAlignment(title, Pos.CENTER);
        sceneLayout.setTop(title);
        sceneLayout.setCenter(innerSceneLayout);
        sceneLayout.setAlignment(backButton, Pos.CENTER);
        sceneLayout.setBottom(backButton);

        return new Scene(sceneLayout, 800, 500);
    }

    /**
     * This methods creates a ChoiceBox (drop down menu) with list of ingredients string.
     * @return a ChoiceBox
     */
    private ChoiceBox<String> getIngredientsChoiceList(Manager manager){
        ChoiceBox<String> ingredientsListBox = new ChoiceBox<>();
        ingredientsListBox.setStyle("-fx-font: 15 arial;");
        HashMap<String, ArrayList> ingredientsList = manager.getKitchen().getIngredients();
        ingredientsListBox.getItems().addAll(ingredientsList.keySet());

        return ingredientsListBox;
    }

    /**
     * Returns a GridPane with all the nodes and layout setup. Created this method to reduce redundancy.
     * @param title Title of gridPane
     * @return the GridPane
     */
    private GridPane getIngredientQuantitySetup(String title, Manager manager){
        Text titleText = new Text(title);
        titleText.setStyle("-fx-font: 15 arial;");
        titleText.setUnderline(true);

        Label selectLabel = new Label("Select ingredient:");
        selectLabel.setStyle("-fx-font: 15 arial;");
        Label enterLabel = new Label("Enter quantity:");
        enterLabel.setStyle("-fx-font: 15 arial;");
        Button submitButton = new Button();
        submitButton.setStyle("-fx-font: 15 arial;");
        // Building ComboBox for ingredients list
        ChoiceBox<String> ingredientList = getIngredientsChoiceList(manager);
        ingredientList.setStyle("-fx-font: 15 arial;");
        // Input for quantity of ingredient
        TextField quantityInput = new TextField();
        quantityInput.setStyle("-fx-font: 15 arial;");

        // Action for submit button
        if (title.equals("Change Threshold")) {
            submitButton.setText("Submit New Threshold");
            submitButton.setOnAction(e -> {
                if (ScenesHelper.getChoice(ingredientList) == null) {
                    ScenesHelper.getMessageScene("Please select an ingredient");
                } else if (!quantityInput.getText().equals("")) {
                    ScenesHelper.getMessageScene(manager.changeThreshold(ScenesHelper.getChoice(ingredientList),
                            quantityInput.getText()));
                } else if (quantityInput.getText().equals("")) {
                    ScenesHelper.getMessageScene("Please enter quantity.");
                }
            });
        } else if (title.equals("Restock Ingredient")){
            submitButton.setText("Submit Restock");
            submitButton.setOnAction(e -> {
                        if (ScenesHelper.getChoice(ingredientList) == null) {
                            ScenesHelper.getMessageScene("Please select an ingredient");
                        } else if (!quantityInput.getText().equals("")) {
                            ScenesHelper.getMessageScene(manager.restockInventory(ScenesHelper.getChoice(ingredientList),
                                    quantityInput.getText()));
                        } else {
                            ScenesHelper.getMessageScene(manager.restockInventory(ScenesHelper.getChoice(ingredientList)));
                        }
                    }
            );
        }

        // Layout for Ingredient/Quantity setup
        GridPane gridPane = new GridPane();
        gridPane.setVgap(4);
        gridPane.setHgap(10);
        gridPane.add(titleText, 0,0);
        gridPane.add(selectLabel,0, 1);
        gridPane.add(ingredientList, 1,1);
        gridPane.add(enterLabel, 0, 2);
        gridPane.add(quantityInput, 1,2);
        gridPane.add(submitButton, 1, 3);

        return gridPane;
    }

    /**
     * Returns a scene, either for viewing inventory, requests or pending orders.
     * @param info to determine which scene to view
     * @return a scene according to the given info
     */
    private Scene getScene(String info, Manager manager, Stage window, Scene restaurantScene, Restaurant restaurant){
        BorderPane innerSceneLayout = new BorderPane();
        Text title = new Text("Manager");
        Label sceneLabel = new Label(info);
        Label outputText = new Label();
        switch (info) {
            case "Inventory": {
                outputText.setText(manager.generateInventory());
                break;
            }
            case "Requests": {
                outputText.setText(manager.readRequests());
                break;
            }
            case "Orders": {
                outputText.setText(manager.seeOrders());
                break;
            }
        }

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> window.setScene(getManagerScene(window, manager, restaurantScene, restaurant)));

        innerSceneLayout.setTop(sceneLabel);
        innerSceneLayout.setCenter(outputText);

        BorderPane sceneLayout = new BorderPane();
        sceneLayout.setTop(title);
        sceneLayout.setCenter(innerSceneLayout);
        sceneLayout.setBottom(backButton);

        return new Scene(sceneLayout, 800, 500);
    }
}
