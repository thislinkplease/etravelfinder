package src.Model;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

public class SignOut {
    @FXML
    public void handleSignoutButtonClick(ActionEvent event) {
        UserSession.getInstance().cleanUserSession();
        Node source = (Node) event.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        logout(currentStage);
    }
    private void logout(Stage currentStage) {
        NavigationHelper.showNewStage("/FXML_files/Login.fxml");
        currentStage.close();
    }
}

