package me.raducapatina.client.gui;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.HashMap;

public class SceneController {

    private Stage main;
    private HashMap<String, Scene> screenMap = new HashMap<>();
    private String activeSceneName = null;

    public SceneController() {
    }

    public SceneController(Stage main) {
        this.main = main;
    }

    public Scene getScene(String name) {
        return screenMap.get(name);
    }

    public SceneController addScreen(String name, Scene pane){
        screenMap.put(name, pane);
        return this;
    }

    public void removeScreen(String name){
        screenMap.remove(name);
    }

    public void activate(String name){
        if(main.isShowing())
            main.hide();
        main.setScene(screenMap.get(name));
        main.show();
        activeSceneName = name;
    }

}
