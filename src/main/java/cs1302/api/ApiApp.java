package cs1302.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpRequest;
import java.net.URI;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

import java.lang.Thread;
import java.lang.Exception;
import java.lang.Class;

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

import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

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
    ListView<String> addressView;
    Text statusText;
    ObservableList<String> addresses;

    LocationResponse locationResponse;
    ChargeResult[] chargeResults;

    double referenceLongitude;
    double referenceLatitude;




    /** HTTP client. */
    public static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)           // uses HTTP protocol version 2 where possible
        .followRedirects(HttpClient.Redirect.NORMAL)  // always redirects, except from HTTPS to HTTP
        .build();                                     // builds and returns a HttpClient object

    /** Google {@code Gson} object for parsing JSON-formatted strings. */
    public static Gson GSON = new GsonBuilder()
        .setPrettyPrinting()                          // enable nice output when printing
        .create();                                    // builds and returns a Gson object


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

        customAddressButton.setOnAction((event) -> {
            addressField.setDisable(false);
            searchButton.setDisable(false);
        });
        currentLocationButton.setOnAction((event) -> {
            addressField.setDisable(true);
            searchButton.setDisable(false);
        });

        currentLocationButton.setToggleGroup(group);
        customAddressButton.setToggleGroup(group);



        addressView = new ListView<>();
        addresses = FXCollections.observableArrayList("");
        addressView.setItems(addresses);

        sideBar = new VBox(addressView);
        VBox.setVgrow(addressView, Priority.ALWAYS);

        addressField = new TextField();
        addressField.setDisable(true);

        Platform.runLater(() -> mapView = new WebView());

        statusPane = new HBox();

        resultView = new BorderPane();

        statusText = new Text("Find EV Chargers near you!");

        searchButton = new Button("search");
        searchButton.setDisable(true);

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

        searchButton.setOnAction((e) -> this.fetchLocation());

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

    /** Gets the location (longitude and latitude) we're referencing.
     */
    private void fetchLocation() {
        statusText.setText("Fetching Results...");
        String locationRequestString = "";
        if (currentLocationButton.isSelected()) {
            locationRequestString = "https://ipgeolocation.abstractapi.com/v1/?api_key=686c558080334cb3a12305d49fcf54c0";
        } else if (customAddressButton.isSelected()) {
            //custom address used
        }

        //final String REQUEST_STRING_FINAL = locationRequestString;

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(locationRequestString))
                 .build();

        Thread locationThread = new Thread (() -> {
            try {
                HttpResponse<String> response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
                locationResponse = GSON
                    .fromJson(response.body(), LocationResponse.class);
                referenceLongitude = locationResponse.longitude;
                referenceLatitude = locationResponse.latitude;
                this.fetchChargers(); // sends request to the chargeAPI

            } catch (Exception e) {
                System.err.println("Error occurred when sending the request for the location");
                e.printStackTrace();
            }
            statusText.setText("Find EV Chargers near you!");
        });
        locationThread.start();

    }

    /** Requests the charging stations from the api.
     */
    private void fetchChargers() {
        String chargerRequestString  = "https://api.openchargemap.io/v3/poi?maxresults=100";
        String latString = "&latitude=" + referenceLatitude;
        String longString = "&longitude=" + referenceLongitude;
        String keyString = "&key=8cd4a572-293c-4d05-bd8d-c2eb45cc553e";

        chargerRequestString += latString + longString + keyString;
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(chargerRequestString))
            .header("Accept", "application/json")
            .method("GET", HttpRequest.BodyPublishers.noBody())
            .build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());


            chargeResults = GSON
                .fromJson(response.body(), ChargeResult[].class);

            Platform.runLater(() -> {

                addresses = FXCollections.observableArrayList("");
                for (ChargeResult chargeResult: chargeResults) {
                    System.out.println(chargeResult.addressInfo);
                    if (chargeResult.addressInfo == null) {
                        continue;
                    }
                    System.out.println("continue passed");
                    String siteString = "";
                    siteString += chargeResult.addressInfo.addressLine1 + "\n";

                    if (chargeResult.addressInfo.addressLine2 != null) {
                        siteString += chargeResult.addressInfo.addressLine2 + "\n";
                    }

                    siteString += chargeResult.addressInfo.town + " "
                        + chargeResult.addressInfo.postCode + "\n";
                    siteString += chargeResult.addressInfo.country.title;

                    addresses.add(siteString);

                }

                addressView.setItems(addresses);

            });
        } catch (Exception e) {
            System.err.println("Error occurred when sending the request for the chargers");
            e.printStackTrace();
        }

    }

} // ApiApp
