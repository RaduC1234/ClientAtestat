package me.raducapatina.client.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import me.raducapatina.client.MainClient;
import me.raducapatina.client.core.ClientInstance;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class Gui extends Application  {

    public final int SCREEN_WIDTH = 1600;
    public final int SCREEN_HEIGHT = 800;

    private static Gui instance = null;

    private LoginController loginController = new LoginController();

    private SceneController sceneController = new SceneController();

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

        FXMLLoader splashScreen = new FXMLLoader(MainClient.class.getResource("/sources/SplashScreen.fxml"));
        FXMLLoader loginScreen = new FXMLLoader(MainClient.class.getResource("/sources/LoginScreen.fxml")); loginScreen.setController(loginController);

        sceneController = new SceneController(stage);
        sceneController.addScreen("splashScreen", new Scene(splashScreen.load(), SCREEN_WIDTH, SCREEN_HEIGHT));
        sceneController.addScreen("loginScreen", new Scene(loginScreen.load(), SCREEN_WIDTH, SCREEN_HEIGHT));
        sceneController.addScreen("dashboardScreen", new Scene(new FullScreenWebView("/html/main.html"), SCREEN_WIDTH, SCREEN_HEIGHT));
        sceneController.addScreen("loadingScreen", new Scene(new FullScreenWebView("/html/loading.html"), SCREEN_WIDTH, SCREEN_HEIGHT, Color.web("#666970")));

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


    public static class FullScreenWebView extends Pane {

        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();

        public FullScreenWebView(String source) {
            //apply the styles
            getStyleClass().add("browser");
            // load the web page
            URL url = MainClient.class.getResource(source);
            webEngine.load(url.toString());
            //add the web view to the scene
            getChildren().add(browser);

        }

        private Node createSpacer() {
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            return spacer;
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
    }

    public static class Browser extends Region {}
}
