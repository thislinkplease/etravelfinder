package src.Controller;

import Entity.Attraction;
import Entity.Booking;
import Entity.UserInfor;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import src.Model.HibernateUtil;
import src.Model.LoginUtil;
import src.Model.NavigationHelper;

import java.io.IOException;
import java.time.OffsetDateTime;

public class Vehicles_bookingController {

    @FXML
    private ImageView imageView;

    @FXML
    private Label nameLabel, addressLabel, adultLabel, childLabel, totalPrice;

    @FXML
    private TextArea description;

    @FXML
    private TextField numberOfAdult, numberOfChild;

    private double adultPriceValue, childPriceValue;

    public void initData(ImageView image, Label name, Label address, Label descriptions, Label adultPrice, Label childPrice, double adultPriceValue, double childPriceValue) {
        imageView.setImage(image.getImage());
        nameLabel.setText(name.getText());
        addressLabel.setText(address.getText());
        description.setText(descriptions.getText());
        adultLabel.setText(adultPrice.getText()  + " / Adult");
        childLabel.setText(childPrice.getText()  + " / Child");

        // Lưu giá trị giá vé người lớn và trẻ em
        this.adultPriceValue = adultPriceValue;
        this.childPriceValue = childPriceValue;

        // Thêm listeners để cập nhật giá trị tổng giá mỗi khi thay đổi số lượng
        numberOfAdult.textProperty().addListener((observable, oldValue, newValue) -> updateTotalPrice());
        numberOfChild.textProperty().addListener((observable, oldValue, newValue) -> updateTotalPrice());
    }

    private void updateTotalPrice() {
        try {
            int numAdult = numberOfAdult.getText().isEmpty() ? 0 : Integer.parseInt(numberOfAdult.getText());
            int numChild = numberOfChild.getText().isEmpty() ? 0 : Integer.parseInt(numberOfChild.getText());

            double total = ((double)numAdult * adultPriceValue) + ((double)numChild * childPriceValue);
            totalPrice.setText(String.format("$%.2f", total));
        } catch (NumberFormatException e) {
            totalPrice.setText("$0.00");
        }
    }

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
    private void backtoSearch(MouseEvent event) throws IOException {
        NavigationHelper.switchToScene((Node) event.getSource(), "/FXML_files/User/UserInterface-attractions.fxml");
    }

    @FXML
    private void bookingBtn(MouseEvent event) {
        try {
            int numAdult, numChild;
            try {
                numAdult = numberOfAdult.getText().isEmpty() ? 0 : Integer.parseInt(numberOfAdult.getText());
                numChild = numberOfChild.getText().isEmpty() ? 0 : Integer.parseInt(numberOfChild.getText());
                if (numAdult <= 0 && numChild <= 0) {
                    throw new NumberFormatException("Number of people must be greater than zero");
                }
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Please enter a valid number of people!");
                alert.showAndWait();
                return;
            }

            double total = (numAdult * adultPriceValue) + (numChild * childPriceValue);

            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Booking");
            confirmAlert.setHeaderText(null);
            confirmAlert.setContentText(String.format("Number of Adults: %d\nNumber of Children: %d\nTotal Price: $%.2f\nDo you want to confirm the booking?", numAdult, numChild, total));

            if (confirmAlert.showAndWait().get() != ButtonType.OK) {
                return;
            }

            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = session.beginTransaction();

            String currentUsername = LoginUtil.loadLoginInfo();
            if (currentUsername == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("User is not logged in!");
                alert.showAndWait();
                return;
            }

            String hql = "FROM UserInfor WHERE username = :username";
            Query<UserInfor> query = session.createQuery(hql);
            query.setParameter("username", currentUsername);
            UserInfor userInfor = query.uniqueResult();

            if (userInfor == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("User not found!");
                alert.showAndWait();
                return;
            }

            String attractionName = nameLabel.getText();
            String hqlAttraction = "FROM Attraction WHERE attractionName = :attractionName";
            Query<Attraction> queryAttraction = session.createQuery(hqlAttraction);
            queryAttraction.setParameter("attractionName", attractionName);
            Attraction attraction = queryAttraction.uniqueResult();

            if (attraction == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Attraction not found!");
                alert.showAndWait();
                return;
            }

            Booking booking = new Booking();
            booking.setAttractionName(attraction);
            booking.setUsername(userInfor);
            booking.setNumberOfTickets((short) (numAdult + numChild));
            booking.setBookingDate(OffsetDateTime.now());
            booking.setTotalprice(total);
            booking.setUploadBy(attraction.getUploadBy());

            session.save(booking);
            transaction.commit();
            session.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Booking successful!");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Booking failed!");
            alert.showAndWait();
        }
    }
}
