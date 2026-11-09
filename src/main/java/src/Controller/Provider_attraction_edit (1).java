package src.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import src.Model.NavigationHelper;
import Entity.Attraction;
import src.Model.UserSession;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Provider_attraction_edit {
    String oldName;
    @FXML
    private NavigationHelper navigationHelper = new NavigationHelper();

    @FXML
    private TextField AdultPrice, ChildPrice, attractionAddress, attractionName;

    @FXML
    private ComboBox<String> category;

    @FXML
    private TextArea descriptionTA;

    @FXML
    private ImageView imageUpload;
    private File selectedImageFile;

    @FXML
    private MenuItem logout;

    @FXML
    private MenuButton menuBtn;

    private static SessionFactory factory;

    @FXML
    private void menuBtn(MouseEvent event) {

    }

    @FXML
    private void logout(ActionEvent event) {
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
    private void analyticBtn(MouseEvent event) throws IOException {
        NavigationHelper.switchToScene((Node) event.getSource(), "/FXML_files/Provider/Attractions-analytics.fxml");
    }

    public void initData (ImageView imageView, String name, String address, String Category, String description, double adultPrice, double childPrice) {
        imageUpload.setImage(imageView.getImage());
        attractionName.setText(name);
        attractionAddress.setText(address);
        category.setValue(Category);
        descriptionTA.setText(description);
        AdultPrice.setText(String.valueOf(adultPrice));
        ChildPrice.setText(String.valueOf(childPrice));

        oldName = name;
    }

    @FXML
    void imageUpload(MouseEvent event) throws IOException {
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
    private void initialize() {
        if (category == null) {
            // Log an error message or handle the situation as needed
            System.err.println("Error: category field is not injected.");
            return;
        }

        // Tạo danh sách các giá trị
        ObservableList<String> categories = FXCollections.observableArrayList(
                "Museum", "Park", "Historical Site", "Zoo", "Amusement Park"
        );

        // Thiết lập giá trị cho ComboBox
        category.setItems(categories);
    }

    @FXML
    private void cancel(MouseEvent event) throws IOException {
        NavigationHelper.switchToScene((Node) event.getSource(), "/FXML_files/Provider/Attractions-view.fxml");
    }

    @FXML
    private void Delete(MouseEvent event) {
        try {
            // Lấy tên của Attraction cần xóa từ giao diện người dùng
            String attractionNameToDelete = attractionName.getText();

            // Kiểm tra xem trường thông tin attractionName có rỗng hay không
            if (attractionNameToDelete.isEmpty()) {
                showAlert("Error", "Please enter the attraction name.");
                return; // Return early if the field is empty
            }

            // Khởi tạo SessionFactory và Session
            if (factory == null) {
                factory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
            }
            Session session = factory.openSession();
            Transaction tx = null;

            try {
                // Bắt đầu transaction
                tx = session.beginTransaction();

                // Tìm thực thể Attraction hiện tại trong cơ sở dữ liệu
                Attraction attractionToDelete = session.get(Attraction.class, attractionNameToDelete);

                if (attractionToDelete != null) {
                    // Xóa thực thể
                    session.delete(attractionToDelete);

                    // Commit transaction
                    tx.commit();
                    showAlertSuccess("Success", "Attractions deleted successful!");
                } else {
                    showAlert("Error", "Attractions deleted Failed!");
                    if (tx != null) tx.rollback();
                }

            } catch (HibernateException e) {
                if (tx != null) tx.rollback();
                showAlert("Error", "An error occurred while deleting the attraction. Please try again.");
                e.printStackTrace();
            } finally {
                session.close();
            }
        } catch (Exception e) {
            showAlert("Error", "An unexpected error occurred. Please try again.");
            e.printStackTrace();
        }

        NavigationHelper.switchToScene((Node) event.getSource(), "/FXML_files/Provider/Attractions-view.fxml");
    }

    @FXML
    private void Update(MouseEvent event) {
        try {
            String updatedName = attractionName.getText();
            String updatedAddress = attractionAddress.getText();
            String updatedCategory = category.getValue();
            String updatedDescription = descriptionTA.getText();
            String updatedAdultPriceStr = AdultPrice.getText();
            String updatedChildPriceStr = ChildPrice.getText();

            if (updatedName.isEmpty() || updatedAddress.isEmpty() || updatedCategory == null || updatedDescription.isEmpty() || updatedAdultPriceStr.isEmpty() || updatedChildPriceStr.isEmpty()) {
                showAlert("Error", "Please fill in all fields.");
                return;
            }

            double updatedAdultPrice;
            double updatedChildPrice;

            try {
                updatedAdultPrice = Double.parseDouble(updatedAdultPriceStr);
                updatedChildPrice = Double.parseDouble(updatedChildPriceStr);
            } catch (NumberFormatException e) {
                showAlert("Error", "Please enter valid numerical values for prices.");
                return;
            }

            // Chuyển đổi tệp hình ảnh đã chọn thành mảng byte
            byte[] updatedImageBytes = null;
            if (selectedImageFile != null) {
                updatedImageBytes = Files.readAllBytes(selectedImageFile.toPath());
            } else {
                showAlert("Error", "Please select an image!");
                return;
            }

            // Khởi tạo SessionFactory và Session
            if (factory == null) {
                factory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
            }
            Session session = factory.openSession();
            Transaction tx = null;

            try {
                // Bắt đầu transaction
                tx = session.beginTransaction();

                // Tìm thực thể Attraction hiện tại trong cơ sở dữ liệu
                Attraction existingAttraction = session.get(Attraction.class, oldName);

                if (existingAttraction != null) {
                    // Xóa thực thể cũ
                    session.delete(existingAttraction);

                    // Tạo thực thể mới với các giá trị cập nhật
                    Attraction newAttraction = new Attraction();
                    newAttraction.setAttractionName(updatedName);
                    newAttraction.setAttractionAddress(updatedAddress);
                    newAttraction.setCategory(updatedCategory);
                    newAttraction.setDescription(updatedDescription);
                    newAttraction.setAdultPrice(updatedAdultPrice);
                    newAttraction.setChildPrice(updatedChildPrice);
                    newAttraction.setUploadBy(UserSession.getInstance().getUserName());
                    if (updatedImageBytes != null) {
                        newAttraction.setImages(updatedImageBytes);
                    }

                    // Lưu thực thể mới vào cơ sở dữ liệu
                    session.save(newAttraction);

                    showAlertSuccess("Success", "Attractions updated successfully!");
                    // Commit transaction
                    tx.commit();
                } else {
                    showAlert("Error", "Attraction not found!");
                    if (tx != null) tx.rollback();
                }

            } catch (HibernateException e) {
                if (tx != null) tx.rollback();
                showAlert("Error", "An error occurred while updating the attraction. Please try again.");
                e.printStackTrace();
            } finally {
                session.close();
            }
        } catch (IOException e) {
            showAlert("Error", "An unexpected error occurred. Please try again.");
            e.printStackTrace();
        }
    }


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showAlertSuccess(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
