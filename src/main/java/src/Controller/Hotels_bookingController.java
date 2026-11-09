package src.Controller;

import Entity.Attraction;
import Entity.Booking;
import Entity.Hotel;
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
import java.time.LocalDate;
import java.time.OffsetDateTime;

public class Hotels_bookingController {

    @FXML
    private ImageView imageView;

    @FXML
    private Label nameLabel, addressLabel, priceLabel, totalPrice;

    @FXML
    private DatePicker checkinDate, checkoutDate;

    @FXML
    private TextArea description;

    @FXML
    private TextField numberOfRooms;

    private double priceValue;

    public void initData(ImageView image, Label name, Label address, Label descriptions, Label price, double priceValue) {
        imageView.setImage(image.getImage());
        nameLabel.setText(name.getText());
        addressLabel.setText(address.getText());
        description.setText(descriptions.getText());
        priceLabel.setText(price.getText());

        // Lưu giá trị giá phòng
        this.priceValue = priceValue;

        // Thêm listeners để cập nhật giá trị tổng giá mỗi khi thay đổi số lượng
        numberOfRooms.textProperty().addListener((observable, oldValue, newValue) -> updateTotalPrice());
    }

    private void updateTotalPrice() {
        try {
            int numRooms = numberOfRooms.getText().isEmpty() ? 0 : Integer.parseInt(numberOfRooms.getText());

            double total = numRooms * priceValue;
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
        NavigationHelper.switchToScene((Node) event.getSource(), "/FXML_files/User/UserInterface-hotels.fxml");
    }

    @FXML
    private void bookingBtn(MouseEvent event) {
        try {
            int numRooms;
            try {
                numRooms = numberOfRooms.getText().isEmpty() ? 0 : Integer.parseInt(numberOfRooms.getText());
                if (numRooms <= 0) {
                    throw new NumberFormatException("Number of rooms must be greater than zero");
                }
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Please enter a valid number of rooms greater than zero!");
                alert.showAndWait();
                return;
            }

            double total = numRooms * priceValue;

            LocalDate checkin = checkinDate.getValue();
            LocalDate checkout = checkoutDate.getValue();

            if (checkin == null || checkout == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Please select check-in and check-out dates!");
                alert.showAndWait();
                return;
            }

            if (checkin.isBefore(LocalDate.now())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Check-in date cannot be before today!");
                alert.showAndWait();
                return;
            }

            if (checkout.isBefore(checkin)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Check-out date cannot be before check-in date!");
                alert.showAndWait();
                return;
            }

            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Booking");
            confirmAlert.setHeaderText(null);
            confirmAlert.setContentText(String.format("Number of Rooms: %d\nTotal Price: $%.2f\nDo you want to confirm the booking?", numRooms, total));

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

            String hotelName = nameLabel.getText();
            String hqlHotel = "FROM Hotel WHERE hotelName = :hotelName";
            Query<Hotel> queryHotel = session.createQuery(hqlHotel);
            queryHotel.setParameter("hotelName", hotelName);
            Hotel hotel = queryHotel.uniqueResult();

            if (hotel == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Attraction not found!");
                alert.showAndWait();
                return;
            }

            Booking booking = new Booking();
            booking.setHotelName(hotel);
            booking.setUsername(userInfor);
            booking.setNumberOfTickets((short) numRooms);
            booking.setBookingDate(OffsetDateTime.now());
            booking.setTotalprice(total);
            booking.setUploadBy(hotel.getUploadBy());

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
