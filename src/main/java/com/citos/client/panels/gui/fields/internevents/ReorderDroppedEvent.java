/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.fields.internevents;

/**
 * Created by johannesengler on 13.05.17.
 */
public class ReorderDroppedEvent {
    private int resolvedPosition;

    public ReorderDroppedEvent(int resolvedPosition) {
        this.resolvedPosition = resolvedPosition;
    }

    public int getResolvedPosition() {
        return resolvedPosition;
    }
}
