import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;

public class OverviewScenes {

    private Restaurant restaurant;
    private Stage window;
    private ObservableList<Employee> employeeObservableList = FXCollections.observableArrayList();

    // --------------------- General Scene Helper Method --------------------- //

    /**
     * Returns the choice made when user clicked on the dropdown menu.
     *
     * @param choiceBox ChoiceBox user chose from
     * @return the value chosen by user
     */
    private String getChoice(ChoiceBox<String> choiceBox) {
        return choiceBox.getValue();
    }

    private void getMessageScene(String message) {
        Label messageLabel = new Label(message);

        // Setup okButton to close after reading message.
        Button okButton = new Button("OK");
        okButton.setOnAction(e -> okButton.getScene().getWindow().hide());

        VBox layout = new VBox();
        layout.setSpacing(10);
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(messageLabel, okButton);

        StackPane sceneLayout = new StackPane();
        sceneLayout.getChildren().addAll(layout);
        Scene scene = new Scene(sceneLayout, 300, 200);

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Message");
        stage.setScene(scene);
        stage.show();
    }

    // --------------------------- Homepage Scene ---------------------------- \\

    public Scene getHomePage(Stage window, Restaurant restaurant) {
        this.window = window;
        this.restaurant = restaurant;

        Text titleText = new Text("Homepage");

        //Setup Employees Button
        Button employeeButton = new Button("Employee Setup");
        employeeButton.setOnAction(e -> window.setScene(getSetupScene(window)));

        //Setup Actions Button
        //Button actionButton = new Button("Action");
        //actionButton.setOnAction(e -> window.setScene(getActionScene(window)));

        //Setup Employee Tracker Button
        Button trackerButton = new Button("Employee Tracker");
        //actionButton.setOnAction(e -> window.setScene(getTrackerScene(window)));


        VBox layout = new VBox();
        layout.setSpacing(10);
        //layout.getChildren().addAll(employeeButton, actionButton, trackerButton);
        layout.setAlignment(Pos.CENTER);

        BorderPane sceneLayout = new BorderPane();
        sceneLayout.setAlignment(titleText, Pos.CENTER);
        sceneLayout.setTop(titleText);
        sceneLayout.setCenter(layout);

        return new Scene(sceneLayout, 800, 500);


    }

    // --------------------- Restaurant Setup Scene Builder --------------------- //

    public Scene getSetupScene(Stage window){

        Text setupText = new Text("Setup");

        Label idLabel = new Label("ID");
        Label typeLabel = new Label("Type");
        final TextField idText = new TextField(); // id input

        // Setup drop down menu for Employee Type
        ChoiceBox<String> employeeTypeList = new ChoiceBox<>();
        employeeTypeList.getItems().addAll("Manager", "Cook", "Server");
        employeeTypeList.setValue("Manager" );

        // Setup drop down menu for Cook Type
        ChoiceBox<String> cookTypeList = new ChoiceBox<>();
        cookTypeList.getItems().addAll("Main", "Dessert", "Appetizer", "Not Available");
        cookTypeList.setValue("Not Available");

        // Setup addButton
        Button addButton = new Button("Add");
        addButton.setOnAction(e -> addEmployee(getChoice(employeeTypeList), idText.getText(), getChoice(cookTypeList)));

        // Setup deleteButton
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> deleteEmployee(getChoice(employeeTypeList), idText.getText(), getChoice(cookTypeList)));

        // Setup doneButton
        Button doneButton = new Button("Done");
        doneButton.setOnAction(e -> window.setScene(getHomePage(window, restaurant)));

        // Setup TableView for Employee
        // Setup job column
        TableColumn<Employee, String> jobColumn = new TableColumn<>("Job");
        jobColumn.setMinWidth(200);
        jobColumn.setCellValueFactory(new PropertyValueFactory<>("job"));
        // Setup ID Column
        TableColumn<Employee, String> idColumn = new TableColumn<>("ID");
        idColumn.setMinWidth(200);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        // Setup Type Column
        TableColumn<Employee, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setMinWidth(200);
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        // Setup TableView
        TableView<Employee> table = new TableView<>();
        table.setItems(employeeObservableList);
        table.getColumns().addAll(jobColumn, idColumn, typeColumn);

