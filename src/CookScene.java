import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * This class represents the Cook Scene
 */
public class CookScene {

    /**
     * This method returns the Cook Scene
     * @param window Stage that is being used
     * @param cook cook that is used
     * @param restaurantScene restaurant scene that is being used
     * @return Cook scene
     */
    public static Scene getCookScene(Stage window, Cook cook, Scene restaurantScene){

        Text title = new Text("Cook");
        HBox ordersBox = new HBox();

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> window.setScene(restaurantScene));

        // Setup Orders this cook needs to prepare
        for (Order order: cook.getKitchen().getOrderList()) {
            Text orderNum = new Text("Order " + order.getId());
            Text tableNum = new Text("Table " + order.getTableNum());
            Label orderDetails = new Label();
            orderDetails.setText(order.getFoodList());
            orderDetails.setStyle("-fx-font: 14 arial;");

            VBox orderNameBox = new VBox();
            orderNameBox.setAlignment(Pos.CENTER);
            orderNameBox.getChildren().addAll(orderNum, tableNum);
            orderNameBox.setSpacing(5);

            HBox buttonBox = new HBox();
            buttonBox.setAlignment(Pos.CENTER);
            Button seeButton = new Button("Seen");
            seeButton.setStyle("-fx-font: 15 arial;");
            seeButton.setOnAction(e -> {
                ScenesHelper.getMessageScene(cook.hasSeenOrder(order));
                if (order.hasSeen(cook.getId())){
                    seeButton.setDisable(true);
                }
            });
            if (order.hasSeen(cook.getId())) {
                seeButton.setDisable(true);
            }

            Button prepButton = new Button("Prepare");
            prepButton.setStyle("-fx-font: 15 arial;");
            prepButton.setOnAction(e -> {
                ScenesHelper.getMessageScene(cook.prepFood(order));
                if (order.isFilled()) {
                    prepButton.setDisable(true);
                }
            });
            if (order.isFilled()) {
                prepButton.setDisable(true);
            }
            buttonBox.getChildren().addAll(seeButton, prepButton);

            VBox orderDetailsBox = new VBox();
            orderDetailsBox.setStyle("-fx-border-color: black;\n" +
                    "-fx-border-insets: 5;\n" +
                    "-fx-border-width: 1;\n");
            orderDetailsBox.setPrefWidth(200);
            orderDetailsBox.setAlignment(Pos.CENTER);
            orderDetailsBox.getChildren().addAll(orderNameBox, orderDetails, buttonBox);
            ordersBox.getChildren().add(orderDetailsBox);
        }

        ScrollPane ordersScrollPane = new ScrollPane();
        ordersScrollPane.setContent(ordersBox);

        BorderPane sceneLayout = new BorderPane();
        sceneLayout.setCenter(ordersScrollPane);
        sceneLayout.setTop(title);

        BorderPane.setAlignment(backButton, Pos.CENTER);
        sceneLayout.setBottom(backButton);

        return new Scene(sceneLayout, 800, 500);
    }
}
