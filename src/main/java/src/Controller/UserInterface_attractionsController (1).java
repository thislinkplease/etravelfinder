package src.Controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import Entity.Attraction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import src.Model.HibernateUtil;
import src.Model.NavigationHelper;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import src.Model.SearchState_Attraction;

public class UserInterface_attractionsController {

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> category;

    @FXML
    private TextField minPrice;

    @FXML
    private TextField maxPrice;

    @FXML
    private ScrollPane scrollPane;

	@FXML
	private void hotelsBtn(MouseEvent event) throws IOException {
        NavigationHelper.switchToScene((Node) event.getSource(), "/FXML_files/User/UserInterface-hotels.fxml");
    }
	
	@FXML
	private void flightsBtn(MouseEvent event) throws IOException {
        NavigationHelper.switchToScene((Node) event.getSource(), "/FXML_files/User/UserInterface-flights.fxml");
    }
	
	@FXML
	private void vehiclesBtn(MouseEvent event) throws IOException {
        NavigationHelper.switchToScene((Node) event.getSource(), "/FXML_files/User/UserInterface-vehicles.fxml");
    }
	
	@FXML
	private void attractionsBtn(MouseEvent event) throws IOException {
        NavigationHelper.switchToScene((Node) event.getSource(), "/FXML_files/User/UserInterface-attractions.fxml");
    }

    @FXML
    public void initialize() {
        // Tạo danh sách các giá trị
        ObservableList<String> categories = FXCollections.observableArrayList(
                "Museum", "Park", "Historical Site", "Zoo", "Amusement Park"
        );

        // Thiết lập giá trị cho ComboBox
        category.setItems(categories);

        SearchState_Attraction searchStateAttraction = SearchState_Attraction.getInstance();
        if (searchStateAttraction.getDestination() != null) {
            searchField.setText(searchStateAttraction.getDestination());
            category.setValue(searchStateAttraction.getCategory());
            minPrice.setText(String.valueOf(searchStateAttraction.getMinPrice()));
            maxPrice.setText(String.valueOf(searchStateAttraction.getMaxPrice()));
            displayResults(searchStateAttraction.getSearchResults());
        }
    }

    @FXML
    private void searchBtn(MouseEvent event) throws IOException {
        String destination = searchField.getText();
        String selectedCategory = category.getValue();
        String minPriceText = minPrice.getText();
        String maxPriceText = maxPrice.getText();

        if (destination.isEmpty() || selectedCategory == null || selectedCategory.isEmpty() || minPriceText.isEmpty() || maxPriceText.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all fields to search!");
            alert.showAndWait();
        } else {
            try {
                Double min = Double.parseDouble(minPriceText);
                Double max = Double.parseDouble(maxPriceText);

                try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                    session.beginTransaction();

                    String hql = "FROM Attraction a WHERE a.attractionAddress LIKE :destination AND a.category = :category AND a.adultPrice BETWEEN :min AND :max";
                    Query<Attraction> query = session.createQuery(hql, Attraction.class);
                    query.setParameter("destination", "%" + destination + "%");
                    query.setParameter("category", selectedCategory);
                    query.setParameter("min", min);
                    query.setParameter("max", max);

                    List<Attraction> results = query.list();
                    session.getTransaction().commit();

                    SearchState_Attraction searchStateAttraction = SearchState_Attraction.getInstance();
                    searchStateAttraction.setSearchParameters(destination, selectedCategory, min, max);
                    searchStateAttraction.setSearchResults(results);

                    displayResults(results);
                } catch (HibernateException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Database Error");
                    alert.setHeaderText(null);
                    alert.setContentText("An error occurred while accessing the database.");
                    alert.showAndWait();
                }
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Input Error");
                alert.setHeaderText(null);
                alert.setContentText("Please enter valid numbers for min and max prices.");
                alert.showAndWait();
            }
        }
    }

    private void displayResults(List<Attraction> results) {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10)); // Padding cho gridPane

        int row = 0; // Hàng hiện tại
        int col = 0; // Cột hiện tại

        // Tạo một đối tượng NumberFormat cho tiền tệ
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());

        for (Attraction attraction : results) {
            HBox attractionBox = new HBox(10); // Khoảng cách giữa các thành phần trong HBox
            attractionBox.setPrefSize(580, 180);
            attractionBox.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-padding: 10px;" +
                    "-fx-background-radius: 10px;"
            );
            attractionBox.setPadding(new Insets(10));

            attractionBox.setOnMouseEntered(event -> {
                attractionBox.setScaleX(1.005);
                attractionBox.setScaleY(1.005);
                attractionBox.setStyle("-fx-background-color: rgba(99,237,237,0.04);" + "-fx-background-radius: 10px;");
            });

            attractionBox.setOnMouseExited(event -> {
                attractionBox.setScaleX(1.0);
                attractionBox.setScaleY(1.0);
                attractionBox.setStyle("-fx-background-color: #ffffff;" + "-fx-background-radius: 10px;");
            });

            ImageView imageView = new ImageView();
            if (attraction.getImages() != null) {
                Image image = new Image(new ByteArrayInputStream(attraction.getImages()));
                imageView.setImage(image);
                imageView.setFitWidth(150);
                imageView.setPreserveRatio(true);
            }
            attractionBox.getChildren().add(imageView);

            Label nameLabel = new Label(attraction.getAttractionName());
            nameLabel.setWrapText(true);
            nameLabel.setPrefSize(420,60);
            nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            Label addressLabel = new Label("Address: " + attraction.getAttractionAddress());
            addressLabel.setStyle("-fx-text-fill: rgba(74,74,74,0.76);");

            Label descriptionLabel = new Label("Description: " + getShortenedDescription(attraction.getDescription(), 100));
            descriptionLabel.setWrapText(true);
            descriptionLabel.setStyle("-fx-text-fill: rgba(74,74,74,0.76);");

            Label adultLabel = new Label(currencyFormat.format(attraction.getAdultPrice()));
            adultLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #0090ff; -fx-font-weight: bold;");

            VBox detailsBox = new VBox(5, nameLabel, addressLabel, descriptionLabel, adultLabel);
            attractionBox.getChildren().add(detailsBox);

            gridPane.add(attractionBox, col, row);
            // Di chuyển sang hàng và cột tiếp theo
            col++;
            if (col == 2) { // Nếu đã đạt đến cột cuối của hàng, di chuyển sang hàng mới
                col = 0;
                row++;
            }

            attractionBox.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML_files/User/Attractions-booking.fxml"));
                        Parent root = loader.load();

                        Attractions_bookingController controller = loader.getController();
                        Label address = new Label(attraction.getAttractionAddress());
                        Label descriptions = new Label(attraction.getDescription());
                        Label childLabel = new Label(currencyFormat.format(attraction.getChildPrice()));
                        controller.initData(imageView, nameLabel, address, descriptions, adultLabel, childLabel, attraction.getAdultPrice(), attraction.getChildPrice());

                        NavigationHelper.switchToScene2((Node) event.getSource() ,root);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        scrollPane.setContent(gridPane);
    }

    private String getShortenedDescription(String description, int maxLength) {
        if (description.length() > maxLength) {
            return description.substring(0, maxLength) + "...view more";
        } else {
            return description;
        }
    }

}