        // For TextField and labels
        HBox textsLabelsFlowPane = new HBox();
        textsLabelsFlowPane.setSpacing(20);
        textsLabelsFlowPane.setAlignment(Pos.CENTER);
        textsLabelsFlowPane.getChildren().addAll(employeeTypeList, idLabel, idText, typeLabel, cookTypeList);

        // For buttons
        HBox buttonsFlowPane = new HBox();
        buttonsFlowPane.setSpacing(30);
        buttonsFlowPane.setAlignment(Pos.CENTER);
        buttonsFlowPane.getChildren().addAll(addButton, deleteButton, doneButton);

        // Layout for lower layout
        VBox innerLowerLayout = new VBox();
        innerLowerLayout.setSpacing(10);
        innerLowerLayout.getChildren().add(textsLabelsFlowPane);
        innerLowerLayout.getChildren().add(buttonsFlowPane);

        // Scene overall layout
        BorderPane sceneLayout = new BorderPane();
        sceneLayout.setBottom(innerLowerLayout);
        sceneLayout.setCenter(table);
        return new Scene(sceneLayout, 800, 500);
    }
    private void addEmployee(String employeeType, String id, String cookType){
        if (employeeType.equals("Cook") && cookType.equals("Not Available")){
            getMessageScene("Please select cook type.");
        } else if (!employeeType.equals("Cook") && !cookType.equals("Not Available")){
            getMessageScene(employeeType + " should not have a type.");
        } else if (!restaurant.isWorker(employeeType, id)) {
            Employee employee = this.restaurant.addEmployee(employeeType, id, cookType);
            if (employee != null) {
                employeeObservableList.add(employee);
            }
        } else {
            getMessageScene("Employee is already in the system.");
        }
    }

    private void deleteEmployee(String employeeType, String id, String cookType) {
        if (this.restaurant.deleteEmployee(employeeType, id)) {
            employeeObservableList.remove(getEmployee(employeeType, id, cookType));
        } else {
            getMessageScene("I.D does not exist. Cannot delete employee.");
        }
    }


    private Employee getEmployee(String employeeType, String id, String cookType) {
        for (Employee employee: employeeObservableList) {
            if (employee.getJob().equals(employeeType) && employee.getId().equals(id)){
                return employee;
            }
        }
        return null;
    }

    // -------------------------------- Tracker Scene -------------------------------------- \\

    public Scene getTrackerScene(Stage window) {

        Text titleText = new Text("Log in");
        BorderPane sceneLayout = new BorderPane();
        sceneLayout.setAlignment(titleText, Pos.CENTER);
        sceneLayout.setTop(titleText);
        return new Scene(sceneLayout, 800, 500);


    }

    /**
     *
     *

    // --------------------- First Page Scene Builder --------------------- //

    public Scene getFirstScene(Stage window, Restaurant restaurant) {
        this.window = window;
        this.restaurant = restaurant;

        Text titleText = new Text("Homepage");

        //Setup Employees Button
        Button employeeButton = new Button("Employee Setup");
        employeeButton.setOnAction(e -> window.setScene(getSetupScene(window)));

        //Setup Actions Button
        Button actionButton = new Button("Action");
        actionButton.setOnAction(e -> window.setScene(getRestaurantScene()));

        //Setup Employee Tracker Button
        Button trackerButton = new Button("Employee Tracker");
        trackerButton.setOnAction(e -> window.setScene(getTrackerScene(window)));


        VBox layout = new VBox();
        layout.setSpacing(10);
        layout.getChildren().addAll(employeeButton, actionButton, trackerButton);
        layout.setAlignment(Pos.CENTER);

        BorderPane sceneLayout = new BorderPane();
        sceneLayout.setAlignment(titleText, Pos.CENTER);
        sceneLayout.setTop(titleText);
        sceneLayout.setCenter(layout);

        return new Scene(sceneLayout, 800, 500);
    }

    // -------------------------------- Tracker Scene -------------------------------------- \\

    public Scene getTrackerScene(Stage window) {

        Text titleText = new Text("Log in");
        BorderPane sceneLayout = new BorderPane();
        sceneLayout.setAlignment(titleText, Pos.CENTER);
        sceneLayout.setTop(titleText);
        return new Scene(sceneLayout, 800, 500);


    }
     */





}
