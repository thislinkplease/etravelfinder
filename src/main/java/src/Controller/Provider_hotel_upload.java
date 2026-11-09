package src.Controller;

import Entity.Hotel;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import org.hibernate.Session;
import org.hibernate.Transaction;
import src.Model.HibernateUtil;
import src.Model.NavigationHelper;
import src.Model.UserSession;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Provider_hotel_upload {

    @FXML
    private TextField HotelName ;

    @FXML
    private TextField  Price;

    @FXML
    private TextField HotelAddress;

    @FXML
    private TextField Totalrooms;

    @FXML
    private TextArea description;



    @FXML
    private ImageView imageUpload;
    private File selectedImageFile;

    @FXML
    private void analyticBtn(MouseEvent event) throws IOException {
        NavigationHelper.switchToScene((Node) event.getSource(), "/FXML_files/Provider/Hotel-analytics.fxml");
    }

    @FXML
    private void editBtn(MouseEvent event) throws IOException {
        NavigationHelper.switchToScene((Node) event.getSource(), "/FXML_files/Provider/Hotel-upload.fxml");
    }

    @FXML
    private void uploadBtn(MouseEvent event) throws IOException {
        NavigationHelper.switchToScene((Node) event.getSource(), "/FXML_files/Provider/Hotel-edit.fxml");
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
    private void upload(MouseEvent event) throws IOException {
        if (HotelName.getText().isEmpty() || Price.getText().isEmpty()  ||HotelAddress.getText().isEmpty() || description.getText().isEmpty() || selectedImageFile == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all fields and select an image!");
            alert.showAndWait();
        } else {
            try {
                Double PriceValue = Double.parseDouble(Price.getText());
                int TotalRoomsValue = Integer.parseInt(Totalrooms.getText());


                Session session = HibernateUtil.getSessionFactory().openSession();
                Transaction transaction = null;

                try (InputStream inputStream = new FileInputStream(selectedImageFile)){
                    byte[] imageBytes = inputStream.readAllBytes();

                    transaction = session.beginTransaction();
                    Hotel hotel = new Hotel();
                    hotel.setHotelName(HotelName.getText());
                    hotel.setHotelAddress(HotelAddress.getText());
                    hotel.setImages(imageBytes);
                    hotel.setTotalRooms((short) TotalRoomsValue);
                    hotel.setPrice(PriceValue);
                    hotel.setDescription(description.getText());
                    hotel.setUploadBy(UserSession.getInstance().getUserName());

                    session.save(hotel);
                    transaction.commit();

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("Hotel uploaded successfully!");
                    alert.showAndWait();
                } catch (Exception e) {
                    if (transaction != null) {
                        transaction.rollback();
                    }
                    e.printStackTrace();

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Failed to upload hotel!");
                    alert.showAndWait();
                } finally {
                    session.close();
                }
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Price/Total Rooms must be a valid number!");
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void cancel(MouseEvent event) throws IOException {
        NavigationHelper.switchToScene((Node) event.getSource(), "/FXML_files/Provider/Hotel-upload.fxml");
    }
}
