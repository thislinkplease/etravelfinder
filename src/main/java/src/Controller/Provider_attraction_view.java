package src.Controller;

import Entity.Attraction;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import src.Model.HibernateUtil;
import src.Model.LoginUtil;
import src.Model.NavigationHelper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class Provider_attraction_view {

    @FXML
    private ScrollPane scrollPane;
    public void menuBtn(MouseEvent mouseEvent) {}

    private NavigationHelper navigationHelper = new NavigationHelper();

    @FXML
    private MenuItem logout ;

    @FXML
    private void logout (ActionEvent event) {
        navigationHelper.switchToScene3(logout, "/FXML_files/Login.fxml");
    }
    @FXML
    private void uploadBtn(MouseEvent event) throws IOException {
        NavigationHelper.switchToScene((Node) event.getSource(), "/FXML_files/Provider/Attractions-upload.fxml");
    }

    @FXML
    private void editBtn(MouseEvent event) throws IOException {
        NavigationHelper.switchToScene((Node) event.getSource(), "/FXML_files/Provider/Attractions-view.fxml");
    }

    @FXML
    private void initialize() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            String currentUsername = LoginUtil.loadLoginInfo();
            if (currentUsername == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("User is not logged in!");
                alert.showAndWait();
                return;
            }

            String hql = "FROM Attraction a WHERE a.uploadBy LIKE :currentUsername";
            Query<Attraction> query = session.createQuery(hql, Attraction.class);
            query.setParameter("currentUsername", currentUsername);

            List<Attraction> results = query.list();
            session.getTransaction().commit();

            displayResults(results);
        } catch (HibernateException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred while accessing the database.");
            alert.showAndWait();
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
                attractionBox.setStyle(
                        "-fx-background-color: rgba(99,237,237,0.04);" +
                        "-fx-background-radius: 10px;");
            });

            attractionBox.setOnMouseExited(event -> {
                attractionBox.setScaleX(1.0);
                attractionBox.setScaleY(1.0);
                attractionBox.setStyle(
                        "-fx-background-color: #ffffff;" +
                        "-fx-background-radius: 10px;");
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
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML_files/Provider/Attractions-edit.fxml"));
                        Parent root = loader.load();

                        Provider_attraction_edit controller = loader.getController();

                        controller.initData(imageView, attraction.getAttractionName(), attraction.getAttractionAddress(), attraction.getCategory(), attraction.getDescription(), attraction.getAdultPrice(), attraction.getChildPrice());

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

    @FXML
    private void analyticsBtn(MouseEvent event) throws IOException {
        NavigationHelper.switchToScene((Node) event.getSource(), "/FXML_files/Provider/Attractions-analytics.fxml");
    }
}
