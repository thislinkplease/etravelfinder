package src.Controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import Entity.Vehicle;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;
import src.Model.HibernateUtil;
import src.Model.NavigationHelper;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class UserInterface_vehiclesController {
	@FXML
	private CheckBox showDate;

	@FXML
	private DatePicker departureDate, date;

	@FXML
	private ScrollPane scrollPane;

	@FXML
	private TextField departure, destination;

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
	private void initialize() {
		date.visibleProperty().bind(showDate.selectedProperty());

		showDate.selectedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				date.setValue(null);
			}
		});
	}

	@FXML
	private void exchangeBtn(MouseEvent event) throws IOException {
		String currentDeparture = departure.getText();
		String currentDestination = destination.getText();
		departure.setText(currentDestination);
		destination.setText(currentDeparture);
	}

	@FXML
	private void searchBtn(MouseEvent event) throws IOException {
		String departureFrom = departure.getText();
		String arrivalTo = destination.getText();
		LocalDate departuredate = departureDate.getValue();
		LocalDate returnDate = date.getValue();

		if (departureFrom.isEmpty() || arrivalTo.isEmpty() || departuredate == null) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText(null);
			alert.setContentText("Please fill in all fields to search!");
			alert.showAndWait();
		} else {
			try (Session session = HibernateUtil.getSessionFactory().openSession()) {
				session.beginTransaction();

				// Query for departure vehicle
				String hqlDeparture = "FROM Vehicle v WHERE v.departureFrom LIKE :departureFrom AND v.arrivalTo LIKE :arrivalTo AND v.departureDate = :departuredate";
				Query<Vehicle> queryDeparture = session.createQuery(hqlDeparture, Vehicle.class);
				queryDeparture.setParameter("departureFrom", "%" + departureFrom + "%");
				queryDeparture.setParameter("arrivalTo", "%" + arrivalTo + "%");
				queryDeparture.setParameter("departuredate", departuredate);

				List<Vehicle> departureResults = queryDeparture.list();

				List<Vehicle> returnResults = null;
				if (returnDate != null) {
					// Query for return vehicle
					String hqlReturn = "FROM Vehicle v WHERE v.departureFrom LIKE :departureFrom AND v.arrivalTo LIKE :arrivalTo AND v.departureDate = :returnDate";
					Query<Vehicle> queryReturn = session.createQuery(hqlReturn, Vehicle.class);
					queryReturn.setParameter("departureFrom", "%" + arrivalTo + "%");
					queryReturn.setParameter("arrivalTo", "%" + departureFrom + "%");
					queryReturn.setParameter("returnDate", returnDate);

					returnResults = queryReturn.list();
				}

				session.getTransaction().commit();

				displayResults(departureResults, returnResults);
			} catch (HibernateException e) {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Database Error");
				alert.setHeaderText(null);
				alert.setContentText("An error occurred while accessing the database.");
				alert.showAndWait();
			}
		}
	}

	private void displayResults(List<Vehicle> departureResults, List<Vehicle> returnResults) {
		GridPane gridPane = new GridPane();
		gridPane.setHgap(10);
		gridPane.setVgap(10);
		gridPane.setPadding(new Insets(10)); // Padding cho gridPane

		int row = 0; // Hàng hiện tại
		int col = 0; // Cột hiện tại

		// Tạo một đối tượng NumberFormat cho tiền tệ
		NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());

		// Display departure vehicles
		for (Vehicle vehicle : departureResults) {
			HBox vehicleBox = createVehicleBox(vehicle, currencyFormat);
			gridPane.add(vehicleBox, col, row);
			col++;
			if (col == 2) { // Nếu đã đạt đến cột cuối của hàng, di chuyển sang hàng mới
				col = 0;
				row++;
			}

			vehicleBox.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2) {
					try {
						FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML_files/User/Vehicles-booking.fxml"));
						Parent root = loader.load();

						Vehicles_bookingController controller = loader.getController();
						controller.initData(
								new ImageView(new Image(new ByteArrayInputStream(vehicle.getImages()))),
								vehicle.getVehicleName(), vehicle.getVehicleNo(), vehicle.getDepartureFrom(), vehicle.getArrivalTo(),
								currencyFormat.format(vehicle.getAdultPrice()), vehicle.getAdultPrice(), vehicle.getChildPrice(),
								vehicle.getDescription(), vehicle.getDepartureDate(), vehicle.getDepartureTime(), vehicle.getUploadBy()
						);

						NavigationHelper.switchToScene2((Node) event.getSource(), root);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			});
		}

		// Display return vehicles if available
		if (returnResults != null) {
			for (Vehicle vehicle : returnResults) {
				HBox vehicleBox = createVehicleBox(vehicle, currencyFormat);
				gridPane.add(vehicleBox, col, row);
				col++;
				if (col == 2) { // Nếu đã đạt đến cột cuối của hàng, di chuyển sang hàng mới
					col = 0;
					row++;
				}

				vehicleBox.setOnMouseClicked(event -> {
					if (event.getClickCount() == 2) {
						try {
							FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML_files/User/Vehicles-booking.fxml"));
							Parent root = loader.load();

							Vehicles_bookingController controller = loader.getController();
							controller.initData(
									new ImageView(new Image(new ByteArrayInputStream(vehicle.getImages()))),
									vehicle.getVehicleName(), vehicle.getVehicleNo(), vehicle.getDepartureFrom(), vehicle.getArrivalTo(),
									currencyFormat.format(vehicle.getAdultPrice()), vehicle.getAdultPrice(), vehicle.getChildPrice(),
									vehicle.getDescription(), vehicle.getDepartureDate(), vehicle.getDepartureTime(), vehicle.getUploadBy()
							);

							NavigationHelper.switchToScene2((Node) event.getSource(), root);
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					}
				});
			}
		}

		scrollPane.setContent(gridPane);
	}

	private HBox createVehicleBox(Vehicle vehicle, NumberFormat currencyFormat) {
		HBox vehicleBox = new HBox(10); // Khoảng cách giữa các thành phần trong HBox
		vehicleBox.setPrefSize(592, 180);
		vehicleBox.setStyle(
				"-fx-background-color: white;" +
				"-fx-padding: 10px;" +
				"-fx-background-radius: 10px;"
		);
		vehicleBox.setPadding(new Insets(10));

		vehicleBox.setOnMouseEntered(event -> {
			vehicleBox.setScaleX(1.005);
			vehicleBox.setScaleY(1.005);
			vehicleBox.setStyle(
				"-fx-background-color: rgba(99,237,237,0.04);" +
				"-fx-background-radius: 10px;");
		});

		vehicleBox.setOnMouseExited(event -> {
			vehicleBox.setScaleX(1.0);
			vehicleBox.setScaleY(1.0);
			vehicleBox.setStyle(
				"-fx-background-color: #ffffff;" +
				"-fx-background-radius: 10px;");
		});

		ImageView imageView = new ImageView();
		if (vehicle.getImages() != null) {
			Image image = new Image(new ByteArrayInputStream(vehicle.getImages()));
			imageView.setImage(image);
			imageView.setFitWidth(150);
			imageView.setPreserveRatio(true);
		}
		vehicleBox.getChildren().add(imageView);

		Label vehicleName = new Label(vehicle.getVehicleName());
		vehicleName.setWrapText(true);
		vehicleName.setPrefSize(420, 20);
		vehicleName.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

		Label vehicleNoLabel = new Label("Vehicle No: " + vehicle.getVehicleNo());
		vehicleNoLabel.setStyle("-fx-text-fill: rgba(74,74,74,0.76);");

		Label fromtoLabel = new Label("Sector: " + vehicle.getDepartureFrom() + " ---- " + vehicle.getArrivalTo());
		fromtoLabel.setStyle("-fx-text-fill: rgba(74,74,74,0.76);");

		Label departureDateLabel = new Label("Departure Date: " + vehicle.getDepartureDate().toString());
		departureDateLabel.setStyle("-fx-text-fill: rgba(74,74,74,0.76);");

		Label departureTimeLabel = new Label("Departure Time: " + vehicle.getDepartureTime().toString());
		departureTimeLabel.setStyle("-fx-text-fill: rgba(74,74,74,0.76);");

		Label priceLabel = new Label(currencyFormat.format(vehicle.getAdultPrice()));
		priceLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #0090ff; -fx-font-weight: bold;");

		VBox detailsBox = new VBox(5, vehicleName, vehicleNoLabel, fromtoLabel, departureDateLabel, departureTimeLabel, priceLabel);
		vehicleBox.getChildren().add(detailsBox);

		return vehicleBox;
	}
}
