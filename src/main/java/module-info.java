module me.raducapatina.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires io.netty.transport;
    requires io.netty.codec;
    requires com.fasterxml.jackson.databind;


    opens me.raducapatina.client to javafx.fxml;
    exports me.raducapatina.client;
    exports me.raducapatina.client.gui;
    opens me.raducapatina.client.gui to javafx.fxml;
    exports me.raducapatina.client.core;
    opens me.raducapatina.client.core to javafx.fxml;
}