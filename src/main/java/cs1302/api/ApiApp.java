package cs1302.api;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.control.TitledPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.scene.text.Text;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TextField;
import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;

/**
 * REPLACE WITH NON-SHOUTING DESCRIPTION OF YOUR APP.
 */
public class ApiApp extends Application {
    Stage stage;
    Scene scene;
    VBox root;
    HBox addressPane;
    WebView mapView;
    BorderPane resultView;
    HBox statusPane;
    RadioButton currentLocationButton;
    RadioButton customAddressButton;
    ToggleGroup group;
    TextField addressField;

    VBox sideBar;
    Button searchButton;

    ListView<String> addresses;

    Text statusText;

    /**
     * Constructs an {@code ApiApp} object. This default (i.e., no argument)
     * constructor is executed in Step 2 of the JavaFX Application Life-Cycle.
     */
    public ApiApp() {
        root = new VBox();
    } // ApiApp

    /** {@inheritDoc}*/
    @Override
    public void init() {

        addressPane = new HBox();
        addressPane.setPadding(new Insets(10));
        addressPane.setSpacing(10);

        group = new ToggleGroup();
        currentLocationButton = new RadioButton("Current location");
        customAddressButton = new RadioButton("Custom address");
        currentLocationButton.setToggleGroup(group);
        customAddressButton.setToggleGroup(group);

        addresses = new ListView<>();
        sideBar = new VBox(addresses);
        VBox.setVgrow(addresses, Priority.ALWAYS);

        addressField = new TextField();

        Platform.runLater(() -> mapView = new WebView());

        statusPane = new HBox();

        resultView = new BorderPane();

        statusText = new Text("Find EV Chargers near you!");

        searchButton = new Button("search");

    }

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {

        this.stage = stage;

        // setup scene
        addressPane.getChildren().addAll(currentLocationButton,customAddressButton,addressField
            ,searchButton);
        HBox.setHgrow(addressField,Priority.ALWAYS);


        sideBar.setStyle("-fx-background-color: black;");
        resultView.setRight(sideBar);
        resultView.setCenter(mapView);
        statusPane.getChildren().addAll(statusText);
        root.getChildren().addAll(addressPane,resultView,statusPane);
        scene = new Scene(root);
        //scene.getStylesheets().add("main/java/cs1302/style/stylesheet.css");


        // setup stage
        stage.setTitle("Charger Finder");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> Platform.exit());
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();

    } // start

} // ApiApp
