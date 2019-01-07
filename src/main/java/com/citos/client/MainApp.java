/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client;

import com.citos.client.panels.gui.fields.otherevents.CloseApplicationSafelyEvent;
import com.citos.client.panels.gui.fields.otherevents.EndWindowPositioningEvent;
import com.citos.client.panels.gui.fields.otherevents.StartWindowPositioningEvent;
import com.citos.client.panels.gui.fields.otherevents.WindowPositionLoadedEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
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
    private Scene scene;
    // Bereich für den Hook
    private boolean strg = false;
    private boolean shift = false;
    private boolean five = false;
    private boolean escape = false;
    private double xOffset = 0;
    private double yOffset = 0;
    private double x;
    private double y;
    private EventBus eventBus;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Scene.fxml"));
        Parent root = loader.load();
        FXMLController controller = loader.getController();
        eventBus = new EventBus();
        eventBus.register(this);
        controller.startApp(eventBus);
        controller.setStage(stage);
        Scene scene = new Scene(root);
        Font.loadFont(MainApp.class.getResource("/styles/Roboto-Light.ttf").toExternalForm(),13);

        scene.getStylesheets().add("/styles/Styles.css");
        stage.setTitle("Citel Manager");

        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });
        stage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
        stage.getIcons().add(new Image("/pics/easy_cti_logo_round.png"));

        this.stage = stage;
        this.scene = scene;
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
        // RACE CONDITION
        setStagePosition();
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

    @Subscribe
    public void startWindowPositioning(StartWindowPositioningEvent ev) {
        StackPane root = new StackPane();
        root.setBackground(Background.EMPTY);
        String style = "-fx-background-color: rgba(0, 0, 0, 0.7);";
        root.setStyle(style);

        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                x = event.getScreenX() - xOffset;
                y = event.getScreenY() - yOffset;
                setStagePosition();
            }
        });
        root.setOnMouseReleased(event ->
        {
            eventBus.post(new EndWindowPositioningEvent(x, y));
            stage.setScene(scene);
            stage.show();
        });

        Text waitingForKey = new Text("Info:\n\n" + "Drag and Drop me");
        waitingForKey.setTextAlignment(TextAlignment.CENTER);
        waitingForKey.setFont(new Font(18));
        waitingForKey.setStyle("-fx-fill: lightgray;");
        root.getChildren().add(waitingForKey);
        Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        stage.setScene(scene);
        stage.show();
    }

    @Subscribe
    public void loadedWindowPosition(WindowPositionLoadedEvent event) {
        this.x = event.getX();
        this.y = event.getY();

        if (stage != null)
            setStagePosition();
    }

    private void setStagePosition() {
        // Check if the saved position is out of the window area - if go to border
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        if (primaryScreenBounds.getWidth() - scene.getWidth() < x) {
            this.x = primaryScreenBounds.getWidth() - scene.getWidth() - 1;
        }
        if (primaryScreenBounds.getHeight() - scene.getHeight() < y) {
            String os = System.getProperty("os.name");
            int offset = os.startsWith("Windows") ? 50 : 0;
            this.y = primaryScreenBounds.getHeight() - scene.getHeight() + offset;
        }

        stage.setX(this.x);
        stage.setY(this.y);
    }
}
