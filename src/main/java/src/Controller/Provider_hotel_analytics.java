package src.Controller;

import Entity.Booking;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.hibernate.Session;
import org.hibernate.query.Query;
import src.Model.HibernateUtil;
import src.Model.NavigationHelper;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class Provider_hotel_analytics {

    @FXML
    private TableView<Booking> tableView;

    @FXML
    private TableColumn<Booking, String> HotelName;

    @FXML
    private TableColumn<Booking, String> UserName;

    @FXML
    private TableColumn<Booking, LocalDate> CheckIn;

    @FXML
    private TableColumn<Booking, LocalDate> CheckOut;

    @FXML
    private TableColumn<Booking, LocalDate> BookingDate;

    @FXML
    private TableColumn<Booking, Double> TotalPrice;

    @FXML
    private DatePicker fromdate;

    @FXML
    private DatePicker Todate;

    @FXML
    private TextField Search;

    @FXML
    private void uploadBtn(MouseEvent event) throws IOException {
        NavigationHelper.switchToScene((Node) event.getSource(), "/FXML_files/Provider/Attractions-upload.fxml");
    }

    @FXML
    private void editBtn(MouseEvent event) throws IOException {
        NavigationHelper.switchToScene((Node) event.getSource(), "/FXML_files/Provider/Attractions-edit.fxml");
    }

    @FXML
    private void analyticsBtn(MouseEvent event) throws IOException {
        NavigationHelper.switchToScene((Node) event.getSource(), "/FXML_files/Provider/Attractions-analytics.fxml");
    }

    @FXML
    private void profileOption(MouseEvent event) {
        // Implement profile option functionality here
    }

    @FXML
    private void logoutOption(MouseEvent event) {

    }

    @FXML
    private void searchBtn(MouseEvent event) {
        LocalDate fromDate = fromdate.getValue();
        LocalDate toDate = Todate.getValue();
        String hotelName = Search.getText().trim();

        if (fromDate != null && toDate != null && !hotelName.isEmpty()) {
            searchBookings(fromDate, toDate, hotelName);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select dates and enter hotel name!");
            alert.showAndWait();
        }
    }

    private void searchBookings(LocalDate fromDate, LocalDate toDate, String hotelName) {
        ObservableList<Booking> bookingsList = FXCollections.observableArrayList();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Booking WHERE bookingDate BETWEEN :fromDate AND :toDate AND hotelName = :hotelName";
            Query<Booking> query = session.createQuery(hql, Booking.class);
            query.setParameter("fromDate", fromDate);
            query.setParameter("toDate", toDate);
            query.setParameter("hotelName", hotelName);
            List<Booking> results = query.list();

            bookingsList.addAll(results);
            tableView.setItems(bookingsList);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to search bookings!");
            alert.showAndWait();
        }
    }

    @FXML
    public void initialize() {
        // Initialize the table columns with the model class properties
        HotelName.setCellValueFactory(new PropertyValueFactory<>("hotelName"));
        UserName.setCellValueFactory(new PropertyValueFactory<>("userName"));
        CheckIn.setCellValueFactory(new PropertyValueFactory<>("checkIn"));
        CheckOut.setCellValueFactory(new PropertyValueFactory<>("checkOut"));
        BookingDate.setCellValueFactory(new PropertyValueFactory<>("bookingDate"));
        TotalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
    }
}
