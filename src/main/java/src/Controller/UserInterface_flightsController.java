package src.Controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Entity.Attraction;
import Entity.Flight;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import src.Model.SearchState_Attraction;
import src.Model.SearchState_Flight;

public class UserInterface_flightsController {
	private static SearchState_Flight searchState;
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
    private void initialize() throws IOException {
		date.visibleProperty().bind(showDate.selectedProperty());

		showDate.selectedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				date.setValue(null);
			}
		});

		if (searchState != null) {
			departure.setText(searchState.getDepartureFrom());
			destination.setText(searchState.getArrivalTo());
			departureDate.setValue(searchState.getDepartureDate());
			date.setValue(searchState.getReturnDate());
			showDate.setSelected(searchState.isShowDate());

			searchBtn(null);
		}
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
		searchState = new SearchState_Flight(
				departure.getText(),
				destination.getText(),
				departureDate.getValue(),
				date.getValue(),
				showDate.isSelected()
		);

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
			searchState = new SearchState_Flight(departureFrom, arrivalTo, departuredate, returnDate, showDate.isSelected());

			try (Session session = HibernateUtil.getSessionFactory().openSession()) {
				session.beginTransaction();

				// Query for departure flight
				String hqlDeparture = "FROM Flight f WHERE f.departureFrom LIKE :departureFrom AND f.arrivalTo LIKE :arrivalTo AND f.departureDate = :departuredate";
				Query<Flight> queryDeparture = session.createQuery(hqlDeparture, Flight.class);
				queryDeparture.setParameter("departureFrom", "%" + departureFrom + "%");
				queryDeparture.setParameter("arrivalTo", "%" + arrivalTo + "%");
				queryDeparture.setParameter("departuredate", departuredate);

				List<Flight> departureResults = queryDeparture.list();

				List<Flight> returnResults = null;
				if (returnDate != null) {
					// Query for return flight
					String hqlReturn = "FROM Flight f WHERE f.departureFrom LIKE :departureFrom AND f.arrivalTo LIKE :arrivalTo AND f.departureDate = :returnDate";
					Query<Flight> queryReturn = session.createQuery(hqlReturn, Flight.class);
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

	private void displayResults(List<Flight> departureResults, List<Flight> returnResults) {
		GridPane gridPane = new GridPane();
		gridPane.setHgap(10);
		gridPane.setVgap(10);
		gridPane.setPadding(new Insets(10)); // Padding cho gridPane

		int row = 0; // Hàng hiện tại
		int col = 0; // Cột hiện tại

		// Tạo một đối tượng NumberFormat cho tiền tệ
		NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());

		// Display departure flights
		for (Flight flight : departureResults) {
			HBox flightBox = createFlightBox(flight, currencyFormat);
			gridPane.add(flightBox, col, row);
			col++;
			if (col == 2) { // Nếu đã đạt đến cột cuối của hàng, di chuyển sang hàng mới
				col = 0;
				row++;
			}

			flightBox.setOnMouseClicked(event -> {
				if(event.getClickCount() == 2) {
					try {
						FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML_files/User/Flights-booking.fxml"));
						Parent root = loader.load();

						Flights_bookingController controller = loader.getController();
						controller.initData(
								new ImageView(new Image(new ByteArrayInputStream(flight.getImages()))),
								flight.getAirlinesName(), flight.getFlightNo(), flight.getDepartureFrom(),  flight.getArrivalTo(),
								currencyFormat.format(getAdultPriceByFlightNoAndSeatClass(flight.getFlightNo(), "Economy Adult")),
								flight.getDescription(), flight.getDepartureDate(), flight.getDepartureTime(), flight.getUploadBy()
						);

						NavigationHelper.switchToScene2((Node) event.getSource(), root);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			});
		}

		// Display return flights if available
		if (returnResults != null) {
			for (Flight flight : returnResults) {
				HBox flightBox = createFlightBox(flight, currencyFormat);
				gridPane.add(flightBox, col, row);
				col++;
				if (col == 2) { // Nếu đã đạt đến cột cuối của hàng, di chuyển sang hàng mới
					col = 0;
					row++;
				}

				flightBox.setOnMouseClicked(event -> {
					if(event.getClickCount() == 2) {
						try {
							FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML_files/User/Flights-booking.fxml"));
							Parent root = loader.load();

							Flights_bookingController controller = loader.getController();
							controller.initData(
									new ImageView(new Image(new ByteArrayInputStream(flight.getImages()))),
									flight.getAirlinesName(), flight.getFlightNo(), flight.getDepartureFrom(),  flight.getArrivalTo(),
									currencyFormat.format(getAdultPriceByFlightNoAndSeatClass(flight.getFlightNo(), "Economy Adult")),
									flight.getDescription(), flight.getDepartureDate(), flight.getDepartureTime(), flight.getUploadBy()
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

	private HBox createFlightBox(Flight flight, NumberFormat currencyFormat) {
		HBox flightBox = new HBox(10); // Khoảng cách giữa các thành phần trong HBox
		flightBox.setPrefSize(592, 180);
		flightBox.setStyle(
				"-fx-background-color: white;" +
				"-fx-padding: 10px;" +
				"-fx-background-radius: 10px;"
		);
		flightBox.setPadding(new Insets(10));

		flightBox.setOnMouseEntered(event -> {
			flightBox.setScaleX(1.005);
			flightBox.setScaleY(1.005);
			flightBox.setStyle(
					"-fx-background-color: rgba(99,237,237,0.04);" +
					"-fx-background-radius: 10px;");
		});

		flightBox.setOnMouseExited(event -> {
			flightBox.setScaleX(1.0);
			flightBox.setScaleY(1.0);
			flightBox.setStyle(
					"-fx-background-color: #ffffff;" +
					"-fx-background-radius: 10px;");
		});

		ImageView imageView = new ImageView();
		if (flight.getImages() != null) {
			Image image = new Image(new ByteArrayInputStream(flight.getImages()));
			imageView.setImage(image);
			imageView.setFitWidth(150);
			imageView.setPreserveRatio(true);
		}
		flightBox.getChildren().add(imageView);

		Label airlinesName = new Label(flight.getAirlinesName());
		airlinesName.setWrapText(true);
		airlinesName.setPrefSize(420, 20);
		airlinesName.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

		Label flightNoLabel = new Label("Flight No: " + flight.getFlightNo());
		flightNoLabel.setStyle("-fx-text-fill: rgba(74,74,74,0.76);");

		Label fromtoLabel = new Label("Sector: " + flight.getDepartureFrom() + " ---- " + flight.getArrivalTo());
		fromtoLabel.setStyle("-fx-text-fill: rgba(74,74,74,0.76);");

		Label departureDateLabel = new Label("Departure Date: " + flight.getDepartureDate().toString());
		departureDateLabel.setStyle("-fx-text-fill: rgba(74,74,74,0.76);");

		Label departureTimeLabel = new Label("Departure Time: " + flight.getDepartureTime().toString());
		departureTimeLabel.setStyle("-fx-text-fill: rgba(74,74,74,0.76);");

		Double adultPrice = getAdultPriceByFlightNoAndSeatClass(flight.getFlightNo(), "Economy Adult");
		String priceText = adultPrice != null ? currencyFormat.format(adultPrice) : "N/A";

		Label priceLabel = new Label(priceText);
		priceLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #0090ff; -fx-font-weight: bold;");

		VBox detailsBox = new VBox(5, airlinesName, flightNoLabel, fromtoLabel, departureDateLabel, departureTimeLabel,priceLabel);
		flightBox.getChildren().add(detailsBox);

		return flightBox;
	}

	private Double getAdultPriceByFlightNoAndSeatClass(String flightNo, String seatClass) {
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			String hql = "SELECT sc.adultPrice FROM SeatClass sc WHERE sc.flightNo.flightNo = :flightNo AND sc.seatClass = :seatClass";
			Query<Double> query = session.createQuery(hql, Double.class);
			query.setParameter("flightNo", flightNo);
			query.setParameter("seatClass", seatClass);

			return query.uniqueResult();
		} catch (HibernateException e) {
			e.printStackTrace();
			return null;
		}
	}
}
