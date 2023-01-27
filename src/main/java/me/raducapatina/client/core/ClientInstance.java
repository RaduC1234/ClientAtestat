package me.raducapatina.client.core;

import javafx.application.Platform;
import me.raducapatina.client.data.User;
import me.raducapatina.client.gui.Gui;
import me.raducapatina.client.network.ClientNetworkService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class ClientInstance {

    private static ClientInstance instance = null;

    private Gui gui = null;

    private AtomicReference<User> selfUser = new AtomicReference<>();
    private ExecutorService executorService = Executors.newFixedThreadPool(3);

    private ClientNetworkService service = new ClientNetworkService();

    private ClientInstance() {

    }

    public static synchronized ClientInstance getInstance() {
        if (instance == null)
            instance = new ClientInstance();
        return instance;
    }

    public void start() {
        this.gui = Gui.getInstance();
        this.service.start();
    }

    public void stopApplication() {
        Platform.exit();
        System.exit(0); // just to be sure
    }

    public User getUser() {
        return selfUser.get();
    }

    public ClientInstance setSelfUser(User selfUser) {
        this.selfUser.set(selfUser);
        return this;
    }

    public ClientNetworkService getNetworkService() {
        return this.service;
    }

    public void runLater(Runnable action) {
        this.executorService.execute(action);
    }
}