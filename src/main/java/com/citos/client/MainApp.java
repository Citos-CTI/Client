/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client;

import com.google.common.eventbus.EventBus;
import com.citos.client.panels.gui.fields.otherevents.CloseApplicationSafelyEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MainApp extends Application implements NativeKeyListener {

    private Stage stage;
    // Bereich für den Hook
    private boolean strg = false;
    private boolean shift = false;
    private boolean five = false;
    private boolean escape = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Scene.fxml"));
        Parent root = loader.load();
        FXMLController controller = loader.getController();
        EventBus eventBus = new EventBus();
        controller.startApp(eventBus);
        controller.setStage(stage);
        Scene scene = new Scene(root);
        Font.loadFont(MainApp.class.getResource("/styles/Roboto-Light.ttf").toExternalForm(),13);

        scene.getStylesheets().add("/styles/Styles.css");
        stage.setTitle("JavaFX and Maven");

        stage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        stage.setScene(scene);

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.show();
        stage.setX(primaryScreenBounds.getWidth() - scene.getWidth());
        stage.setY(primaryScreenBounds.getHeight() - scene.getHeight());

        stage.getIcons().add(new Image("/pics/easy_cti_logo_round.png"));
        
        this.stage = stage;

        if (true) {
            // Get the logger for "org.jnativehook" and set the level to warning.
            Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
            logger.setLevel(Level.OFF);
            // Don't forget to disable the parent handlers.
            logger.setUseParentHandlers(false);

            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(this);
        }
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Logger.getLogger(getClass().getName()).info("Window Closed");
                try {
                    GlobalScreen.unregisterNativeHook();
                } catch (NativeHookException ex) {
                    Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
                }
                eventBus.post(new CloseApplicationSafelyEvent());
                stage.close();
                Platform.exit();
                System.exit(0);
                //TODO: Find way to securely shutdown program
            }
        });
    }

    /**
     * @param e
     * @see
     * org.jnativehook.keyboard.NativeKeyListener#nativeKeyTyped(org.jnativehook.keyboard.NativeKeyEvent)
     */
    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nke) {
        switch (nke.getKeyCode()) {
            case 1: //Escape Key
                five = false;
                strg = false;
                shift = false;
                escape = true;
                break;
            case 2:
                five = true;
                break;
            case 42:
                strg = true;
                break;
            case 29:
                shift = true;
                break;
            default:
                break;
        }
        if (escape == true) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    stage.toBack();
                    stage.setIconified(true);
                }
            });
            escape = false;
        } else if (five && strg && shift == true) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    stage.toFront();
                    stage.requestFocus();
                    stage.setIconified(false);
                }
            });
            five = false;
            strg = false;
            shift = false;
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nke) {
        switch (nke.getKeyCode()) {
            case 1:
                escape = false;
                break;
            case 2:
                five = false;
                break;
            case 42:
                strg = false;
                break;
            case 29:
                shift = false;
                break;
            default:
                break;
        }
    }
}
