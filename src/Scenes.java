import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.ArrayList;

/**
 * Scenes class sets up different scenes to be viewed in GUI
 */
public class Scenes {
    private Restaurant restaurant;
    private Stage window;
    private ObservableList<Employee> employeeObservableList = FXCollections.observableArrayList();
    private ManagerScene managerScene = new ManagerScene();
    private ServerScene serverScene = new ServerScene();

    // TODO: If manager change threshold and its less than quantity, it should be in request
    // --------------------- First Page Scene Builder --------------------- //

    /**
     * The first scene Scene to navigate through the GUI. This is the homepage for all scenes.
     * @param window the Stage that is being used
     * @param restaurant the Restaurant this Scene is using
     * @return the Homepage Scene
     */
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

        //Setup Menu Button
        Button menuButton = new Button("Menu");
        menuButton.setOnAction(e -> ScenesHelper.getMessageScene(restaurant.getMenu().generateMenu()));


        VBox layout = new VBox();
        layout.setSpacing(10);
        layout.getChildren().addAll(employeeButton, actionButton, trackerButton, menuButton);
        layout.setAlignment(Pos.CENTER);

        BorderPane sceneLayout = new BorderPane();
        sceneLayout.setAlignment(titleText, Pos.CENTER);
        sceneLayout.setTop(titleText);
        sceneLayout.setCenter(layout);

        return new Scene(sceneLayout, 800, 500);
    }


