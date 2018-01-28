/*
 * Copyright (c) 2017. Johannes Engler
 */
package com.citos.client.panels.gui.fields;
import com.google.common.eventbus.EventBus;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.citos.client.PhoneNumber;
import com.citos.client.panels.gui.fields.callrecordevents.RemoveCdrAndUpdateLocalEvent;
import com.citos.client.panels.gui.fields.serverconnectionhandlerevents.CallEvent;
import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 *
 * @author johannesengler
 */
public class HistoryField extends VBox {

    private final String who;
    private final String labelText;
    private final String when;
    private final String howLong;
    private final String searched;
    private final String countryCode;
    private final boolean internal;
    private final String prefix;
    private final boolean outgoing;
    private final EventBus eventBus;
    private String name;
    private final long timeStamp;

    public HistoryField(String who, String when, String howLong, boolean outgoing,
                        boolean internal, int countryCode, int prefix, long timeStamp, String searched,EventBus eventBus) {
        this.when = when;
        this.who = who;
        this.howLong = getTimeFormat(howLong);
        this.outgoing = outgoing;
        this.eventBus = eventBus;
        this.countryCode = countryCode + "";
        this.labelText = internal ? who + " (not found)" : getNumberInfos() + who + " (not found)";
        this.timeStamp = timeStamp;
        this.searched = searched;
        this.internal = internal;
        this.prefix = prefix + "";
        buildField();
    }
    public HistoryField(String name, String who, String when, String howLong, boolean outgoing,
                        boolean internal, int countryCode, int prefix, long timeStamp, String searched, EventBus eventBus) {
        this.when = when;
        this.name = name;
        this.who = who;
        this.howLong = getTimeFormat(howLong);
        this.outgoing = outgoing;
        this.eventBus = eventBus;
        this.countryCode = countryCode + "";
        this.labelText = internal ? who + " ("+name+")" : getNumberInfos() + who + " ("+name+")";
        this.timeStamp = timeStamp;
        this.searched = searched;
        this.internal = internal;
        this.prefix = prefix + "";
        buildField();
    }


    public void buildField() {
        this.setMaxWidth(Double.MAX_VALUE);
        this.setPadding(new Insets(12, 12, 12, 12));
        this.setSpacing(3);
        this.setFocusTraversable(true);
        this.getStyleClass().clear();
        this.getStyleClass().add("history-box");


        HBox inner = new HBox();
        inner.setSpacing(5);
        inner.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(inner, Priority.ALWAYS);

        HBox innerinner = new HBox();
        innerinner.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(innerinner, Priority.ALWAYS);

        this.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getClickCount() == 2) {
                this.eventBus.post(new CallEvent(constructNumber(),false));
            }
            HistoryField.this.requestFocus();
            event.consume();

        });

        Label a = new Label(labelText);
        a.getStyleClass().clear();
        a.getStyleClass().add("history-label-big");
        Label b = null;
        if (outgoing) {
            b = new Label("Outgoing");
            b.getStyleClass().clear();
            b.getStyleClass().add("history-out");
        } else {
            b = new Label("Incoming");
            b.getStyleClass().clear();
            b.getStyleClass().add("history-in");
        }
        b.setMinWidth(Region.USE_PREF_SIZE);
        inner.getChildren().add(a);
        inner.getChildren().add(innerinner);
        inner.getChildren().add(b);
        this.getChildren().add(inner);

        this.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                this.eventBus.post(new CallEvent(HistoryField.this.getWho(),false));
                event.consume(); // do nothing
            }
        });

        HBox innerLine2 = new HBox();
        innerLine2.setSpacing(5);
        innerLine2.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(innerLine2, Priority.ALWAYS);

        HBox innerinnerLine2 = new HBox();
        innerinnerLine2.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(innerinnerLine2, Priority.ALWAYS);

        Label aLine2 = new Label(when);
        aLine2.getStyleClass().clear();
        aLine2.getStyleClass().add("history-label");
        Label bLine2 = new Label(howLong);
        bLine2.getStyleClass().clear();
        bLine2.getStyleClass().add("history-label");
        innerLine2.getChildren().add(aLine2);
        this.getChildren().add(innerLine2);
        innerLine2.getChildren().add(innerinnerLine2);
        innerLine2.getChildren().add(bLine2);

        final ContextMenu contextMenu = new ContextMenu();
        MenuItem del = new MenuItem("Delete");
        MenuItem call = new MenuItem("Call");
        contextMenu.getItems().addAll(del, call);

        del.setOnAction(event -> {
            this.eventBus.post(new RemoveCdrAndUpdateLocalEvent(HistoryField.this));
            contextMenu.hide();
        });
        call.setOnAction(event -> {
            this.eventBus.post(new CallEvent(constructNumber(),false));
            contextMenu.hide();

        });

        this.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(HistoryField.this, event.getScreenX(), event.getScreenY());
            } else {
                contextMenu.hide();
            }
        });

    }

    public String getWho() {
        return who;
    }

    public String getWhen() {
        return when;
    }

    public String getHowLong() {
        return howLong;
    }

    public boolean isOutgoing() {
        return outgoing;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getSearched() {
        return searched;
    }

    public String getTimeFormat(String secString) {
        int secs = Integer.parseInt(secString);
        String g = "";
        if(secs / (60 * 60) > 0) {
            String s = "" +secs / (60 * 60);
            if (s.length()<2) {
                g = "0"+s+":";
            } else {
                g = s+":";
            }
        } else {
            g = "00:";
        }
        if(secs % (60 * 60) > 0) {
            String s = "" + ((secs % (60 * 60))/60);
            if (s.length()<2) {
                g = g + "0"+s+":";
            } else {
                g = g + s+":";
            }
        } else {
            g = g + "00:";
        }
        String s ="" + secs % 60;
        if (s.length()<2) {
            s = "0" + s;
        }
        g = g + s;
        return g;
    }

    private String getNumberInfos() {
        PhoneNumberUtil phoneutil = PhoneNumberUtil.getInstance();
        String country = phoneutil.getRegionCodeForCountryCode(Integer.parseInt(countryCode));
        return country+" 0";
    }

    private String constructNumber() {
        return prefix + "00" + countryCode + who;
    }

}

