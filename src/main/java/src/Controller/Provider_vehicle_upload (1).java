package src.Controller;

import Entity.Vehicle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Provider_vehicle_upload {

    @FXML
    private TextField VehicleNo;

    @FXML
    private TextField VehicleName ;

    @FXML
    private ComboBox<String> DepatureFrom, ArrivalTo;

    @FXML
    private DatePicker DepartureDate;

    @FXML
    private TextField AdultPrice, ChildPrice;

    @FXML
    private TextField TotalSeat;

    @FXML
    private TextField DepartureTime;

    @FXML
    private TextArea description;


    @FXML
    private ImageView ImageUpload;
    private File selectedImageFile;

    @FXML
    private void analyticBtn(MouseEvent event) throws IOException {
        NavigationHelper.switchToScene((Node) event.getSource(), "/FXML_files/Provider/Vehicle-analytics.fxml");
    }
    @FXML
    private void editBtn(MouseEvent event) throws IOException {
        NavigationHelper.switchToScene((Node) event.getSource(), "/FXML_files/Provider/Vehicles-edit.fxml");
    }
    @FXML
    private void uploadBtn(MouseEvent event) throws IOException {
        NavigationHelper.switchToScene((Node) event.getSource(), "/FXML_files/Provider/Vehicles-upload.fxml");
    }
    @FXML
    private void cancel(MouseEvent event) throws IOException {
        NavigationHelper.switchToScene((Node) event.getSource(), "/FXML_files/Provider/Vehicles-upload.fxml");
    }

    @FXML
    private void imageUpload(MouseEvent event) throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        selectedImageFile = fileChooser.showOpenDialog(null);

        if (selectedImageFile != null) {
            Image image = new Image(selectedImageFile.toURI().toString());
            ImageUpload.setImage(image);
        } else {
            System.out.println("File selection cancelled.");
        }
    }

    @FXML
    public void initialize() {
        // Tạo danh sách các giá trị
        ObservableList<String> destinations = FXCollections.observableArrayList(
                "Hue", "Da Nang", "Ha Noi"
        );

        // Thiết lập giá trị cho ComboBox
        DepatureFrom.setItems(destinations);
        ArrivalTo.setItems(destinations);
    }

    @FXML
    private void upload(MouseEvent event) throws IOException {
        // Kiểm tra nếu các trường bắt buộc bị bỏ trống
        if (AdultPrice.getText().isEmpty() || ChildPrice.getText().isEmpty() || VehicleNo.getText().isEmpty() ||
                VehicleName.getText().isEmpty() || DepatureFrom.getValue() == null || ArrivalTo.getValue() == null ||
                DepartureDate.getValue() == null || DepartureTime.getText().isEmpty() || TotalSeat.getText().isEmpty() ||
                description.getText().isEmpty() || selectedImageFile == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all fields and select an image!");
            alert.showAndWait();
            return;
        }

        // Kiểm tra nếu địa điểm xuất phát và địa điểm đến giống nhau
        if (DepatureFrom.getValue().equals(ArrivalTo.getValue())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Departure location and arrival location cannot be the same!");
            alert.showAndWait();
            return;
        }

        // Kiểm tra nếu ngày xuất phát trước ngày hiện tại
        if (DepartureDate.getValue().isBefore(LocalDate.now())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Departure date cannot be before the current date!");
            alert.showAndWait();
            return;
        }


        try {
            Double adultPriceValue = Double.parseDouble(AdultPrice.getText());
            Double childPriceValue = Double.parseDouble(ChildPrice.getText());
            int TotalSeatValue = Integer.parseInt(TotalSeat.getText());
            String timeInput = DepartureTime.getText();

            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = null;

            try (InputStream inputStream = new FileInputStream(selectedImageFile)){
                byte[] imageBytes = inputStream.readAllBytes();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                LocalTime time = LocalTime.parse(timeInput, formatter);

                transaction = session.beginTransaction();

                Vehicle vehicle = new Vehicle();
                vehicle.setVehicleNo(VehicleNo.getText());
                vehicle.setVehicleName(VehicleName.getText());
                vehicle.setDepartureFrom(DepatureFrom.getValue());
                vehicle.setArrivalTo(ArrivalTo.getValue());
                vehicle.setDepartureDate(DepartureDate.getValue());
                vehicle.setDepartureTime(time);
                vehicle.setTotalSeats((short) TotalSeatValue);
                vehicle.setImages(imageBytes);
                vehicle.setAdultPrice(adultPriceValue);
                vehicle.setChildPrice(childPriceValue);
                vehicle.setDescription(description.getText());
                vehicle.setUploadBy(UserSession.getInstance().getUserName());

                session.save(vehicle);
                transaction.commit();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Vehicle uploaded successfully!");
                alert.showAndWait();
            } catch (DateTimeParseException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Invalid time format. Please enter time as HH:mm!");
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
