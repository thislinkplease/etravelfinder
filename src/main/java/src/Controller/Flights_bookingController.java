package src.Controller;

import Entity.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.time.LocalTime;
import java.time.OffsetDateTime;

public class Flights_bookingController {

    @FXML
    private ImageView imageView;

    @FXML
    private Label airlinesNameLabel, flightNoLabel, departureFromLabel, arrvialToLabel, priceFormatedLabel, departureDateLabel, departureTimeLabel, totalPrice;

    @FXML
    private TextArea description;

    @FXML
    private TextField numberOfAdult, numberOfChild;

    @FXML
    private ComboBox scAdult, scChild;

    private double adultPriceValue, childPriceValue;
    private String uploadby;

    public void initData(ImageView image, String name, String flightNo, String departureFrom, String arrvialTo, String priceFormated, String descripStr, LocalDate departureDate, LocalTime departureTime, String uploadBy) {
        imageView.setImage(image.getImage());
        airlinesNameLabel.setText(name);
        flightNoLabel.setText(flightNo);
        departureFromLabel.setText(departureFrom);
        arrvialToLabel.setText(arrvialTo);
        priceFormatedLabel.setText(priceFormated + " / Economy");
        description.setText(descripStr);
        departureDateLabel.setText(departureDate.toString());
        departureTimeLabel.setText(departureTime.toString());
        uploadby = uploadBy;

        // Lấy giá ban đầu
        adultPriceValue = fetchPrice(flightNo, "Economy Adult");
        childPriceValue = fetchPrice(flightNo, "Economy Child");

        scAdult.getSelectionModel().select("Economy Adult");
        scChild.getSelectionModel().select("Economy Child");

        scAdult.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateSeatPrices(flightNo, (String) newValue, "adult"));
        scChild.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateSeatPrices(flightNo, (String) newValue, "child"));

        numberOfAdult.textProperty().addListener((observable, oldValue, newValue) -> updateTotalPrice());
        numberOfChild.textProperty().addListener((observable, oldValue, newValue) -> updateTotalPrice());
    }

    private double fetchPrice(String flightNo, String seatClass) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        double price = 0.0;

        try {
            transaction = session.beginTransaction();
            String hql = "FROM SeatClass WHERE flightNo.flightNo = :flightNo AND seatClass = :seatClass";
            Query<SeatClass> query = session.createQuery(hql, SeatClass.class);
            query.setParameter("flightNo", flightNo);
            query.setParameter("seatClass", seatClass);
            SeatClass seatClassEntity = query.uniqueResult();
            if (seatClassEntity != null) {
                if (seatClass.contains("Adult")) {
                    price = seatClassEntity.getAdultPrice();
                } else {
                    price = seatClassEntity.getChildPrice();
                }
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
        return price;
    }

    private void updateSeatPrices(String flightNo, String seatClass, String type) {
        double price = fetchPrice(flightNo, seatClass);
        if ("adult".equals(type)) {
            adultPriceValue = price;
        } else {
            childPriceValue = price;
        }
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        try {
            int numAdult = numberOfAdult.getText().isEmpty() ? 0 : Integer.parseInt(numberOfAdult.getText());
            int numChild = numberOfChild.getText().isEmpty() ? 0 : Integer.parseInt(numberOfChild.getText());

            double total = (numAdult * adultPriceValue) + (numChild * childPriceValue);
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
        NavigationHelper.switchToScene((Node) event.getSource(), "/FXML_files/User/UserInterface-flights.fxml");
    }

    @FXML
    private void initialize() {
        ObservableList<String> seatclassA = FXCollections.observableArrayList(
                "Economy Adult", "Premium Economy Adult", "Business Adult", "First Class Adult"
        );

        ObservableList<String> seatclassC = FXCollections.observableArrayList(
                "Economy Child", "Premium Economy Child", "Business Child", "First Class Child"
        );

        scAdult.setItems(seatclassA);
        scChild.setItems(seatclassC);
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

            String hqlUser = "FROM UserInfor WHERE username = :username";
            Query<UserInfor> queryUser = session.createQuery(hqlUser, UserInfor.class);
            queryUser.setParameter("username", currentUsername);
            UserInfor userInfor = queryUser.uniqueResult();

            if (userInfor == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("User not found!");
                alert.showAndWait();
                return;
            }

            String flNo = flightNoLabel.getText();
            String hqlFlight = "FROM Flight WHERE flightNo = :flNo";
            Query<Flight> queryFlight = session.createQuery(hqlFlight, Flight.class);
            queryFlight.setParameter("flNo", flNo);
            Flight flight = queryFlight.uniqueResult();

            if (flight == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Flight not found!");
            }

            Booking booking = new Booking();
            booking.setFlightNo(flight);
            booking.setAirlinesName(airlinesNameLabel.getText());
            booking.setNumberOfSeats((short) (numAdult + numChild));
            booking.setDepartureFrom(departureFromLabel.getText());
            booking.setArrivalTo(arrvialToLabel.getText());
            booking.setDepartureDate(LocalDate.parse(departureDateLabel.getText()));
            booking.setBookingDate(OffsetDateTime.now());
            booking.setTotalprice(total);
            booking.setUsername(userInfor);
            booking.setUploadBy(uploadby);

            session.save(booking);
            transaction.commit();
            session.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Booking successful!");
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Booking failed!");
            alert.showAndWait();
        }
    }
}
