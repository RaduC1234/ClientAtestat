package me.raducapatina.client.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.web.WebView;
import me.raducapatina.client.MainClient;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController {

    @FXML
    public Button login_signin_button;

    @FXML
    public TextField login_username_field;

    @FXML
    public TextField login_password_field;

    @FXML
    public Label login_info_label;
}
