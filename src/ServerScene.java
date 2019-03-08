import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;

/**
 * This class represents the Server Scene
 */
public class ServerScene {

    /**
     * This method returns the Server Scene
     * @param window Stage that is being used
     * @param server server that is used
     * @param restaurant the Restaurant this Scene is using
     * @param restaurantScene restaurant scene that is being used
     * @return Server Scene
     */
    public Scene getServerScene(Stage window, Server server, Restaurant restaurant, Scene restaurantScene ){

        ChoiceBox<String> tablesChoiceBox = new ChoiceBox<>();
        tablesChoiceBox.setValue("Table 1");
        ArrayList<String> tablesList = restaurant.getTablesString();
        tablesChoiceBox.getItems().addAll(tablesList);

        Button recordOrderButton = new Button("Record Order");
        recordOrderButton.setOnAction(event ->
                window.setScene(getRecordOrderScene(window, server,
                        ScenesHelper.getChoice(tablesChoiceBox), restaurant, restaurantScene)));

        Button viewOrderButton = new Button("View Order");
        viewOrderButton.setOnAction(event -> window.setScene(getViewOrdersScene(window, restaurant, server,
                ScenesHelper.getChoice(tablesChoiceBox), restaurantScene)));

        Button getReceiptButton = new Button("Get Receipt");
        getReceiptButton.setOnAction(e -> {
            int tableNum = Integer.parseInt(ScenesHelper.getChoice(tablesChoiceBox).replaceAll("[^0-9]",
                    ""));
            Table chosenTable = restaurant.getTable(tableNum);
            ScenesHelper.getMessageScene(chosenTable.getReceipts());
        });

        Button clearTableButton = new Button("Clear Table");
//        clearTableButton.setStyle("-fx-font: 20 arial;");
        clearTableButton.setOnAction(event -> {
            int tableNum = Integer.parseInt(ScenesHelper.getChoice(tablesChoiceBox).replaceAll("[^0-9]",
                    ""));
            ScenesHelper.getMessageScene(server.clearTable(restaurant.getTable(tableNum)));
        });

        HBox actionsBox = new HBox();
        actionsBox.setAlignment(Pos.CENTER);
        actionsBox.setSpacing(10);
        actionsBox.getChildren().addAll(tablesChoiceBox, recordOrderButton, viewOrderButton, clearTableButton, getReceiptButton);

        BorderPane ordersBoxLayout = new BorderPane();
        HBox ordersBox = new HBox();
        ScrollPane ordersScrollPane = new ScrollPane();
        ordersScrollPane.setContent(ordersBox);
        Label assignedOrdersLabel = new Label("Assigned Orders: ");
        assignedOrdersLabel.setStyle("-fx-font: 15 arial;");
        ordersBoxLayout.setTop(assignedOrdersLabel);
        ordersBoxLayout.setCenter(ordersScrollPane);

        for (Order order: server.getAssignedOrders()) {
            Text orderNum = new Text("Order " + order.getId());
            Text tableNum = new Text("Table " + order.getTableNum());
            Label orderDetails = new Label();
            orderDetails.setText(order.getFoodList());

            VBox orderNameBox = new VBox();
            orderNameBox.getChildren().addAll(orderNum, tableNum);
            orderNameBox.setSpacing(5);

            Button deliverButton = new Button("Deliver");
            if (!order.isFilled()) {
                deliverButton.setDisable(true);
            }
            deliverButton.setOnAction(e -> {
                ScenesHelper.getMessageScene(server.deliverOrder(order.getId(), restaurant.getTable(order.getTableNum())));
                deliverButton.setDisable(true);
                getConfirmOrderWindow(server, order, restaurant, window, restaurantScene);
            });
            if (order.isDelivered()) {
                deliverButton.setDisable(true);
            }

            VBox orderDetailsBox = new VBox();
            orderDetailsBox.setStyle("-fx-border-color: black;\n" +
                    "-fx-border-insets: 5;\n" +
                    "-fx-border-width: 1;\n");
            orderDetailsBox.setPrefWidth(200);
            orderDetailsBox.getChildren().addAll(orderNameBox, orderDetails, deliverButton);
            ordersBox.getChildren().add(orderDetailsBox);
        }

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> window.setScene(restaurantScene));

        BorderPane sceneLayout = new BorderPane();
        sceneLayout.setTop(actionsBox);
        sceneLayout.setCenter(ordersBoxLayout);
        sceneLayout.setBottom(backButton);

