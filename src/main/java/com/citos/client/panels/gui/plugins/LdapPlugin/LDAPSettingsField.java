/*
 * Copyright (c) 2017. Johannes Engler
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.citos.client.panels.gui.plugins.LdapPlugin;

import com.citos.client.panels.gui.plugins.DataSource;
import com.citos.client.panels.gui.plugins.PluginDataField;
import com.citos.client.panels.gui.plugins.PluginSettingsField;
import com.citos.client.panels.gui.plugins.TransparentImageButton;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
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
 *
 * @author johannesengler
 */

public class LDAPSettingsField extends PluginSettingsField {
    private ArrayList<HBox> ldapFields;
    private LdapLoader loader;
    private boolean hasChanged = false;

    public LDAPSettingsField(LdapLoader loader, String pluginName) {
        super(pluginName);
        ldapFields = new ArrayList<>();
        this.loader = loader;
    }

    @Override
    public void expand() {
        VBox v = new VBox();
        v.setSpacing(3);
        VBox.setMargin(v, new Insets(6, 0, 3, 0));
        TextField f = new TextField();
        f.setPromptText("IP Adresse (Beispiel: server)");
        f.setText(loader.getStorageTemp().getLdapAddress());
        f.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                loader.getStorageTemp().setLdapAddress(newValue);
                hasChanged = true;
            }
        });
        final TextField f2 = new TextField();
        f2.setPromptText("Port (Beispiel: 389)");
        f2.setText(Integer.toString(loader.getStorageTemp().getLdapServerPort()));
        f2.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try{
                    loader.getStorageTemp().setLdapServerPort(Integer.valueOf(newValue));
                    hasChanged = true;
                } catch (Exception e) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null,e);
                }
            }
        });
        TextField f3 = new TextField();
        f3.setPromptText("Suchbasis (Beispiel: dc=server, dc=com)");
        f3.setText(loader.getStorageTemp().getLdapSearchBase());
        f3.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                loader.getStorageTemp().setLdapSearchBase(newValue);
                hasChanged = true;
            }
        });
        TextField f5 = new TextField();
        f5.setText(loader.getStorageTemp().getLdapType());
        f5.setPromptText("Typ (Beispiel: objectClass=inetOrgPerson");
        f5.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                loader.getStorageTemp().setLdapBase(newValue);
                hasChanged = true;
            }
        });

      Separator s = new Separator();
      Label l = new Label("LDAP Felder");
      l.getStyleClass().add("setting-label");

        VBox.setMargin(s, new Insets(5,0, 0, 0));
      VBox.setMargin(l, new Insets(0, 0, 0, 5));
      int i = 0;
      v.getChildren().addAll(f,f2,f3,f5,s,l);
        VBox vLdapFields = new VBox();
        vLdapFields.setSpacing(3);
      for(PluginDataField g: loader.getStorageTemp().getLdapFields()) {
          ldapFields.add(makeAdditionalField(g.getFieldname(), g.getFieldvalue(), vLdapFields,false,i));
          ++i;
      }

        ldapFields.add(makeAdditionalField("", "", vLdapFields,true,i));

      v.getChildren().addAll(vLdapFields);

        this.getChildren().add(v);


        super.expand();

    }

    @Override
    public void collapse() {
        this.getChildren().remove(this.getChildren().size()-1);
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
            loader.getStorageTemp().alterLdapFields(oldValue,t2.getText(),newValue,t2.getText());
        });
        t2.textProperty().addListener((observable, oldValue, newValue) -> {
            loader.getStorageTemp().alterLdapFields(t1.getText(),oldValue,t1.getText(),newValue);
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
                int deleted = loader.getStorageTemp().removeFromLdapFields(t11.getText(), t21.getText());
                if(deleted == loader.getStorageTemp().getTelephone()) {
                    loader.getStorageTemp().unsetTelephone();
                } else if(deleted == loader.getStorageTemp().getMobile()) {
                    loader.getStorageTemp().unsetMobile();
                }
                ldapFields.remove(b1);
                vLdapFields.getChildren().remove(but.getParent());
                hasChanged = true;
            });
        }  else {
            but.setOnMouseClicked(event -> {
                boolean r = loader.getStorageTemp().addToLdapFields(t1.getText(), t2.getText());
                if (r) {
                    ldapFields.remove(but.getParent());
                    vLdapFields.getChildren().remove(but.getParent());
                    ldapFields.add(this.makeAdditionalField(t1.getText(), t2.getText(), vLdapFields, false, num+1));
                    loader.getStorageTemp().addToLdapFields(t1.getText(),t2.getText());
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
                    loader.getStorageTemp().setMobile(ldapFields.indexOf(view.getParent()));
                    view.setPos(1);
                    if (ldapFields.indexOf(view.getParent()) == loader.getStorageTemp().getTelephone()) {
                        loader.getStorageTemp().unsetTelephone();
                    }
                } else if (loader.getStorageTemp().getTelephone() < 0 && view.getPos()!=2) {
                    v.setImage(new Image("/pics/telephone-of-old-design.png"));
                    loader.getStorageTemp().setTelephone(ldapFields.indexOf(view.getParent()));
                    view.setPos(2);
                    if (ldapFields.indexOf(view.getParent()) == loader.getStorageTemp().getMobile()) {
                        loader.getStorageTemp().unsetMobile();
                    }
                } else {
                    v.setImage(new Image("/pics/right-arrow.png"));
                    if (ldapFields.indexOf(view.getParent()) == loader.getStorageTemp().getTelephone()) {
                        loader.getStorageTemp().unsetTelephone();
                    }
                    if (ldapFields.indexOf(view.getParent()) == loader.getStorageTemp().getMobile()) {
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

