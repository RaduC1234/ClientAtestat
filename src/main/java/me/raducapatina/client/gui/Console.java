package me.raducapatina.client.gui;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/*
    Logger wrapper
 */
public class Console {

    private static final Logger logger = LogManager.getLogger(Console.class);

    public void debug(String message) {
        logger.debug(message);
    }

    public void error(String message) {
        logger.error(message);
    }

    public void fatal(String message) {
        logger.fatal(message);
    }

    public void info(String message) {
        logger.info(message);
    }
}
