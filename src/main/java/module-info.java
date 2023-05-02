module me.raducapatina.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires io.netty.transport;
    requires io.netty.codec;
    requires com.fasterxml.jackson.databind;
    requires jdk.jsobject;
    requires org.apache.logging.log4j;


    opens me.raducapatina.client to javafx.fxml;
    exports me.raducapatina.client;
    exports me.raducapatina.client.gui;
    opens me.raducapatina.client.gui to javafx.fxml;
    exports me.raducapatina.client.core;
    opens me.raducapatina.client.core to javafx.fxml;

    opens me.raducapatina.client.data to com.fasterxml.jackson.databind;
    exports me.raducapatina.client.data;
    exports me.raducapatina.client.network;
    opens me.raducapatina.client.network to javafx.fxml;
    exports me.raducapatina.client.util;
    opens me.raducapatina.client.util to javafx.fxml;

}