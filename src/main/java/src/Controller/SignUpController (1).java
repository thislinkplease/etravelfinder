package src.Controller;

import Entity.UserInfor;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.mindrot.jbcrypt.BCrypt;
import src.Model.HibernateUtil;
import src.Model.NavigationHelper;

import java.io.IOException;

public class SignUpController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;
    
    @FXML
    private PasswordField confirmPasswordField;


    public void showSignInView() throws IOException {
        NavigationHelper.switchToScene(usernameField, "/FXML_files/Login.fxml");
    }

    @FXML
    private void signupBtn(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Sign Up Error");
            alert.setContentText("Please fill in all text field! (Username, Password and Confirm Password)");

            alert.showAndWait();
        } else if (!password.equals(confirmPassword)) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Sign Up Error");
            alert.setContentText("Password and Confirm Password do not match!");

            alert.showAndWait();
        } else {

            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                session.beginTransaction();

                UserInfor userInfor = new UserInfor();
                userInfor.setUsername(username);
                userInfor.setPassword(hashedPassword);
                userInfor.setUsertype("user");

                session.save(userInfor);

                session.getTransaction().commit();

                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("User registered successfully!");
                alert.showAndWait();

                showSignInView();
            } catch (HibernateException | IOException e) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setHeaderText("Sign Up Error");
                alert.setContentText("An error occurred while registering user!");

                alert.showAndWait();

                throw new RuntimeException(e);
            }
        }
    }
    
    @FXML
    private void helpBtn(MouseEvent event) {

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Need help?");
        alert.setHeaderText(null);
        alert.setContentText("Please contact admin to solve your problem!");

        alert.showAndWait();
    }
}
