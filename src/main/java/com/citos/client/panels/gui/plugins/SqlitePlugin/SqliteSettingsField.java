/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.plugins.SqlitePlugin;
/* To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.citos.client.panels.gui.plugins.PluginDataField;
import com.citos.client.panels.gui.plugins.PluginSettingsField;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

/**
 * @author johannesengler
 */
public class SqliteSettingsField extends PluginSettingsField {
    private final ArrayList<HBox> mysqlFields;
    private final String pluginName;
    private SqliteLoader loader;
    private boolean hasChanged = false;

    public SqliteSettingsField(SqliteLoader loader, String settingsFieldName, String pluginName) {
        super(pluginName);
        this.loader = loader;
        mysqlFields = new ArrayList<>();
        this.pluginName = pluginName;
    }

    @Override
    public void expand() {
        VBox v = new VBox();
        v.setSpacing(3);
        VBox.setMargin(v, new Insets(6, 0, 3, 0));
        TextField f = new TextField();
        f.setPromptText("path to file (eg. /home/database.db)");
        f.setText(loader.getStorageTemp().getServerAddress());
        f.textProperty().addListener((observable, oldValue, newValue) -> {
            loader.getStorageTemp().setServerAddress(newValue);
            hasChanged = true;
        });

        TextField f3 = new TextField();
        f3.setPromptText("table (eg. employees)");
        f3.setText(loader.getStorageTemp().getDatabase());
        f3.textProperty().addListener((observable, oldValue, newValue) -> {
            loader.getStorageTemp().setDatabase(newValue);
            hasChanged = true;
        });

        Separator s = new Separator();
        Label l = new Label("fields");
        VBox.setMargin(s, new Insets(5, 0, 0, 0));
        VBox.setMargin(l, new Insets(0, 0, 0, 5));
        int i = 0;
        v.getChildren().addAll(f, f3, s, l);
        VBox vLdapFields = new VBox();
        vLdapFields.setSpacing(3);
        for (PluginDataField g : loader.getStorageTemp().getMysqlFields()) {
            mysqlFields.add(makeAdditionalField(g.getFieldname(), g.getFieldvalue(), vLdapFields, "X"));
            ++i;
        }

        // Button plus = new Button("Hizufügen");
        //  plus.setOnAction(new EventHandler<javafx.event.ActionEvent>() {

        //     @Override
        //      public void handle(javafx.event.ActionEvent event) {
        //           ldapFields.add(makeAdditionalField("", "",vLdapFields));
        //    }
        //  });

        mysqlFields.add(makeAdditionalField("", "", vLdapFields, "+"));

        v.getChildren().addAll(vLdapFields);
        this.getChildren().add(v);
        super.expand();
    }


    @Override
    public void collapse() {
        this.getChildren().remove(this.getChildren().size() - 1);
        super.collapse();
    }

    @Override
    public boolean hasChanged() {
        boolean out = hasChanged;
        hasChanged = false;
        return out;
    }

    public HBox makeAdditionalField(String a, String b, VBox vLdapFields, String sign) {
        HBox box = new HBox();
        TextField t1 = new TextField(a);
        t1.setPromptText("field (table column name)");
        Label l = new Label(":");
        TextField t2 = new TextField(b);
        t2.setPromptText("name to show");
        Button but = new Button(sign);
        if ("+".equals(sign)) {
            but.setStyle("-fx-font-size:13.5;");
        }
        but.setOnAction(event -> {
            if (("X").equals(but.getText())) {
                HBox b1 = (HBox) but.getParent();
                TextField t11 = (TextField) b1.getChildren().get(0);
                TextField t21 = (TextField) b1.getChildren().get(2);
                loader.getStorageTemp().removeFromMysqlFields(t11.getText(), t21.getText());
                mysqlFields.remove(but.getParent());
                vLdapFields.getChildren().remove(but.getParent());
                hasChanged = true;
            } else {
                boolean r = loader.getStorageTemp().addToMysqlFields(t1.getText(), t2.getText());
                if (r) {
                    makeAdditionalField("", "", vLdapFields, "+");
                    but.setText("X");
                    but.setStyle("-fx-font-size:13");
                } else {
                    t1.setPromptText("already showed");
                    t2.setPromptText("already showed");
                    t1.setText("");
                    t2.setText("");
                }
            }
        });
        box.getChildren().addAll(t1, l, t2, but);
        vLdapFields.getChildren().add(box);
        hasChanged = true;
        return box;
    }


}
