package me.raducapatina.client.gui;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.application.Application;
import javafx.application.Platform;
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
import javafx.stage.Screen;
import javafx.stage.Stage;
import me.raducapatina.client.MainClient;
import me.raducapatina.client.core.ClientInstance;
import me.raducapatina.client.data.Article;
import me.raducapatina.client.data.User;
import me.raducapatina.client.data.UserType;
import netscape.javascript.JSObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import java.awt.Desktop;
import java.net.URI;

public class Gui extends Application {

    private static final Logger logger = LogManager.getLogger(Gui.class);

    public final int BASE_SCREEN_WIDTH = 1600;
    public final int BASE_SCREEN_HEIGHT = 800;

    private static Gui instance = null;

    private LoginController loginController = new LoginController();
    private SceneController sceneController = new SceneController();

    private WebModule mainModule = new WebModule("/html/main-page.html");

    private List<Article> mainPageArticles = new ArrayList<>();

    // Do not use this constructor and do not declare it private
    public Gui() {
        instance = this;
    }

    public static synchronized Gui getInstance() {
        if (instance == null) {
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
        FXMLLoader loginScreen = new FXMLLoader(MainClient.class.getResource("/sources/loginScreen.fxml"));
        loginScreen.setController(loginController);

        sceneController = new SceneController(stage);
        sceneController.addScreen("splashScreen", new Scene(splashScreen.load(), BASE_SCREEN_WIDTH, BASE_SCREEN_HEIGHT));
        sceneController.addScreen("loginScreen", new Scene(loginScreen.load(), BASE_SCREEN_WIDTH, BASE_SCREEN_HEIGHT));
        sceneController.addScreen("dashboardScreen", new Scene(mainModule, BASE_SCREEN_WIDTH, BASE_SCREEN_HEIGHT, Color.web("#666970")));
        sceneController.addScreen("loadingScreen", new Scene(new WebModule("/html/loading.html"), BASE_SCREEN_WIDTH, BASE_SCREEN_HEIGHT, Color.web("#666970")));

        sceneController.activate("splashScreen");
    }

    // called when network service has done getting initial data from server
    public void loadGuiBridge() {
        Platform.runLater(() -> {

            WebEngine webEngine = mainModule.getWebEngine();

            webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
                if (Worker.State.SUCCEEDED == newValue) {

                    JSObject window = (JSObject) webEngine.executeScript("window");
                    window.setMember("Gui", this);
                    window.setMember("Console", new Console());

                    webEngine.executeScript(
                            "const event = new Event('engineReady');" +
                                    "document.dispatchEvent(event);"
                    );

                    loadPage();
                }
            });
            logger.info("JavaFx bridge fully loaded.");
        });
    }

    public void loadPage() {

        // Sidebar
        UserType userType = ClientInstance.getInstance().getUser().getType();

        switch (userType) {

            case STUDENT -> {
                //this.mainModule.webEngine.executeScript("document.getElementById(\"_ADMIN_USERS_\").style.display = \"none\"");
                //this.mainModule.webEngine.executeScript("document.getElementById(\"_TEACHER_\").style.display = \"none\"");
            }
            case TEACHER -> {
                //this.mainModule.webEngine.executeScript("document.getElementById(\"_ADMIN_USERS_\").style.display = \"none\"");
                //this.mainModule.webEngine.executeScript("document.getElementById(\"_STUDENT_\").style.display = \"none\"");
            }
            case ADMIN -> {
                //this.mainModule.webEngine.executeScript("document.getElementById(\"_TEACHER_\").style.display = \"none\"");
                //this.mainModule.webEngine.executeScript("document.getElementById(\"_STUDENT_\").style.display = \"none\"");
            }
            case DEBUG, UNKNOWN -> {
            }
        }

        // Other pages
        mainModule.webEngine.executeScript("loadPage();");
    }

    // ADMIN_READ_USERS
    public void requestAdminGetUsers() {
        ClientInstance.getInstance().getNetworkService().sendRequest("ADMIN_GET_USERS", null);

        logger.debug("Calling 'ADMIN_GET_USERS'");
    }

    public void callbackAdminGetUsers(JsonNode usersJson) {

        for (JsonNode node : usersJson) {
            ((ObjectNode) node).remove("subjects");
            ((ObjectNode) node).remove("grades");
        }

        runOnGui(() -> {
            mainModule.getWebEngine().executeScript("refreshTable(\"admin-users-table\", " + usersJson.toPrettyString() + ")");
        });
    }

    // ADMIN_DELETE_USERS
    public void requestAdminDeleteUsers(int id) {
        ClientInstance.getInstance().getNetworkService().sendRequest("ADMIN_DELETE_USERS", new Object[]{id});

        logger.debug("Calling 'ADMIN_DELETE_USERS'");
    }

    public void callbackAdminDeleteUsers() {
        requestAdminGetUsers();
    }

    // ADMIN_ADD_USERS
    public void requestAdminAddUsers(String username, String password, String firstName, String lastName, String type) {
        ClientInstance.getInstance().getNetworkService().sendRequest("ADMIN_ADD_USERS", new Object[]{username, password, firstName, lastName, type});
    }

    public void callbackAdminAddUsers() {
        runOnGui(() -> {
            mainModule.getWebEngine().executeScript("clearAndCloseModal()");
        });
        requestAdminGetUsers();
    }

    // ADMIN_ADD_SUBJECTS
    public void requestAdminAddSubjects(String name, int teacherId) {
        ClientInstance.getInstance().getNetworkService().sendRequest("ADMIN_ADD_SUBJECTS", new Object[]{name, teacherId});
    }

    public void callbackAdminAddSubjects() {
        requestAdminGetSubjects();
    }

    // ADMIN_GET_SUBJECTS
    public void requestAdminGetSubjects() {
        ClientInstance.getInstance().getNetworkService().sendRequest("ADMIN_GET_SUBJECTS", null);
    }

    public void callbackAdminGetSubjects(JsonNode subjectsJson) {

        runOnGui(() -> {
            mainModule.getWebEngine().executeScript("refreshCollapse(\"admin-subjects\", " + subjectsJson.toPrettyString() + ")");
        });
    }

    // ADMIN_DELETE_SUBJECTS
    public void requestAdminDeleteSubjects(long id) {
        ClientInstance.getInstance().getNetworkService().sendRequest("ADMIN_DELETE_SUBJECTS", new Object[]{id});
    }

    public void callbackAdminDeleteSubjects() {
        requestAdminGetSubjects();
    }


    // ADMIN_GET_STUDENTS
    public void requestAdminGetStudents(long id) {
        ClientInstance.getInstance().getNetworkService().sendRequest("ADMIN_GET_STUDENTS", new Object[]{id});
    }

    public void callbackAdminGetStudents(JsonNode node) {

        runOnGui(() -> {
            mainModule.getWebEngine().executeScript("refreshAddModal(\"table0\"," + node.toPrettyString() + ")");

        });
    }

    // ADMIN_GET_TEACHERS
    public void requestAdminGetTeachers() {
        ClientInstance.getInstance().getNetworkService().sendRequest("ADMIN_GET_TEACHERS", null);
    }

    public void callbackAdminGetTeachers(JsonNode teachersNode) {

        runOnGui(() -> {
            mainModule.getWebEngine().executeScript("dropDown(" + teachersNode.toPrettyString() + ");");

        });
    }

    // ADMIN_ADD_STUDENT_TO_SUBJECT
    public void requestAdminAddStudentToSubject(long studentID, long subjectID) {
        logger.info(studentID + " " + subjectID);
    }

    public void requestStudentLoadGrades() {

        User user = ClientInstance.getInstance().getUser();

        try {
            String json = new JsonMapper().writeValueAsString(user);
            System.out.println(json);
            runOnGui(() -> {
                mainModule.getWebEngine().executeScript("refreshStudentTable(" + json + ")");
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void requestOpenExternalLink(String string) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI(string));
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void init(Stage stage) {
        stage.setTitle("Education Software Client. Alpha 3.0");
        stage.getIcons().add(new Image(Objects.requireNonNull(MainClient.class.getResourceAsStream("/assets/tray_logo.png"))));
        stage.setOnCloseRequest(event -> ClientInstance.getInstance().stopApplication());

        // Get the primary screen dimensions
        double screenWidth = Screen.getPrimary().getBounds().getWidth();
        double screenHeight = Screen.getPrimary().getBounds().getHeight();

        stage.setWidth(screenWidth * 4 / 5);
        stage.setHeight(screenHeight * 3 / 4);
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

    public List<Article> getMainPageArticles() {
        return this.mainPageArticles;
    }

    public void runOnGui(Runnable runnable) {
        Platform.runLater(runnable);
    }

    public static class WebModule extends Pane {

        private final WebView browser = new WebView();
        private final WebEngine webEngine = browser.getEngine();

        public WebModule(String source) {

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
