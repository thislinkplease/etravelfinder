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

public class Provider_flight_analytics {

    @FXML
    private TableView<Booking> tableView;

    @FXML
    private TableColumn<Booking, String> FlightNo;

    @FXML
    private TableColumn<Booking, String> UserName;

    @FXML
    private TableColumn<Booking, String> DepatureFrom;

    @FXML
    private TableColumn<Booking, String> Arrivalto;

    @FXML
    private TableColumn<Booking, LocalDate> DepartureTime;

    @FXML
    private TableColumn<Booking, Integer> NumberOfSeats;

    @FXML
    private TableColumn<Booking, LocalDate> BookingDate;

    @FXML
    private TableColumn<Booking, Double> TotalPrice;

    @FXML
    private DatePicker Fromdate;

    @FXML
    private DatePicker Todate;

    @FXML
    private TextField Search;

    @FXML
    private Label SoldTicket;

    @FXML
    private void uploadBtn(MouseEvent event) throws IOException {
        NavigationHelper.switchToScene((Node) event.getSource(), "/FXML_files/Provider/Flight-upload.fxml");
    }

    @FXML
    private void editBtn(MouseEvent event) throws IOException {
        NavigationHelper.switchToScene((Node) event.getSource(), "/FXML_files/Provider/Flight-edit.fxml");
    }

    @FXML
    private void analyticsBtn(MouseEvent event) throws IOException {
        NavigationHelper.switchToScene((Node) event.getSource(), "/FXML_files/Provider/Flight-analytics.fxml");
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
        LocalDate fromDate = Fromdate.getValue();
        LocalDate toDate = Todate.getValue();
        String searchQuery = Search.getText().trim();

        if (fromDate != null && toDate != null && !searchQuery.isEmpty()) {
            searchBookings(fromDate, toDate, searchQuery);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select dates and enter search query!");
            alert.showAndWait();
        }
    }

    private void searchBookings(LocalDate fromDate, LocalDate toDate, String searchQuery) {
        ObservableList<Booking> bookingsList = FXCollections.observableArrayList();
        int totalSoldTickets = 0;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Booking WHERE bookingDate BETWEEN :fromDate AND :toDate AND (flightNo LIKE :searchQuery OR UserName LIKE :searchQuery)";
            Query<Booking> query = session.createQuery(hql, Booking.class);
            query.setParameter("fromDate", fromDate);
            query.setParameter("toDate", toDate);
            query.setParameter("searchQuery", "%" + searchQuery + "%");
            List<Booking> results = query.list();

            for (Booking booking : results) {
                totalSoldTickets += booking.getNumberOfSeats();
            }

            bookingsList.addAll(results);
            tableView.setItems(bookingsList);

            SoldTicket.setText(String.valueOf(totalSoldTickets));
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
        FlightNo.setCellValueFactory(new PropertyValueFactory<>("attractionName"));
        UserName.setCellValueFactory(new PropertyValueFactory<>("userName"));
        DepatureFrom.setCellValueFactory(new PropertyValueFactory<>("departureFrom"));
        Arrivalto.setCellValueFactory(new PropertyValueFactory<>("arrivalTo"));
        DepartureTime.setCellValueFactory(new PropertyValueFactory<>("departureTime"));
        NumberOfSeats.setCellValueFactory(new PropertyValueFactory<>("numberOfSeats"));
        BookingDate.setCellValueFactory(new PropertyValueFactory<>("bookingDate"));
        TotalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
    }
}