        return new Scene(sceneLayout, 800, 500);
    }

    /**
     * This scene confirms if the order is delivered
     * @param server server that is used
     * @param order order that is used
     * @param restaurant the Restaurant this Scene is using
     */
         private void getConfirmOrderWindow(Server server, Order order, Restaurant restaurant, Stage window, Scene restaurantScene) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);

        Button confirmOrderButton = new Button("Confirm");
        confirmOrderButton.setOnAction(e -> {
            server.confirmOrder(order.getId(), restaurant.getTable(order.getTableNum()));
            confirmOrderButton.getScene().getWindow().hide();
        });

        Button returnButton = new Button("Return");
        returnButton.setOnAction(event -> {
            window.setScene(getViewOrdersScene(window, restaurant, server, String.valueOf(order.getTableNum()), restaurantScene));
            returnButton.getScene().getWindow().hide();
        });

        Label label = new Label("Please confirm order delivery.");

        HBox buttonLayout = new HBox();
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.getChildren().addAll(confirmOrderButton, returnButton);

        VBox innerLayout = new VBox();
        innerLayout.setAlignment(Pos.CENTER);
        innerLayout.getChildren().addAll(label, buttonLayout);

        StackPane sceneLayout = new StackPane();
        sceneLayout.getChildren().addAll(innerLayout);
        Scene confirmOrderScene = new Scene(sceneLayout, 300, 200);

        stage.setScene(confirmOrderScene);
        stage.show();
    }


    // --------------------- Setup Record Order Scene ---------------------//
    private Food chosenFood;
    private ObservableList<Food> foodObservableList = FXCollections.observableArrayList();
    private String chosenBillId;
    private ObservableList<String> ingredientsObservableList = FXCollections.observableArrayList();
    private ChoiceBox<String> ingredientsList;
    private Label foodName = new Label();

    /**
     * This scene shows the server recording the order
     * @param window the Stage that is used
     * @param server server that is used
     * @param table table that is used
     * @param restaurant the Restaurant this Scene is using
     * @param restaurantScene restaurant scene that is being used
     * @return Recording Order scene
     */
    private Scene getRecordOrderScene(Stage window, Server server, String table, Restaurant restaurant, Scene restaurantScene) {
        int tableNum = Integer.parseInt(table.replaceAll("[^0-9]", ""));
        Table selectedTable = restaurant.getTable(tableNum);

        // Setup customers info layout
        ObservableList<String> customerObservableList = FXCollections.observableArrayList(selectedTable.getPayingCustomersList());
        Label numCustomerLabel = new Label("Number of customer: ");
        Label numPayCustLabel = new Label("Number of paying customer: ");
        TextField numPayCustInput = new TextField();
        TextField numCustomerInput = new TextField();
        Button submitButton = new Button("Submit");
        submitButton.setAlignment(Pos.BOTTOM_RIGHT);
        submitButton.setOnAction(event -> {
            ScenesHelper.getMessageScene(server.fillTable(selectedTable,
                    numPayCustInput.getText(), numCustomerInput.getText()));
            customerObservableList.addAll(selectedTable.getPayingCustomersList());
            if (selectedTable.isOccupied) {
                submitButton.setDisable(true);
            }
        });
        if (selectedTable.isOccupied) {
            submitButton.setDisable(true);
        }

        HBox customerInfoInput = new HBox();
        customerInfoInput.getChildren().addAll(numCustomerLabel, numCustomerInput, numPayCustLabel, numPayCustInput);
        VBox customerInfoLayout = new VBox();
        customerInfoLayout.getChildren().addAll(customerInfoInput, submitButton);

        // Setup TableView for Customer
        TableView<String> custTable = new TableView<>();
        custTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<String, String> customerColumn = new TableColumn<>("Customer");
        customerColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue()));
        custTable.getColumns().add(customerColumn);
        custTable.setItems(customerObservableList);
        custTable.setOnMouseClicked(event -> {
            setChosenBillId(custTable.getSelectionModel().getSelectedItem());
            foodObservableList.setAll(selectedTable.foodsToOrder.get(getChosenBillId()));
        });

        // Setup layout for Foods in Order
        TableView<Food> foodTable = new TableView<>();
        foodTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Food, String> foodNameColumn = new TableColumn<>("Food");
        foodNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        foodTable.getColumns().add(foodNameColumn);
        foodTable.setItems(foodObservableList);
        foodTable.setOnMouseClicked(e -> {
            if (foodTable.getSelectionModel().getSelectedItem() != null) {
                setChosenFood(foodTable.getSelectionModel().getSelectedItem());
                foodName.setText(getChosenFood().getName());
                ingredientsObservableList.setAll(getChosenFood().getIngredients());
                ingredientsList.getItems().setAll(getChosenFood().getIngredients());
            }
        });

        // Setup food selections to be added/remove from order list
        ChoiceBox<String> foodList = new ChoiceBox<>();
        foodList.getItems().addAll(restaurant.getMenu().getFoodNameList());
        Button addFoodButton = new Button("Add");
        addFoodButton.setAlignment(Pos.BOTTOM_LEFT);
        addFoodButton.setOnAction(event -> addFood(server,
                server.createFoodItem(restaurant.getMenu().getFood(ScenesHelper.getChoice(foodList))),
                table, getChosenBillId(), restaurant));
        Button remFoodButton = new Button("Remove");
        remFoodButton.setAlignment(Pos.BOTTOM_RIGHT);
        remFoodButton.setOnAction(event -> remFood(server,
                restaurant.getMenu().getFood(ScenesHelper.getChoice(foodList)), table, getChosenBillId(), restaurant));
        HBox foodButtonsLayout = new HBox();
        foodButtonsLayout.setAlignment(Pos.CENTER);
        foodButtonsLayout.getChildren().addAll(foodList, addFoodButton, remFoodButton);

        VBox foodLayout = new VBox();
        foodLayout.getChildren().addAll(foodTable,foodButtonsLayout);

        // Setup layout for Ingredients in Food
        TableView<String> ingredientsTable = new TableView<>();
        ingredientsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<String, String> ingredientsColumn = new TableColumn<>("Ingredients");
        ingredientsColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue()));
        ingredientsTable.getColumns().add(ingredientsColumn);
        ingredientsTable.setItems(ingredientsObservableList);

        ingredientsList = new ChoiceBox<>();
        Button addIngredientButton = new Button("Add");
        addIngredientButton.setOnAction(event -> {
            getChosenFood().addIngredient(ScenesHelper.getChoice(ingredientsList));
            ingredientsObservableList.add(ScenesHelper.getChoice(ingredientsList));
        });
        Button remIngredientButton = new Button("Remove");
        remIngredientButton.setOnAction(event -> {
            getChosenFood().remIngredient(ScenesHelper.getChoice(ingredientsList));
            ingredientsObservableList.remove(ScenesHelper.getChoice(ingredientsList));
        });

        HBox ingredientsButtonsLayout = new HBox();
        ingredientsButtonsLayout.setAlignment(Pos.CENTER);
        ingredientsButtonsLayout.getChildren().addAll(ingredientsList, addIngredientButton, remIngredientButton);

        Button placeOrderButton = new Button("Place order");
        placeOrderButton.setOnAction(event -> {
            ScenesHelper.getMessageScene(server.placeOrder(selectedTable));
            window.setScene(getServerScene(window, server,restaurant, restaurantScene));
        });
        placeOrderButton.setAlignment(Pos.BOTTOM_LEFT);

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> window.setScene(getServerScene(window, server, restaurant, restaurantScene)));

        HBox setupButtonsLayout = new HBox();
        setupButtonsLayout.setAlignment(Pos.BOTTOM_RIGHT);
        setupButtonsLayout.setSpacing(5);
        setupButtonsLayout.getChildren().addAll(placeOrderButton, backButton);

        VBox ingredientsLayout = new VBox();
        ingredientsLayout.getChildren().addAll(foodName, ingredientsTable, ingredientsButtonsLayout);

        GridPane centerLayout = new GridPane();
        centerLayout.add(custTable, 0, 0);
        centerLayout.add(foodTable, 1, 0);
        centerLayout.add(ingredientsTable, 2, 0);
        centerLayout.add(foodButtonsLayout, 1, 1);
        centerLayout.add(ingredientsButtonsLayout, 2, 1);

        BorderPane sceneLayout = new BorderPane();
        Insets insets = new Insets(10);
        sceneLayout.setTop(customerInfoLayout);
        BorderPane.setMargin(customerInfoLayout, insets);
        sceneLayout.setCenter(centerLayout);
        BorderPane.setMargin(centerLayout, insets);
        sceneLayout.setBottom(setupButtonsLayout);
        BorderPane.setMargin(setupButtonsLayout, insets);
        return new Scene(sceneLayout, 800, 500);
    }

    /**
     * This method adds food to this table's bill
     * @param server Server that is working at this table
     * @param food food that needs to be added
     * @param table table that is used
     * @param billId bill id
     * @param restaurant the Restaurant this is using
     */
    private void addFood(Server server, Food food, String table, String billId, Restaurant restaurant) {
        int tableNum = Integer.parseInt(table.replaceAll("[^0-9]", ""));
        server.recordFoodOrder(restaurant.getTable(tableNum), food, billId);
        foodObservableList.add(food);

    }

    /**
     * This method deletes food from this table's chosen bill
     * @param server Server that is working at this table
     * @param food food that needs to be deleted
     * @param table table that is used
     * @param chosenBillId chosen bill id
     * @param restaurant the Restaurant this is using
     */
    private void remFood(Server server, Food food, String table, String chosenBillId, Restaurant restaurant) {
        if (food == null) {
            ScenesHelper.getMessageScene("Please select food to remove.");
        } else {
            int tableNum = Integer.parseInt(table.replaceAll("[^0-9]", ""));
            foodObservableList.remove(food);
            server.deleteFoodOrder(restaurant.getTable(tableNum), food, chosenBillId);
        }
    }

    /**
     * Sets the chosen food
     * @param food food that is used
     */
    private void setChosenFood(Food food) {
        chosenFood = food;
    }

    /**
     * Get the chosen food
     * @return the chosen food
     */
    private Food getChosenFood() {
        return chosenFood;
    }

    /**
     * Set the chosen bill
     * @param billId bill id
     */
    private void setChosenBillId(String billId) {
        chosenBillId = billId;
    }

    /**
     * Get the chosen bill
     * @return the chosen bill id
     */
    private String getChosenBillId() {
        return chosenBillId;
    }



    // -------------------- Setup View Orders for Server Scene ----------------------- //

    private Food chosenFoodItem;
    private Order chosenOrder;

    /**
     * This Scene Views the order at the given table
     * @param window the Stage that is being used
     * @param restaurant the Restaurant this Scene is using
     * @param server server that is working at this table
     * @param table table that is being used
     * @param restaurantScene restaurant scene that is being used
     * @return View order Scene
     */
    private Scene getViewOrdersScene(Stage window, Restaurant restaurant, Server server, String table, Scene restaurantScene) {
        int tableNum = Integer.parseInt(table.replaceAll("[^0-9]", ""));
        Table chosenTabel = restaurant.getTable(tableNum);

        ObservableList<Order> orderObservableList = FXCollections.observableArrayList();
        ObservableList<Food> foodObservableList = FXCollections.observableArrayList();

        ChoiceBox<String> billChoiceBox = new ChoiceBox<>();
        ObservableList<String> billObservableList = FXCollections.observableArrayList(chosenTabel.getPayingCustomersList());
        billChoiceBox.setItems(billObservableList);
        Button getBillButton = new Button("Get Bill");
        getBillButton.setOnAction(event -> {
            double bill = chosenTabel.getBill(ScenesHelper.getChoice(billChoiceBox));
            ScenesHelper.getMessageScene(Double.toString(bill));
        });

        Label chooseCustomer = new Label("Choose Customer: ");
        chooseCustomer.setAlignment(Pos.TOP_RIGHT);
        ObservableList<String> customerObservableList = FXCollections.observableArrayList(chosenTabel.getPayingCustomersList());
        TableView<String> custTable = new TableView<>();
        custTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<String, String> customerColumn = new TableColumn<>("Customer");
        customerColumn.setCellValueFactory(param -> new ReadOnlyStringWrapper(param.getValue()));
        custTable.getColumns().add(customerColumn);
        custTable.setItems(customerObservableList);
        custTable.setOnMouseClicked(event -> {
            if (custTable.getSelectionModel().getSelectedItem() != null) {
                orderObservableList.setAll(restaurant.getTable(tableNum).orders.get(custTable.getSelectionModel().getSelectedItem()));
            }
        });

        Label ordersLabel = new Label("Orders");
        TableView<Order> ordersTable = new TableView<>();
        ordersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Order, String> orderIdColumn = new TableColumn<>("Order Id");
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        ordersTable.getColumns().add(orderIdColumn);
        ordersTable.setItems(orderObservableList);
        ordersTable.setOnMouseClicked(e -> {
            if (ordersTable.getSelectionModel().getSelectedItem() != null) {
                setChosenOrder(ordersTable.getSelectionModel().getSelectedItem());
                foodObservableList.setAll(getChosenOrder().getFoods());
            }
        });

        Label foodItemLabel = new Label("Food Ordered");
        TableView<Food> foodsTable = new TableView<>();
        foodsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Food, String> foodNameTableColumn = new TableColumn<>("Food");
        foodNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        foodsTable.getColumns().add(foodNameTableColumn);
        foodsTable.setItems(foodObservableList);
        foodsTable.setOnMouseClicked(event -> {
            if (foodsTable.getSelectionModel().getSelectedItem() != null){
                setChosenFoodItem(foodsTable.getSelectionModel().getSelectedItem());
            }
        });

        Button cancelButton = new Button("Cancel Food");
        cancelButton.setOnAction(event -> {
            if (server.cancelFoodItem(getChosenOrder().getId(), getChosenFoodItem(), chosenTabel)) {
                foodObservableList.remove(getChosenFoodItem());
                ScenesHelper.getMessageScene(getChosenFoodItem().getName() + " has been canceled.");
            } else {
                ScenesHelper.getMessageScene("Cannot cancel food item.");
            }
        });

        Button returnButton = new Button("Return food");
        returnButton.setOnAction(event -> {
            if (getChosenOrder().isDelivered()) {
                getReturnScene(server, chosenTabel, getChosenOrder());
                ScenesHelper.getMessageScene(server.returnOrder(getChosenOrder().getId(), chosenTabel,
                        server.createFoodItem(restaurant.getMenu().getFood(getChosenFoodItem().toString()))));
            } else {
                ScenesHelper.getMessageScene("Food hasn't been delivered.");
                window.setScene(getViewOrdersScene(window, restaurant, server, table, restaurantScene));
            }
            });

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> window.setScene(getServerScene(window, server, restaurant, restaurantScene)));

        HBox getBillLayout = new HBox();
        getBillLayout.getChildren().addAll(billChoiceBox, getBillButton);

        HBox buttonsLayout = new HBox();
        buttonsLayout.setSpacing(10);
        buttonsLayout.getChildren().addAll(cancelButton, returnButton);

        VBox billLayout = new VBox();
        billLayout.getChildren().addAll(chooseCustomer, custTable);

        VBox ordersLayout = new VBox();
        ordersLayout.getChildren().addAll(ordersLabel, ordersTable);

        VBox foodItemLayout = new VBox();
        foodItemLayout.getChildren().addAll(foodItemLabel, foodsTable, buttonsLayout);

        HBox viewOrdersLayout = new HBox();
        viewOrdersLayout.getChildren().addAll(billLayout, ordersLayout, foodItemLayout);

        BorderPane sceneLayout = new BorderPane();
        sceneLayout.setTop(getBillLayout);
        sceneLayout.setCenter(viewOrdersLayout);
        sceneLayout.setBottom(backButton);

        return new Scene(sceneLayout, 800, 500);
    }

    /**
     * Sets the chosen order
     * @param order order that is being used
     */
    private void setChosenOrder(Order order) {
        chosenOrder = order;
    }

    /**
     * Get the chosen order
     * @return the chosen order
     */
    private Order getChosenOrder() {
        return chosenOrder;
    }

    /**
     * Sets the chosen food item
     * @param food food item that is used
     */
    private void setChosenFoodItem(Food food) {
        chosenFoodItem = food;
    }

    /**
     * Get the chosen food item
     * @return the food item
     */
    private Food getChosenFoodItem() {
        return chosenFoodItem;
    }

    private void getReturnScene(Server server, Table table, Order order) {
        Label reasonLabel = new Label("Reason: ");
        TextField reasonInput = new TextField();

        HBox reasonLayout = new HBox();
        reasonLayout.getChildren().addAll(reasonLabel, reasonInput);
        reasonLayout.setSpacing(10);

        Button returnButton = new Button("Return");
        returnButton.setOnAction(event -> {
            server.deleteOldFoodOrder(order.getId(), getChosenFoodItem(), reasonInput.getText());
            returnButton.getScene().getWindow().hide();
        });

        VBox innerLayout = new VBox();
        innerLayout.getChildren().addAll(reasonLayout, returnButton);

        StackPane sceneLayout = new StackPane();
        sceneLayout.getChildren().addAll(innerLayout);

        Scene scene = new Scene(sceneLayout, 300, 200);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Confirm return");
        stage.setScene(scene);
        stage.show();
    }
}
