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

import java.net.URLEncoder;

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
    CustomLocationResult[] customLocationResults;

    double referenceLongitude;
    double referenceLatitude;

    private String pinHTML;
    private String homePinHTML;

    private boolean appJustStarting;

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

        this.updateHomeString();

        appJustStarting = true;
    }

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {
        this.fetchLocation();
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

        mapView.getEngine().loadContent(this.getHTMLContent());

        scene = new Scene(root);


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
        String customSearch = "";
        if (currentLocationButton.isSelected() || customAddressButton.isSelected()) {
            statusText.setText("Fetching Results...");
        }
        HttpRequest tempRequest = null;

        if (currentLocationButton.isSelected() || appJustStarting) {
            String locationRequestString = "https://ipgeolocation.abstractapi.com/v1/?api_key=686c558080334cb3a12305d49fcf54c0";
            tempRequest = HttpRequest.newBuilder()
                .uri(URI.create(locationRequestString))
                .build();

        } else if (customAddressButton.isSelected()) {
            try {
                customSearch += "&q=" + URLEncoder.encode(addressField.getText(),"UTF-8");
            } catch (Exception e) {
                System.err.println("Error occurred encoding text from Search Field");
            }
            tempRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://us1.locationiq.com/v1/search?format=json&normalizeaddress=1&key=pk.97577c04e9ff4e46c58a046e0a801ef1"
                + customSearch))
                .header("accept", "application/json")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        }

        final HttpRequest request = tempRequest;
        Thread locationThread = new Thread (() -> {
            try {
                if (currentLocationButton.isSelected() || appJustStarting) {
                    HttpResponse<String> response = HTTP_CLIENT
                        .send(request, BodyHandlers.ofString());
                    locationResponse = GSON
                        .fromJson(response.body(), LocationResponse.class);
                    referenceLongitude = locationResponse.longitude;
                    referenceLatitude = locationResponse.latitude;
                    appJustStarting = false;
                } else if (customAddressButton.isSelected()) {
                    HttpResponse<String> response = HttpClient.newHttpClient()
                        .send(request, HttpResponse.BodyHandlers.ofString());
                    customLocationResults = GSON
                        .fromJson(response.body(),CustomLocationResult[].class);
                    referenceLongitude = Double.parseDouble(customLocationResults[0].lon);
                    referenceLatitude = Double.parseDouble(customLocationResults[0].lat);
                }

                this.updateHomeString();
                if (currentLocationButton.isSelected() || customAddressButton.isSelected()) {
                    this.fetchChargers(); // sends request to the chargeAPI
                }
            } catch (Exception e) {
                System.err.println("Error occurred when sending the request for the location");
                e.printStackTrace();
            }
            statusText.setText("Find EV Chargers near you!");
            Platform.runLater(() -> mapView.getEngine().loadContent(this.getHTMLContent()));
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
                pinHTML = "";
                addresses = FXCollections.observableArrayList("");
                int count = 0;
                for (ChargeResult chargeResult: chargeResults) {
                    count += 1;
                    if (chargeResult.addressInfo == null) {
                        continue;
                    }
                    String siteString = "";
                    siteString += chargeResult.addressInfo.addressLine1 + "\n";
                    if (chargeResult.addressInfo.addressLine2 != null) {
                        siteString += chargeResult.addressInfo.addressLine2 + "\n";
                    }
                    siteString += chargeResult.addressInfo.town + " "
                        + chargeResult.addressInfo.postCode + "\n";
                    siteString += chargeResult.addressInfo.country.title;

                    addresses.add(siteString);
                    pinHTML += " var pin" + count + " = new Microsoft.Maps.Pushpin(" +
                        "new Microsoft.Maps.Location(" + chargeResult.addressInfo.latitude + "," +
                         chargeResult.addressInfo.longitude + ")" + ",{" +
                        "title: '" + chargeResult.addressInfo.addressLine1 + "'," +
                        "subTitle:'" + chargeResult.addressInfo.postCode + "'," +
                        "text: '" +  count + "'" +
                        "});" +
                        "map.entities.push(pin" + count + ");";
                }
                addressView.setItems(addresses);
            });
        } catch (Exception e) {
            System.err.println("Error occurred when sending the request for the chargers");
            e.printStackTrace();
        }
    }


    /**Method to generate HTML content with Bing Maps script.
     * @return HTML Content.
     */
    private String getHTMLContent() {
        String apiKey = "AVPBunBLkii75k5RufNj~KXU2UokTuxbX7f2e7l8bMw~"
            + "AudjlcyLDVk8umkDz-ArbTKCWqYZw6DKrngwSMtWnIvgb3u4tg0laKDtg7WXgjDG";



        String HTMLString = "<!DOCTYPE html>"
            + "<html>"
            + "<head>"
            + "<title></title>"
            + "<meta charset=\"utf-8\" />"
            + "<script type='text/javascript' src='https://www.bing.com/api/maps/mapcontrol?callback=GetMap&key=" + apiKey + "' async defer></script>"
            + "<script type='text/javascript'>"
            + "function GetMap() {"
            + "    var map = new Microsoft.Maps.Map('#map', {"
            + "        credentials: '" + apiKey + "',"
            + "        center: new Microsoft.Maps.Location(" +
            referenceLatitude + ", " + referenceLongitude + "),"
            + "        mapTypeId: Microsoft.Maps.MapTypeId.road,"
            + "        zoom: 10,"
            + "        showMapTypeSelector: false"
            + "    });"
            + homePinHTML
            +  pinHTML
            + "}"
            + "</script>"
            + "</head>"
            + "<body onload=\"GetMap();\">"
            + "<div id=\"map\" style=\"position:relative;width:100%;height:100%;\"></div>"
            + "</body>"
            + "</html>";

        return HTMLString;
    }

    /** Helper.
     */
    private void updateHomeString() {
        homePinHTML = "var Home = new Microsoft.Maps.Pushpin(" +
              "new Microsoft.Maps.Location(" + referenceLatitude + "," +
              referenceLongitude + ")" + ",{" +
              "title: 'YOU ARE HERE'," +
              "color: 'red'" +
              "});" +
              "map.entities.push(Home);";

    }


} // ApiApp
