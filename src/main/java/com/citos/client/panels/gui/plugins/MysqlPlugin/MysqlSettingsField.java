/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.plugins.MysqlPlugin;

import com.citos.client.panels.gui.plugins.PluginDataField;
import com.citos.client.panels.gui.plugins.PluginSettingsField;
import com.citos.client.panels.gui.plugins.TransparentImageButton;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author johannesengler
 */
public class MysqlSettingsField extends PluginSettingsField {
    private final ArrayList<HBox> mysqlBoxes;
    private MysqlLoader loader;
    private boolean hasChanged = false;

    public MysqlSettingsField(MysqlLoader loader, String pluginName) {
        super(pluginName);
        this.loader = loader;
        mysqlBoxes = new ArrayList<>();
    }

    @Override
    public void expand() {
        VBox v = new VBox();
        v.setSpacing(3);
        VBox.setMargin(v, new Insets(6, 0, 3, 0));
        TextField f = new TextField();
        f.setPromptText("IP Adresse (Beispiel: server)");
        f.setText(loader.getStorageTemp().getServerAddress());
        f.textProperty().addListener((observable, oldValue, newValue) -> {
            loader.getStorageTemp().setServerAddress(newValue);
            hasChanged = true;
        });
        final TextField f2 = new TextField();
        f2.setPromptText("Port (Beispiel: 3306)");
        f2.setText(Integer.toString(loader.getStorageTemp().getServerPort()));
        f2.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                loader.getStorageTemp().setServerPort(Integer.valueOf(newValue));
                hasChanged = true;
            } catch (Exception e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            }
        });
        TextField f3 = new TextField();
        f3.setPromptText("Datenbank (Beispiel: database)");
        f3.setText(loader.getStorageTemp().getDatabase());
        f3.textProperty().addListener((observable, oldValue, newValue) -> {
            loader.getStorageTemp().setDatabase(newValue);
            hasChanged = true;
        });

        Separator s = new Separator();
        Label l = new Label("Mysql Felder");
        l.setStyle("-fx-text-fill: #c3c3c3");
        VBox.setMargin(s, new Insets(5, 0, 0, 0));
        VBox.setMargin(l, new Insets(0, 0, 0, 5));
        int i = 0;
        v.getChildren().addAll(f, f2, f3, s, l);
        VBox vLdapFields = new VBox();
        vLdapFields.setSpacing(3);
        for (PluginDataField g : loader.getStorageTemp().getMysqlFields()) {
            mysqlBoxes.add(makeAdditionalField(g.getFieldname(), g.getFieldvalue(), vLdapFields,false,i));
            ++i;
        }

        mysqlBoxes.add(makeAdditionalField("", "", vLdapFields,true,i));

        v.getChildren().add(vLdapFields);
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

    public HBox makeAdditionalField(String a, String b, VBox vLdapFields, boolean add, int num) {
        HBox box = new HBox();
        box.setSpacing(5);
        TextField t1 = new TextField(a);
        t1.setPromptText("Feld");

        TextField t2 = new TextField(b);
        t2.setPromptText("Anzeigename");

        t1.textProperty().addListener((observable, oldValue, newValue) -> {
            loader.getStorageTemp().alterMysqlFields(oldValue,t2.getText(),newValue,t2.getText());
        });
        t2.textProperty().addListener((observable, oldValue, newValue) -> {
            loader.getStorageTemp().alterMysqlFields(t1.getText(),oldValue,t1.getText(),newValue);
        });

        TransparentImageButton but;
        if(add) {
             but = new TransparentImageButton("/pics/add.png");
        } else {
             but = new TransparentImageButton("/pics/remove.png");
        }

        if(!add) {
            but.setOnMouseClicked(event -> {
                HBox b1 = (HBox) but.getParent();
                TextField t11 = (TextField) b1.getChildren().get(0);
                TextField t21 = (TextField) b1.getChildren().get(1);
                int deleted = loader.getStorageTemp().removeFromMysqlFields(t11.getText(), t21.getText());
                if(deleted == loader.getStorageTemp().getTelephone()) {
                    loader.getStorageTemp().unsetTelephone();
                } else if(deleted == loader.getStorageTemp().getMobile()) {
                    loader.getStorageTemp().unsetMobile();
                }
                mysqlBoxes.remove(b1);
                vLdapFields.getChildren().remove(but.getParent());
                hasChanged = true;
            });
        }  else {
            but.setOnMouseClicked(event -> {
                boolean r = loader.getStorageTemp().addToMysqlFields(t1.getText(), t2.getText());
                if (r) {
                    mysqlBoxes.remove(but.getParent());
                    vLdapFields.getChildren().remove(but.getParent());
                    mysqlBoxes.add(this.makeAdditionalField(t1.getText(), t2.getText(), vLdapFields, false, num+1));
                    loader.getStorageTemp().addToMysqlFields(t1.getText(),t2.getText());
                    this.makeAdditionalField("", "", vLdapFields, true, num+2);
                    hasChanged = true;
                } else {
                    t1.setPromptText("already showed");
                    t2.setPromptText("already showed");
                    t1.setText("");
                    t2.setText("");
                }
            });
        }

        String initialResource = "/pics/right-arrow.png";
        if(num == loader.getStorageTemp().getMobile()) {
            initialResource = "/pics/smartphone-call.png";
        } else if(num == loader.getStorageTemp().getTelephone()) {
            initialResource = "/pics/telephone-of-old-design.png";
        }

        TransparentImageButton v = new TransparentImageButton(initialResource,add);
        if(!add) {
            v.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                TransparentImageButton view = (TransparentImageButton) event.getSource();
                if (loader.getStorageTemp().getMobile() < 0 && view.getPos()!=1 && view.getPos()!=2) {
                    v.setImage(new Image("/pics/smartphone-call.png"));
                    loader.getStorageTemp().setMobile(mysqlBoxes.indexOf(view.getParent()));
                    view.setPos(1);
                    if (mysqlBoxes.indexOf(view.getParent()) == loader.getStorageTemp().getTelephone()) {
                        loader.getStorageTemp().unsetTelephone();
                    }
                } else if (loader.getStorageTemp().getTelephone() < 0 && view.getPos()!=2) {
                    v.setImage(new Image("/pics/telephone-of-old-design.png"));
                    loader.getStorageTemp().setTelephone(mysqlBoxes.indexOf(view.getParent()));
                    view.setPos(2);
                    if (mysqlBoxes.indexOf(view.getParent()) == loader.getStorageTemp().getMobile()) {
                        loader.getStorageTemp().unsetMobile();
                    }
                } else {
                    v.setImage(new Image("/pics/right-arrow.png"));
                    if (mysqlBoxes.indexOf(view.getParent()) == loader.getStorageTemp().getTelephone()) {
                        loader.getStorageTemp().unsetTelephone();
                    }
                    if (mysqlBoxes.indexOf(view.getParent()) == loader.getStorageTemp().getMobile()) {
                        loader.getStorageTemp().unsetMobile();
                    }
                    view.setPos(0);
                }
                hasChanged = true;
                event.consume();
            });
        }

        box.setAlignment(Pos.CENTER);

        box.getChildren().addAll(t1, t2, but, v);
        vLdapFields.getChildren().add(box);
        hasChanged = true;
        return box;
    }


}
