package src.Controller;

import Entity.Attraction;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.hibernate.Session;
import org.hibernate.Transaction;
import src.Model.HibernateUtil;
import src.Model.NavigationHelper;
import src.Model.UserSession;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Provider_attractions_upload {

    @FXML
    private TextField attractionName;

    @FXML
    private TextField AdultPrice, ChildPrice;

    @FXML
    private TextField attractionAddress;

    @FXML
    private TextArea desciption;

    @FXML
    private ComboBox<String> category;

    @FXML
    private ImageView imageUpload;
    private File selectedImageFile;

    @FXML
    private void analyticBtn(MouseEvent event) throws IOException {
        NavigationHelper.switchToScene((Node) event.getSource(), "/FXML_files/Provider/Attractions-analytics.fxml");
    }
    @FXML
    private void editBtn(MouseEvent event) throws IOException {
        NavigationHelper.switchToScene((Node) event.getSource(), "/FXML_files/Provider/Attractions-view.fxml");
    }
    @FXML
    private void uploadBtn(MouseEvent event) throws IOException {
        NavigationHelper.switchToScene((Node) event.getSource(), "/FXML_files/Provider/Attractions-upload.fxml");
    }
    @FXML
    private void cancel(MouseEvent event) throws IOException {
        NavigationHelper.switchToScene((Node) event.getSource(), "/FXML_files/Provider/Attractions-upload.fxml");
    }

    @FXML
    private void imageUpload(MouseEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        selectedImageFile = fileChooser.showOpenDialog(null);

        if (selectedImageFile != null) {
            Image image = new Image(selectedImageFile.toURI().toString());
            imageUpload.setImage(image);
        } else {
            System.out.println("File selection cancelled.");
        }
    }

    @FXML
    public void initialize() {
        // Tạo danh sách các giá trị
        ObservableList<String> categories = FXCollections.observableArrayList(
                "Museum", "Park", "Historical Site", "Zoo", "Amusement Park"
        );

        // Thiết lập giá trị cho ComboBox
        category.setItems(categories);
    }

    @FXML
    private void upload(MouseEvent event) throws IOException {
        if (attractionName.getText().isEmpty() || AdultPrice.getText().isEmpty() || ChildPrice.getText().isEmpty() || attractionAddress.getText().isEmpty() || desciption.getText().isEmpty() || category.getValue() == null || selectedImageFile == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all fields and select an image!");
            alert.showAndWait();
        } else {
            try {
                Double adultPriceValue = Double.parseDouble(AdultPrice.getText());
                Double childPriceValue = Double.parseDouble(ChildPrice.getText());

                Session session = HibernateUtil.getSessionFactory().openSession();
                Transaction transaction = null;

                try (InputStream inputStream = new FileInputStream(selectedImageFile)){
                    byte[] imageBytes = inputStream.readAllBytes();

                    transaction = session.beginTransaction();
                    Attraction attraction = new Attraction();
                    attraction.setAttractionName(attractionName.getText());
                    attraction.setAdultPrice(adultPriceValue);
                    attraction.setChildPrice(childPriceValue);
                    attraction.setAttractionAddress(attractionAddress.getText());
                    attraction.setDescription(desciption.getText());
                    attraction.setCategory(category.getValue());
                    attraction.setImages(imageBytes);
                    attraction.setUploadBy(UserSession.getInstance().getUserName());

                    session.save(attraction);
                    transaction.commit();

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("Attraction uploaded successfully!");
                    alert.showAndWait();
                } catch (Exception e) {
                    if (transaction != null) {
                        transaction.rollback();
                    }
                    e.printStackTrace();

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Failed to upload attraction!");
                    alert.showAndWait();
                } finally {
                    session.close();
                }
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Price must be a valid number!");
                alert.showAndWait();
            }
        }
    }
}
