package me.raducapatina.client.gui;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebView;
import me.raducapatina.client.MainClient;
import netscape.javascript.JSObject;

import java.net.URL;
import java.util.ResourceBundle;

public class SplashController implements Initializable {

    @FXML
    WebView loading_WebView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        URL resource = MainClient.class.getResource("/html/loading.html");
        loading_WebView.getEngine().load(resource.toString());
        loading_WebView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (Worker.State.SUCCEEDED == newValue) {
                System.out.println("works");
                loading_WebView.getEngine().executeScript("document.body.style.background = \"#fff\"");
            }
        });
    }
}
