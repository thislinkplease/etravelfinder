package src.Controller;

import Entity.Attraction;
import Entity.Booking;
import Entity.UserInfor;
import javafx.beans.property.SimpleStringProperty;
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
import src.Model.LoginUtil;
import src.Model.NavigationHelper;

import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

public class Provider_attraction_analytics {

    @FXML
    private TableView<Booking> tableView;

    @FXML
    private TableColumn<Booking, String> AttractionName;

    @FXML
    private TableColumn<Booking, String> UserName;

    @FXML
    private TableColumn<Booking, Integer> NumberOfTickets;

    @FXML
    private TableColumn<Booking, LocalDate> BookingDate;

    @FXML
    private TableColumn<Booking, Double> TotalPrice;

    @FXML
    private DatePicker fromDatePicker;

    @FXML
    private DatePicker toDatePicker;

    @FXML
    private TextField Search;

    @FXML
    private Label TotalRevenue, SoldTickets;

    @FXML
    private void uploadBtn(MouseEvent event) throws IOException {
        NavigationHelper.switchToScene((Node) event.getSource(), "/FXML_files/Provider/Attractions-upload.fxml");
    }

    @FXML
    private void editBtn(MouseEvent event) throws IOException {
        NavigationHelper.switchToScene((Node) event.getSource(), "/FXML_files/Provider/Attractions-view.fxml");
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
        // Implement logout functionality here
    }

    @FXML
    private void searchBtn(MouseEvent event) {
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();

        if (fromDate != null && toDate != null) {
            if (fromDate.isAfter(toDate.minusDays(2))) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("The 'From' date must be at least 2 days before the 'To' date!");
                alert.showAndWait();
            } else {
                searchAttractions(fromDate, toDate);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select both dates!");
            alert.showAndWait();
        }
    }

    private void searchAttractions(LocalDate fromDate, LocalDate toDate) {
        ObservableList<Booking> bookingList = FXCollections.observableArrayList();

        String currentUsername = LoginUtil.loadLoginInfo();
        if (currentUsername == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("User is not logged in!");
            alert.showAndWait();
            return;
        }

        OffsetDateTime fromDateTime = fromDate.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime toDateTime = toDate.atStartOfDay().atOffset(ZoneOffset.UTC);

        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            String hql = "FROM Booking WHERE bookingDate >= :fromDateTime AND bookingDate <= :toDateTime AND uploadBy = :currentUsername";
            if (!Search.getText().isEmpty()) {
                hql += " AND attractionName.attractionName LIKE :attractionName";
            }
            Query<Booking> query = session.createQuery(hql, Booking.class);
            query.setParameter("fromDateTime", fromDateTime);
            query.setParameter("toDateTime", toDateTime);
            query.setParameter("currentUsername", currentUsername);
            if (!Search.getText().isEmpty()) {
                query.setParameter("attractionName", "%" + Search.getText() + "%");
            }

            List<Booking> results = query.list();
            session.close();

            bookingList.addAll(results);
            tableView.setItems(bookingList);

            // Calculate totals
            int totalTickets = results.stream().mapToInt(Booking::getNumberOfTickets).sum();
            double totalPrice = results.stream().mapToDouble(Booking::getTotalprice).sum();

            // Update labels
            SoldTickets.setText(String.valueOf(totalTickets));
            TotalRevenue.setText(String.format("%.2f", totalPrice));
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Failed to search attractions!");
            alert.showAndWait();
        }
    }

    @FXML
    public void initialize() {
        AttractionName.setCellValueFactory(cellData -> {
            Booking booking = cellData.getValue();

            Attraction attraction = booking.getAttractionName();

            return new SimpleStringProperty(attraction.getAttractionName());
        });

        UserName.setCellValueFactory(cellData -> {
            Booking booking = cellData.getValue();

            UserInfor user = booking.getUsername();

            return new SimpleStringProperty(user.getUsername());
        });
        NumberOfTickets.setCellValueFactory(new PropertyValueFactory<>("numberOfTickets"));
        BookingDate.setCellValueFactory(new PropertyValueFactory<>("bookingDate"));
        TotalPrice.setCellValueFactory(new PropertyValueFactory<>("totalprice"));
    }
}
