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

public class Provider_vehicle_analytics {

    @FXML
    private TableView<Booking> tableView;

    @FXML
    private TableColumn<Booking, String> VehicleNo ;

    @FXML
    private TableColumn<Booking, String> UserName;

    @FXML
    private TableColumn<Booking, String> DepatureFrom;

    @FXML
    private TableColumn<Booking, String> Arrivalto;

    @FXML
    private TableColumn<Booking, Integer> NumberOfSeats;

    @FXML
    private TableColumn<Booking, LocalDate> DepartureTime;

    @FXML
    private TableColumn<Booking, LocalDate> BookingDate;

    @FXML
    private TableColumn<Booking, Double> TotalPrice;

    @FXML
    private TextField Search;

    @FXML
    private DatePicker Fromdate;

    @FXML
    private DatePicker Todate;

    @FXML
    private Label SoldTicket;

    @FXML
    private Label TicketRevenue;

    @FXML
    private void uploadBtn(MouseEvent event) throws IOException {
        NavigationHelper.switchToScene((Node) event.getSource(),"/FXML_files/Provider/Vehicle-upload.fxml");
    }

    @FXML
    private void editBtn(MouseEvent event) throws IOException {
        NavigationHelper.switchToScene((Node) event.getSource(), "/FXML_files/Provider/Vehicle-edit.fxml");
    }

    @FXML
    private void analyticsBtn(MouseEvent event) throws IOException {
        NavigationHelper.switchToScene((Node) event.getSource(), "/FXML_files/Provider/Vẹhicle-analytics.fxml");
    }

    @FXML
    private void profileOption() {
        // Implement profile option functionality here
    }

    @FXML
    private void logoutOption() {
        // Implement logout option functionality here
    }

    @FXML
    private void searchBtn(MouseEvent event) {
        LocalDate fromDate = Fromdate.getValue();
        LocalDate toDate = Todate.getValue();
        String attractionName = Search.getText().trim();

        if (fromDate != null && toDate != null && !attractionName.isEmpty()) {
            searchBookings(fromDate, toDate, attractionName);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select dates and enter attraction name!");
            alert.showAndWait();
        }
    }

    private void searchBookings(LocalDate fromDate, LocalDate toDate, String attractionName) {
        ObservableList<Booking> bookingsList = FXCollections.observableArrayList();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Booking WHERE bookingDate BETWEEN :fromDate AND :toDate AND attractionName LIKE :attractionName";
            Query<Booking> query = session.createQuery(hql, Booking.class);
            query.setParameter("fromDate", fromDate);
            query.setParameter("toDate", toDate);
            query.setParameter("attractionName", "%" + attractionName + "%");
            List<Booking> results = query.list();

            bookingsList.addAll(results);
            tableView.setItems(bookingsList);

            // Update the number of sold tickets
            int totalSoldTickets = results.stream().mapToInt(Booking::getNumberOfSeats).sum();
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
        // Initialize the table columns with the model class properties
        VehicleNo.setCellValueFactory(new PropertyValueFactory<>("attractionName"));
        UserName.setCellValueFactory(new PropertyValueFactory<>("userName"));
        DepatureFrom.setCellValueFactory(new PropertyValueFactory<>("departureFrom"));
        Arrivalto.setCellValueFactory(new PropertyValueFactory<>("arrivalTo"));
        NumberOfSeats.setCellValueFactory(new PropertyValueFactory<>("numberOfSeats"));
        DepartureTime.setCellValueFactory(new PropertyValueFactory<>("departureTime"));
        BookingDate.setCellValueFactory(new PropertyValueFactory<>("bookingDate"));
        TotalPrice.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
    }
}
