import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * This class is a helper method for other scenes
 */
public class ScenesHelper {
    /**
     * This method sets a message scene with the message
     * @param message String of message
     */
    public static void getMessageScene(String message){
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setTextAlignment(TextAlignment.JUSTIFY);

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

    /**
     * Returns the choice made when user clicked on the dropdown menu.
     * @param choiceBox ChoiceBox user chose from
     * @return the value chosen by user
     */
    static String getChoice(ChoiceBox<String> choiceBox){
        return choiceBox.getValue();
    }

    /**
     * Checks if the Employee exists and sets the Employees attendance
     * @param empType Type of Employee
     * @param id Employee's id
     * @param password Employee's password
     * @param restaurant Restaurant
     * @return return true iff the Employee exists
     */
    public static boolean checkEmpPass(String empType, String id, String password, Restaurant restaurant) {
        Employee employee = restaurant.getWorker(empType, id);

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

    /**
     * This method Changes the password
     * @param empType type of Employee
     * @param id Employee's id
     * @param oldPass Employee's old password
     * @param newPass Employee's new password
     * @param confirmPass Confirmed password
     */
    public static void changePass(String empType, String id, String oldPass, String newPass, String confirmPass,
                                  Restaurant restaurant){
        if (!newPass.equals(confirmPass)){
            ScenesHelper.getMessageScene("You new password is not confirmed");
        } else if (newPass.equals("")) {
            ScenesHelper.getMessageScene("Password can't be and empty string");
        } else if (
                ScenesHelper.checkEmpPass(empType, id, oldPass, restaurant)
                ) {
            Employee employee = restaurant.getWorker(empType, id);
            employee.setPassword(oldPass, newPass);
            ScenesHelper.getMessageScene("You password has been changed");
        } else {
            ScenesHelper.getMessageScene("Invalid password");
        }

    }

}
