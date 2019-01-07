/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui.settings;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author johannes
 */
public class DataSourceSettingsField extends SettingsField {

    private ArrayList<CheckBox> checkBoxes;
    private boolean changed;
    private String pluginFolderTextField;
    private HBox h;
    private Text t;
    public DataSourceSettingsField() {
        super("Datasource");
        checkBoxes = new ArrayList<>();
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        Button pluginFolderButton = new Button("Choose folder");
        h = new HBox();
        h.setFillHeight(true);
        h.setAlignment(Pos.CENTER);
        h.setSpacing(4);
        t = new Text(pluginFolderTextField);
        t.setTextAlignment(TextAlignment.CENTER);
        t.setFont(new Font(10));
        t.setStyle("-fx-fill: lightgray;");
        t.setWrappingWidth(150);
        pluginFolderButton.getStyleClass().add("button-ui");
        pluginFolderButton.setOnAction(event -> {
            Stage stage = (Stage) super.getScene().getWindow();
            File f = new File(pluginFolderTextField);
            if (f.isDirectory()) {
                directoryChooser.setInitialDirectory(new File(pluginFolderTextField));
            }
            File file = directoryChooser.showDialog(stage);
            if (file != null) {
                pluginFolderTextField = file.getAbsolutePath();
                t.setText(pluginFolderTextField);
            }
        });

        h.getChildren().addAll(pluginFolderButton, t);
        changed = false;
    }

    public void setCheckBoxes(List<String> foundList, List<String> activatedList, List<String> loadedPluginsNames) {
        checkBoxes.clear();
        for (String found : foundList) {
            CheckBox b = new CheckBox(found);
            b.setOnAction((event)->changed = true);
            if(activatedList.contains(found)) {
                b.selectedProperty().setValue(true);
            } else {
                b.selectedProperty().setValue(false);
            }
            checkBoxes.add(b);
        }
        ArrayList<String> activatedNotFound = new ArrayList<>();
        for(String activated: activatedList) {
            if(!foundList.contains(activated)) {
                activatedNotFound.add(activated);
            }
        }
        for (String found : activatedNotFound) {
            CheckBox b = new CheckBox(found);
            b.setOnAction((event)->changed = true);
            b.selectedProperty().setValue(true);
            checkBoxes.add(b);
        }
        for(CheckBox b : checkBoxes) {
            if(!loadedPluginsNames.contains(b.getText()) && !foundList.contains(b.getText())) {
                b.setStyle("-fx-text-fill: darkgray");
            }
        }
    }

    public List<String> getChecked() {
        ArrayList<String> checked = new ArrayList<>();
        for(CheckBox checkBox: checkBoxes){
            if(checkBox.selectedProperty().getValue()) {
                checked.add(checkBox.getText());
            }
        }
        return checked;
    }

    @Override
    public void expand() {
        VBox v = new VBox();
        VBox.setMargin(v, new Insets(6, 0, 3, 0));
        v.getChildren().addAll(checkBoxes);
        v.setSpacing(10);
        v.getChildren().add(h);
        this.getChildren().add(v);
        super.expand();
    }


    @Override
    public void collapse() {
        this.getChildren().remove(this.getChildren().size() - 1);
        super.collapse();
    }

    public void setPluginFolder(String pathTextPath) {
        pluginFolderTextField = pathTextPath;
        t.setText(pluginFolderTextField);
    }

    public String getPluginFolder(String pluginFolder) {
        if (!(pluginFolder.equals(pluginFolderTextField))) {
            changed = true;
        }
        return pluginFolderTextField;
    }

    public boolean hasChanged(){
        boolean out = changed;
        changed = false;
        return out;
    }

    public boolean isExpanded() {return super.isExpanded();}

    public void refresh() {super.refresh();}


}
