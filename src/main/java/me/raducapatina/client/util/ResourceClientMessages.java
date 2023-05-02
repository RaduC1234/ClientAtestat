package me.raducapatina.client.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

public class ResourceClientMessages extends ResourceBundle {

    private static ResourceClientMessages INSTANCE = new ResourceClientMessages();
    private static ResourceBundle BUNDLE;

    public static ResourceClientMessages getInstance() {
        return INSTANCE;
    }

    private ResourceClientMessages() {
        File file = new File("");
        URL[] urls = new URL[0];
        try {
            urls = new URL[]{file.toURI().toURL()};
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        ClassLoader loader = new URLClassLoader(urls);
        BUNDLE = ResourceBundle.getBundle("ui_code", Locale.getDefault(), loader);
    }

    public static String getObjectAsString(String key) {
        try {
            return BUNDLE.getObject(key).toString();
        } catch (Exception e) {
            return BUNDLE.getObject("ERROR").toString();
        }
    }

    @Override
    protected Object handleGetObject(String key) {
        return BUNDLE.getObject(key);
    }

    @Override
    public Enumeration<String> getKeys() {
        return BUNDLE.getKeys();
    }
}
