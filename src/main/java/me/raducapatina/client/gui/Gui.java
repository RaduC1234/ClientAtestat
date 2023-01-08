package me.raducapatina.client.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.*;
import me.raducapatina.client.MainClient;
import me.raducapatina.client.core.ClientInstance;
import org.w3c.dom.Document;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class Gui extends Application  {

    public final int SCREEN_WIDTH = 1600;
    public final int SCREEN_HEIGHT = 800;

    private static Gui instance = null;

    private LoginController loginController = new LoginController();
    private SceneController sceneController = new SceneController();

    private WebModule mainModule = new WebModule("/html/main.html");

    public Gui() {
        instance = this;
    }

    public static synchronized Gui getInstance()
    {
        if(instance == null) {
            new Thread(() -> Application.launch(Gui.class)).start();
        }

        while (instance == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    @Override
    public void start(Stage stage) throws IOException {
        init(stage);

        FXMLLoader splashScreen = new FXMLLoader(MainClient.class.getResource("/sources/splashScreen.fxml"));
        FXMLLoader loginScreen = new FXMLLoader(MainClient.class.getResource("/sources/loginScreen.fxml")); loginScreen.setController(loginController);

        sceneController = new SceneController(stage);
        sceneController.addScreen("splashScreen", new Scene(splashScreen.load(), SCREEN_WIDTH, SCREEN_HEIGHT));
        sceneController.addScreen("loginScreen", new Scene(loginScreen.load(), SCREEN_WIDTH, SCREEN_HEIGHT));
        sceneController.addScreen("dashboardScreen", new Scene(mainModule, SCREEN_WIDTH, SCREEN_HEIGHT, Color.web("#666970")));
        sceneController.addScreen("loadingScreen", new Scene(new WebModule("/html/loading.html"), SCREEN_WIDTH, SCREEN_HEIGHT, Color.web("#666970")));

        sceneController.activate("splashScreen");
    }

    private void init(Stage stage) {
        stage.setTitle("Education Software Client. Pre-Alpha 0.4");
        stage.getIcons().add(new Image(Objects.requireNonNull(MainClient.class.getResourceAsStream("/assets/tray_logo.png"))));
        stage.setOnCloseRequest(event -> ClientInstance.getInstance().stopApplication());
        stage.setWidth(SCREEN_WIDTH);
        stage.setHeight(SCREEN_HEIGHT);
        stage.centerOnScreen();
        //stage.initStyle(StageStyle.UNDECORATED);
    }

    public void setScene(String name) {
        Platform.runLater(() -> {
            sceneController.activate(name);
        });
    }

    public synchronized LoginController getLoginController() {
        return this.loginController;
    }

    public synchronized WebModule getMainModule() {
        return mainModule;
    }

    public static class WebModule extends Pane {

        private final WebView browser = new WebView();
        private final WebEngine webEngine = browser.getEngine();

        public WebModule(String source) {
            //apply the styles
            getStyleClass().add("browser");
            // load the web page
            URL url = MainClient.class.getResource(source);
            webEngine.load(url.toString());
            //add the web view to the scene
            getChildren().add(browser);

        }

        @Override
        protected void layoutChildren() {
            double w = getWidth();
            double h = getHeight();
            layoutInArea(browser, 0, 0, w, h, 0, HPos.CENTER, VPos.CENTER);
        }

        @Override
        protected double computePrefWidth(double height) {
            return 750;
        }

        @Override
        protected double computePrefHeight(double width) {
            return 500;
        }

        public WebView getBrowser() {
            return browser;
        }

        public WebEngine getWebEngine() {
            return webEngine;
        }
    }
}