//----------------------------- log in system ------------------------------//
    /**
     * Tracks the attendance of Employees in the restaurant.
     * Employees can log in or log out by using their assigned password.
     * Their default password is "password", and can be changed.
     *
     * @param window the Stage this Scene is using
     * @return Scene that is built
     */

    public Scene getTrackerScene(Stage window) {

        Label idLabel = new Label("ID:");
        Label passwordLabel = new Label("Password:");

        //Setup homeButton
        Button homeButton = new Button("Homepage");
        homeButton.setOnAction(e -> window.setScene(getFirstScene(window, this.restaurant)));

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
        TableColumn<Employee, String> attendanceColumn = new TableColumn<>("Attendance");
        attendanceColumn.setMinWidth(200);
        attendanceColumn.setCellValueFactory(new PropertyValueFactory<>("attendance"));

        // Setup TableView
        TableView<Employee> table = new TableView<>();
        populateTable();
        table.setItems(employeeObservableList);
        populateTable();
        table.getColumns().addAll(jobColumn, idColumn, attendanceColumn);
        // Setup drop down menu for Employee Type
        ChoiceBox<String> employeeTypeList = new ChoiceBox<>();
        employeeTypeList.getItems().addAll("Manager", "Cook", "Server");
        employeeTypeList.setValue("Manager" );


        // For TextField and labels
        final TextField idText = new TextField(); // id input
        final PasswordField passwordText = new PasswordField(); // id input
        HBox textsLabelsFlowPane = new HBox();
        textsLabelsFlowPane.setSpacing(20);
        textsLabelsFlowPane.setAlignment(Pos.CENTER);
        textsLabelsFlowPane.getChildren().addAll(employeeTypeList, idLabel, idText, passwordLabel, passwordText);

        //Setup loginButton
        Button loginButton = new Button("login/logout");
        loginButton.setOnAction(e -> {
            checkEmpPass(ScenesHelper.getChoice(employeeTypeList), idText.getText(), passwordText.getText());
            window.setScene(getTrackerScene(window));
        });

        //Setup Change Password Button
        Button changeButton = new Button("Change Password");
        changeButton.setOnAction(e -> window.setScene(getChangePassword(window)));


        // For buttons
        HBox buttonsFlowPane = new HBox();
        buttonsFlowPane.setSpacing(30);
        buttonsFlowPane.setAlignment(Pos.CENTER);
        buttonsFlowPane.getChildren().addAll(homeButton, loginButton, changeButton);

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

    /**
     * Checks if the Employee exists and sets the Employees attendance
     * @param empType type of Employee
     * @param id id of the Employee
     * @param password Employee's password
     * @return return true iff the Employee exists
     */
    public boolean checkEmpPass(String empType, String id, String password) {
        Employee employee = this.restaurant.getWorker(empType, id);

        if (employee == null){
            ScenesHelper.getMessageScene("This employee does not exist");
            return false;
        }
        if (employee.checkPass(password)) {
            employee.setAttendance();
            return true;
        } else {
            ScenesHelper.getMessageScene("Invalid Password");
            return false;
        }
    }

    // --------------------- Change password Builder ---------------------------- //

    /**
     * This window gets the Scene of change password
     * @param window The Stage that is being used
     * @return Change Password Scene
     */
    public Scene getChangePassword (Stage window){

        Label idLabel = new Label("ID:");
        Label oldLabel = new Label("Old Password:");
        Label newLabel = new Label("New Password");
        Label confirmLabel = new Label("Confirm Password:");

        final TextField idText = new TextField();
        final PasswordField oldText = new PasswordField();
        final PasswordField newText = new PasswordField();
        final PasswordField confirmText = new PasswordField();

        // Setup drop down menu for Employee Type
        ChoiceBox<String> employeeTypeList = new ChoiceBox<>();
        employeeTypeList.getItems().addAll("Manager", "Cook", "Server");
        employeeTypeList.setValue("Manager" );

        //setup Change Password Button
        Button changeButton = new Button("Change Password");
        changeButton.setOnAction(e -> {
            changePass(ScenesHelper.getChoice(employeeTypeList), idText.getText(), oldText.getText(), newText.getText(),
                    confirmText.getText());
            window.setScene(getChangePassword(window));
        });

        //setup back Button
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> window.setScene(getTrackerScene(window)));

        // For TextField and labels
        VBox textsLabelsFlowPane = new VBox();
        textsLabelsFlowPane.setSpacing(20);
        textsLabelsFlowPane.setAlignment(Pos.CENTER);
        textsLabelsFlowPane.getChildren().addAll(employeeTypeList, idLabel, idText, oldLabel, oldText, newLabel,
                newText, confirmLabel, confirmText);

        // For buttons
        HBox buttonsFlowPane = new HBox();
        buttonsFlowPane.setSpacing(30);
        buttonsFlowPane.setAlignment(Pos.CENTER);
        buttonsFlowPane.getChildren().addAll(backButton, changeButton);

        // Layout for lower layout
        VBox innerLowerLayout = new VBox();
        innerLowerLayout.setSpacing(10);
        innerLowerLayout.getChildren().add(textsLabelsFlowPane);
        innerLowerLayout.getChildren().add(buttonsFlowPane);

        // Scene overall layout
        BorderPane sceneLayout = new BorderPane();
        sceneLayout.setCenter(innerLowerLayout);
        return new Scene(sceneLayout, 500, 500);

    }

    /**
     * This method Changes the password
     * @param empType type of Employee
     * @param id Employee's id
     * @param oldPass Employee's old password
     * @param newPass Employee's new password
     * @param confirmPass Confirmed password
     */

    public void changePass(String empType, String id, String oldPass, String newPass, String confirmPass){
        if (!newPass.equals(confirmPass)){
            ScenesHelper.getMessageScene("You new password is not confirmed");
        } else if (newPass.equals("")) {
            ScenesHelper.getMessageScene("Password can't be and empty string");
        } else if (checkEmpPass(empType, id, oldPass)) {
            Employee employee = this.restaurant.getWorker(empType, id);
            employee.setPassword(oldPass, newPass);
            ScenesHelper.getMessageScene("You password has been changed");
        } else {
            ScenesHelper.getMessageScene("Invalid password");
        }

    }
    
    // --------------------- Restaurant Setup Scene Builder --------------------- //

    /**
     * Get the Setup Scene of Restaurant
     * @param window The Stage that is being used
     * @return The Setup Scene of the Restaurant
     */
    private Scene getSetupScene(Stage window){

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
        addButton.setOnAction(e -> addEmployee(ScenesHelper.getChoice(employeeTypeList), idText.getText(),
                ScenesHelper.getChoice(cookTypeList)));

        // Setup deleteButton
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> deleteEmployee(ScenesHelper.getChoice(employeeTypeList), idText.getText()));

        // Setup doneButton
        Button doneButton = new Button("Done");
        doneButton.setOnAction(e -> window.setScene(getFirstScene(window, restaurant)));

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
        populateTable();
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

    /**
     * Adds Employees to the Employee Observable list
     */
    private void populateTable() {
        ArrayList<Employee> employeesList = new ArrayList<>();
        employeesList.addAll(restaurant.getCooks());
        employeesList.addAll(restaurant.getManagers());
        employeesList.addAll(restaurant.getServers());

        for (Employee employee: employeesList) {
            if (!employeeObservableList.contains(employee)) {
                employeeObservableList.add(employee);
            }
        }
    }

    /**
     * Adds Employees to the System
     * @param employeeType Type of Employee
     * @param id Employee's id
     * @param cookType Type of cook
     */
    private void addEmployee(String employeeType, String id, String cookType){
        if (id.equals("") || !id.chars().allMatch( Character::isDigit )){
            ScenesHelper.getMessageScene("Please enter a valid id.");
        } else if (employeeType.equals("Cook") && cookType.equals("Not Available")){
            ScenesHelper.getMessageScene("Please select cook type.");
        } else if (!employeeType.equals("Cook") && !cookType.equals("Not Available")){
            ScenesHelper.getMessageScene(employeeType + " should not have a type.");
        } else if (!restaurant.isWorker(employeeType, id)) {
            Employee employee = this.restaurant.addEmployee(employeeType, id, cookType);
            if (employee != null) {
                employeeObservableList.add(employee);
            }
        } else {
            ScenesHelper.getMessageScene("Employee is already in the system.");
        }
    }

    /**
     * This method deletes the Employee
     * @param employeeType Type of Employee
     * @param id Employee's id
     */
    private void deleteEmployee(String employeeType, String id) {
        if (this.restaurant.deleteEmployee(employeeType, id)) {
            employeeObservableList.remove(getEmployee(employeeType, id));
        } else {
            ScenesHelper.getMessageScene("I.D does not exist. Cannot delete employee.");
        }
    }

    /**
     * Get the Employee
     * @param employeeType Type of Employee
     * @param id Employee's id
     * @return the Employee
     */
    private Employee getEmployee(String employeeType, String id) {
        for (Employee employee: employeeObservableList) {
            if (employee.getJob().equals(employeeType) && employee.getId().equals(id)){
                return employee;
            }
        }
        return null;
    }



    // --------------------- Choose Employee/ID Scene Builder --------------------- //

    /**
     * This Restaurant Scene asks you to choose the Employee type and enter Employees id
     * @return Choose Employee Scene
     */
    private Scene getRestaurantScene(){
        // Setup drop down menu for Employee Type
        ChoiceBox<String> employeeTypeList = new ChoiceBox<>();
        employeeTypeList.setStyle("-fx-font: 15 arial;");
        employeeTypeList.getItems().addAll("Manager", "Cook", "Server");
        employeeTypeList.setValue("Manager" );

        Label idLabel = new Label("Id");
        idLabel.setStyle("-fx-font: 15 arial;");
        TextField idInput = new TextField();
        idInput.setStyle("-fx-font: 15 arial;");

        Button backButton = new Button("Back");
        backButton.setStyle("-fx-font: 15 arial;");
        backButton.setOnAction(e -> window.setScene(getFirstScene(window, restaurant)));

        Button selectButton = new Button("Select");
        selectButton.setStyle("-fx-font: 15 arial;");
        selectButton.setOnAction(e -> {
            if (idInput.getText().equals("")) {
                ScenesHelper.getMessageScene("Please enter id.");
            } else {
                window.setScene(getEmployeeScene(ScenesHelper.getChoice(employeeTypeList), idInput.getText()));
            }
        });

        HBox employeeInputBox = new HBox();
        employeeInputBox.setAlignment(Pos.CENTER);
        employeeInputBox.setSpacing(10);
        employeeInputBox.getChildren().addAll(employeeTypeList, idLabel, idInput);

        VBox innerLayout = new VBox();
        innerLayout.setAlignment(Pos.CENTER);
        innerLayout.setSpacing(10);
        innerLayout.getChildren().addAll(employeeInputBox, selectButton);

        BorderPane sceneLayout = new BorderPane();
        sceneLayout.setCenter(innerLayout);
        BorderPane.setAlignment(backButton, Pos.CENTER);
        sceneLayout.setBottom(backButton);

        return new Scene(sceneLayout, 800, 500);
    }

    /**
     * This method opens the Employee Scene depending on the Employee Type and Employee id
     * @param employeeType Type of Employee
     * @param id Employee's id
     * @return Employee Scene
     */
    private Scene getEmployeeScene(String employeeType, String id) {
        if (restaurant.isWorker(employeeType, id)) {
            Employee employee = restaurant.getWorker(employeeType, id);
            if (employee.attendance.equals("Present")) {
                switch (employeeType) {
                    case "Manager":
                        return managerScene.getManagerScene(window, (Manager) employee, getRestaurantScene(),
                                this.restaurant);
                    case "Cook":
                        return CookScene.getCookScene(window, (Cook) employee, getRestaurantScene());
                    case "Server":
                        return serverScene.getServerScene(window, (Server) employee, restaurant, getRestaurantScene());
                }
            } else {
                ScenesHelper.getMessageScene("Employee is not logged in.");
            }
        } else {
            ScenesHelper.getMessageScene("Not a valid worker.");
        }
        return getRestaurantScene();
    }
}
