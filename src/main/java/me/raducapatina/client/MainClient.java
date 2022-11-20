package me.raducapatina.client;

import me.raducapatina.client.core.ClientInstance;

/**
 * DO NOT MOVE LOCATION OF THIS CLASS: @me.raducapatina.client
 */
public class MainClient {

    public static void main(String[] args) {
        ClientInstance clientInstance = ClientInstance.getInstance();
        clientInstance.start();
    }
}
