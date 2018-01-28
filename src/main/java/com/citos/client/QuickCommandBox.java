/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.citos.client.panels.gui.fields.serverconnectionhandlerevents.ConnectionToServerLostEvent;
import com.citos.client.panels.gui.fields.serverconnectionhandlerevents.UserLoginStatusEvent;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * Created by johannesengler on 30.04.17.
 */
public class QuickCommandBox {
    private EventBus bus;
    private ToggleButton dndToggleButton;
    private ToggleButton redirectToggleButton;
    private Text serverStatusText;

    public QuickCommandBox(EventBus bus, ToggleButton dndToggleButton, ToggleButton redirectToggleButton, Text serverStatusText) {
        this.bus = bus;
        this.dndToggleButton = dndToggleButton;
        this.redirectToggleButton = redirectToggleButton;
        this.serverStatusText = serverStatusText;
        this.bus.register(this);
        Color c = Color.valueOf("#43DBFF");
        serverStatusText.setFill(c);
    }

    @Subscribe
    public void serverConnected(UserLoginStatusEvent event) {
        if (event.isLoggedIn()) {
            Color c = Color.valueOf("#60EB7E");
            serverStatusText.setFill(c);
        }

    }

    @Subscribe
    public void serverConnectionLost(ConnectionToServerLostEvent event) {
        Color c = Color.valueOf("#FC74A7");
        serverStatusText.setFill(c);
    }

}
