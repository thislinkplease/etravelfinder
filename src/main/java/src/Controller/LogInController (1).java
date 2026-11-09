package src.Controller;

import Entity.UserInfor;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;
import javafx.scene.Node;
import org.mindrot.jbcrypt.BCrypt;
import src.Model.LoginUtil;
import src.Model.NavigationHelper;
import src.Model.UserSession;
import src.Model.HibernateUtil;

import java.io.IOException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class LogInController {
	@FXML
	private TextField usernameField;

	@FXML
	private PasswordField passwordField;

	@FXML
	private void loginBtn(MouseEvent event) throws IOException {
		String username = usernameField.getText();
		String password = passwordField.getText();

		try (Session session = HibernateUtil.getSessionFactory().openSession()) {
			session.beginTransaction();

			String hql = "FROM UserInfor WHERE username = :username";
			Query<UserInfor> query = session.createQuery(hql);
			query.setParameter("username", username);
			UserInfor userInfor = query.uniqueResult();

			session.getTransaction().commit();

			if (userInfor != null && BCrypt.checkpw(password, userInfor.getPassword())) {

				// Lưu thông tin đăng nhập vào tệp
				LoginUtil.saveLoginInfo(username);

				UserSession userSession = UserSession.getInstance(username);
				userSession.setUserType(userInfor.getUsertype());

				String userType = userSession.getUserType();
				FXMLLoader loader;
				if ("user".equals(userType)) {
					loader = new FXMLLoader(getClass().getResource("/FXML_files/User/UserInterface-hotels.fxml"));
				} else if ("attractionprovider".equals(userType)) {
					loader = new FXMLLoader(getClass().getResource("/FXML_files/Provider/Attractions-upload.fxml"));
				} else if ("hotelprovider".equals(userType)) {
					loader = new FXMLLoader(getClass().getResource("/FXML_files/Provider/Hotel-upload.fxml"));
				} else if ("vehicleprovider".equals(userType)) {
					loader = new FXMLLoader(getClass().getResource("/FXML_files/Provider/Vehicles-upload.fxml"));
				} else if ("flightprovider".equals(userType)) {
					loader = new FXMLLoader(getClass().getResource("/FXML_files/Provider/Flight-upload.fxml"));
				} else {
					loader = new FXMLLoader(getClass().getResource("/FXML_files/User/UserInterface-hotels.fxml"));
				}

                Parent root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.centerOnScreen();
                stage.show();
            } else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Login Error");
				alert.setHeaderText(null);
				alert.setContentText("Incorrect password or username does not exist.");
				alert.showAndWait();
			}
		} catch (HibernateException e) {
			showAlert("Database Error", "An error occurred while accessing the database.", e.getMessage());
		} catch (IOException e) {
			showAlert("File Error", "An error occurred while loading the FXML file.", e.getMessage());
		}
	}

	@FXML
	private void hereBtn(MouseEvent event) throws IOException {
		NavigationHelper.switchToScene((Node) event.getSource(), "/FXML_files/Signup.fxml");
	}

	@FXML
	private void helpBtn(MouseEvent event) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Forgot Password");
		alert.setHeaderText(null);
		alert.setContentText("Please contact admin to retrieve your password!");

		alert.showAndWait();
	}

	private void showAlert(String title, String header, String content) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}
}
