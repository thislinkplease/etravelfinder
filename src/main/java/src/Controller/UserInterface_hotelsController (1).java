package src.Controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import Entity.Hotel;
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
import src.Model.SearchState_Hotel;

public class UserInterface_hotelsController {

    @FXML
    private TextField destination;

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
    private void initialize() {
        SearchState_Hotel searchStateHotel = SearchState_Hotel.getInstance();
        if (searchStateHotel.getDestination() != null) {
            destination.setText(searchStateHotel.getDestination());
            minPrice.setText(String.valueOf(searchStateHotel.getMinPrice()));
            maxPrice.setText(String.valueOf(searchStateHotel.getMaxPrice()));
            displayResults(searchStateHotel.getSearchResults());
        }
    }

    @FXML
    private void searchBtn(MouseEvent event) throws IOException {
        String des = destination.getText();
        String minPriceText = minPrice.getText();
        String maxPriceText = maxPrice.getText();

        if (des.isEmpty() || minPriceText.isEmpty() || maxPriceText.isEmpty()) {
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

                    String hql = "FROM Hotel a WHERE a.hotelAddress LIKE :des AND a.price BETWEEN :min AND :max";
                    Query<Hotel> query = session.createQuery(hql, Hotel.class);
                    query.setParameter("des", "%" + des + "%");
                    query.setParameter("min", min);
                    query.setParameter("max", max);

                    List<Hotel> results = query.list();
                    session.getTransaction().commit();

                    SearchState_Hotel searchStateHotel = SearchState_Hotel.getInstance();
                    searchStateHotel.setSearchParameters(des, min, max);
                    searchStateHotel.setSearchResults(results);

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

    private void displayResults(List<Hotel> results) {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10)); // Padding cho gridPane

        int row = 0; // Hàng hiện tại
        int col = 0; // Cột hiện tại

        // Tạo một đối tượng NumberFormat cho tiền tệ
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());

        for (Hotel hotel : results) {
            HBox hotelBox = new HBox(10); // Khoảng cách giữa các thành phần trong HBox
            hotelBox.setPrefSize(580, 180);
            hotelBox.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-padding: 10px;" +
                            "-fx-background-radius: 10px;"
            );
            hotelBox.setPadding(new Insets(10));

            hotelBox.setOnMouseEntered(event -> {
                hotelBox.setScaleX(1.005);
                hotelBox.setScaleY(1.005);
                hotelBox.setStyle("-fx-background-color: rgba(99,237,237,0.04);" + "-fx-background-radius: 10px;");
            });

            hotelBox.setOnMouseExited(event -> {
                hotelBox.setScaleX(1.0);
                hotelBox.setScaleY(1.0);
                hotelBox.setStyle("-fx-background-color: #ffffff;" + "-fx-background-radius: 10px;");
            });

            ImageView imageView = new ImageView();
            if (hotel.getImages() != null) {
                Image image = new Image(new ByteArrayInputStream(hotel.getImages()));
                imageView.setImage(image);
                imageView.setFitWidth(150);
                imageView.setPreserveRatio(true);
            }
            hotelBox.getChildren().add(imageView);

            Label nameLabel = new Label(hotel.getHotelName());
            nameLabel.setWrapText(true);
            nameLabel.setPrefSize(420,60);
            nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            Label addressLabel = new Label("Address: " + hotel.getHotelAddress());
            addressLabel.setStyle("-fx-text-fill: rgba(74,74,74,0.76);");

            Label descriptionLabel = new Label("Description: " + getShortenedDescription(hotel.getDescription(), 100));
            descriptionLabel.setWrapText(true);
            descriptionLabel.setStyle("-fx-text-fill: rgba(74,74,74,0.76);");

            Label priceLabel = new Label(currencyFormat.format(hotel.getPrice()));
            priceLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #0090ff; -fx-font-weight: bold;");

            VBox detailsBox = new VBox(5, nameLabel, addressLabel, descriptionLabel, priceLabel);
            hotelBox.getChildren().add(detailsBox);

            gridPane.add(hotelBox, col, row);
            // Di chuyển sang hàng và cột tiếp theo
            col++;
            if (col == 2) { // Nếu đã đạt đến cột cuối của hàng, di chuyển sang hàng mới
                col = 0;
                row++;
            }

            hotelBox.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML_files/User/Hotels-booking.fxml"));
                        Parent root = loader.load();

                        Hotels_bookingController controller = loader.getController();
                        Label address = new Label(hotel.getHotelAddress());
                        Label descriptions = new Label(hotel.getDescription());
                        double price = hotel.getPrice();
                        controller.initData(imageView, nameLabel, address, descriptions, priceLabel, price);

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
