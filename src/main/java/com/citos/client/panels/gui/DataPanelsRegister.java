/*
 * Copyright (c) 2017. Johannes Engler
 */

package com.citos.client.panels.gui;

import com.citos.client.PhoneNumber;
import com.citos.client.SqlLiteConnection;
import com.citos.client.messagestage.ErrorMessage;
import com.citos.client.panels.gui.fields.AddressField;
import com.citos.client.panels.gui.fields.HistoryField;
import com.citos.client.panels.gui.fields.InternField;
import com.citos.client.panels.gui.fields.NewInternField;
import com.citos.client.panels.gui.fields.callrecordevents.*;
import com.citos.client.panels.gui.fields.internevents.AddInternEvent;
import com.citos.client.panels.gui.fields.internevents.RemoveInternAndUpdateEvent;
import com.citos.client.panels.gui.fields.internevents.ReorderDroppedEvent;
import com.citos.client.panels.gui.fields.otherevents.*;
import com.citos.client.panels.gui.fields.serverconnectionhandlerevents.*;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by johannes on 06.04.2017.
 */
public class DataPanelsRegister {
    //Key = phonenumber/extension of the intern
    private HashMap<String, InternField> internFields;
    //Key = phonenumber/extension of the intern
    private Map<String, PhoneNumber> internNumbers;
    private HashSet<HistoryField> historyFields;
    private SqlLiteConnection sqlLiteConnection;
    private VBox panelA;
    private VBox panelB;
    private VBox panelC;
    private Button buttonNext;
    private Button buttonLast;
    private int hFieldPerSite = 5;
    private int historyfieldCount = 0;
    private int maxHistoryFieldcount = 5;
    private boolean sortByCallCount = true;
    // Safes the last query for refreshs on the list
    private String searchPaneAValue = "";
    private String searchPaneCValue = "";
    private long searchPaneCTimestamp;
    private boolean searchPaneCBlock = false;
    private String searchPaneCString = "";
    // Used to cache name <-> number reference (cdr packets usw)
    private HashMap<String, String> resolveCache;

    private EventBus eventBus;

    public DataPanelsRegister(EventBus bus, SqlLiteConnection sqlLiteConnection, VBox[] panels, Button[] buttons) {

        this.panelA = panels[0];
        this.panelB = panels[1];
        this.panelC = panels[2];

        this.buttonLast = buttons[0];
        this.buttonNext = buttons[1];

        this.eventBus = bus;
        this.eventBus.register(this);
        this.sqlLiteConnection = sqlLiteConnection;
        internNumbers = sqlLiteConnection.getInterns();
        panelA.setSpacing(1);
        panelB.setSpacing(1);
        panelC.setSpacing(1);

        internFields = new HashMap();
        internNumbers.entrySet().stream().forEach(g
                -> internFields.put(g.getKey(), new InternField(g.getValue().getName(), g.getValue().getCount(),g.getValue().getPosition(), g.getKey(), eventBus,sortByCallCount)));

        updateView(new ArrayList<>(internFields.values()));
        historyFields = new HashSet();
        panelC.getChildren().addAll(historyFields);

        buttonNext.setOnMouseClicked(event -> {
            ++historyfieldCount;
            historyFields.clear();
            this.buttonLast.setDisable(false);
            orderNextSideHistory();
            // if for the next are not enough fields -> Disable button
            if ((historyfieldCount + 1) * hFieldPerSite > maxHistoryFieldcount) {
                this.buttonNext.setDisable(true);
            }
        });

        buttonLast.setOnMouseClicked(event -> {
            --historyfieldCount;
            historyFields.clear();
            this.buttonNext.setDisable(false);
            orderNextSideHistory();
            // if user is on page 0 (start page, newest) -> Disable button
            if (historyfieldCount == 0) {
                this.buttonLast.setDisable(true);
            }
        });

        resolveCache = new HashMap<>();
    }

    @Subscribe
    public void logInSuccessful(UserLoginStatusEvent event) {
        historyFields.clear();
        panelC.getChildren().clear();
        if (event.isLoggedIn()) {
            internFields.entrySet().stream().forEach(g -> eventBus.post(new AboStatusExtensionEvent(g.getValue().getNumber())));
        }
        eventBus.post(new AskForCdrCountEvent());
        eventBus.post(new OrderCDRsEvent(historyfieldCount * hFieldPerSite, hFieldPerSite));
    }

    @Subscribe
    public void addInternAndUpdate(AddInternEvent event) {
        PhoneNumber p = event.getPhoneNumber();
        for (InternField field : internFields.values()) {
            if (field.getName().equals(p.getName())) {
                Logger.getLogger(getClass().getName()).log(Level.WARNING, "There already exists a user with that phonenumber.");
                new ErrorMessage("There already exists a user with the same name");
                return;
            }
        }
        if (!internFields.containsKey(p.getPhoneNumber())) {
            //Get last position and increment for new internfield (if available)
            int maxPosInternfield = -1;
            if(internFields.size()>0) {
                maxPosInternfield = Collections.max(internFields.values(), Comparator.comparing(InternField::getPosition)).getPosition();
            }
            p.setPosition(maxPosInternfield+1);

            //Write new intern into database and add field to UI
            sqlLiteConnection.queryNoReturn("Insert into internfields (id, number,name,callcount,position) " +
                    "values (((Select max(id) from internfields)+1),'" + p.getPhoneNumber() + "','" + p.getName() + "'," + p.getCount() + "," + p.getPosition() + ")");
            internNumbers.put(p.getPhoneNumber(), p);
            internFields.put(p.getPhoneNumber(), new InternField(p.getName(), p.getCount(), internFields.size(), p.getPhoneNumber(), eventBus,sortByCallCount));
            eventBus.post(new AboStatusExtensionEvent(p.getPhoneNumber()));
            updateView(generateReducedSetFromList(searchPaneAValue, new ArrayList<>(internFields.values())));
        } else {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "There already exists a user with that phonenumber.");
            new ErrorMessage("There already exists a user with the same phone number");
        }
    }

    public List<InternField> generateReducedInternSet(String val) {
        this.searchPaneAValue = val;
        ArrayList<InternField> out = new ArrayList<>();
        internFields.values().stream().filter(f -> f.getName().toLowerCase().contains(val.toLowerCase())).forEachOrdered(f -> out.add(f));
        return out;
    }

    public List<InternField> generateReducedSetFromList(String val, ArrayList<InternField> internFieldsLocal) {
        ArrayList<InternField> out = new ArrayList<>();
        internFieldsLocal.stream().filter(f -> f.getName().toLowerCase().contains(val.toLowerCase())).forEachOrdered(f -> out.add(f));
        return out;
    }

    @Subscribe
    public void addCdrAndUpdate(AddCdrAndUpdateEvent event) {
        //if we're currently in search mode and not on site 1 accept cdr packets which aren't ordered
        if (((!event.isOrdered() && !searchPaneCBlock && historyfieldCount == 0)
                || (event.isOrdered() && !searchPaneCBlock)
                || (event.isOrdered() && searchPaneCTimestamp <= event.getSearchInvokedTimestamp())) && event.getDisposition() == 4) {
            searchPaneCTimestamp = event.getSearchInvokedTimestamp();
            if (internFields.containsKey(event.getWho())) {
                InternField internField = internFields.get(event.getWho());
                String name = internField.getName();
                HistoryField f = new HistoryField(name, event.getWho(), event.getWhen(), event.getHowLong(), event.isOutgoing(),
                        event.isInternal(), event.getCountryCode(), event.getPrefix(), event.getTimeStamp(), event.getSearchText(), eventBus);
                historyFields.add(f);
            } else if (resolveCache.containsKey(event.getWho())) {
                HistoryField f = new HistoryField(resolveCache.get(event.getWho()), event.getWho(), event.getWhen(), event.getHowLong(), event.isOutgoing(),
                        event.isInternal(), event.getCountryCode(), event.getPrefix(), event.getTimeStamp(), "", eventBus);
                historyFields.add(f);
            } else {
                eventBus.post(new SearchDataSourcesForCdrEvent(event));
                return;
            }

            addHistoryFieldsSorted();
        }
    }

    @Subscribe
    public void addCdrUpdateWithNameFromDataSource(FoundCdrNameInDataSourceEvent event) {
        HistoryField f = new HistoryField(event.getName(), event.getWho(), event.getWhen(), event.getHowLong(),
                event.isOutgoing(), event.isInternal(), event.getCountryCode(), event.getPrefix(), event.getTimeStamp(), "", eventBus);
        historyFields.add(f);
        addHistoryFieldsSorted();
        resolveCache.put(event.getWho(), event.getName());
    }

    // print the history fields on the screen - copy of historyfield because of concurrent modufication errors
    public void addHistoryFieldsSorted() {
        ArrayList<HistoryField> hf = new ArrayList<>();
        Iterator<HistoryField> historyFieldsIt = historyFields.iterator();
        while (historyFieldsIt.hasNext()) {
            //TODO: Here is sometimes a concurrent modification exception -> Maybe concurrent hashset or equal
            hf.add(historyFieldsIt.next());
        }
        Collections.sort(hf, Comparator.comparingLong(HistoryField::getTimeStamp));
        Collections.reverse(hf);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                panelC.getChildren().clear();
                if (hf.size() > 4) {
                    panelC.getChildren().addAll(hf.subList(0, hFieldPerSite));
                } else {
                    panelC.getChildren().addAll(hf);
                }
            }
        });
    }

    @Subscribe
    public void addCdrUpdateWithoutName(NotFoundCdrNameInDataSourceEvent event) {
        HistoryField f = new HistoryField(event.getWho(), event.getWhen(), event.getHowLong(), event.isOutgoing(),
                event.isInternal(), event.getCountryCode(), event.getPrefix(), event.getTimeStamp(), "", eventBus);
        historyFields.add(f);

        // The data comes from a not FX Thread ->  Therefore use run later
       addHistoryFieldsSorted();
    }

    @Subscribe
    public void removeCdrAndUpdate(RemoveCdrAndUpdateLocalEvent event) {
        HistoryField f = event.getHistoryField();
        // Fetch current side (to get newest chagne from database) and clear historyfields
        eventBus.post(new RemoveCdrAndUpdateGlobalEvent(f, historyfieldCount * hFieldPerSite, hFieldPerSite));
        searchPaneC(searchPaneCString, true);
        historyFields.clear();
        panelC.getChildren().clear();
    }

    @Subscribe
    public void setStatus(SetStatusEvent event) {
        internFields.get(event.getIntern()).setStatus(event.getStatus());
    }

    @Subscribe
    public void removeInternAndUpdate(RemoveInternAndUpdateEvent event) {
        sqlLiteConnection.queryNoReturn("Delete from internfields where number='" + event.getNumber() + "'");
        internFields.remove(event.getNumber());
        internNumbers.remove(event.getNumber());
        eventBus.post(new DeAboStatusExtensionEvent(event.getNumber()));
        updateView(generateReducedSetFromList(searchPaneAValue, new ArrayList<>(internFields.values())));
    }

    @Subscribe
    public void incrementCallCount(CallEvent event) {
        if (event.isIntern()) {
            sqlLiteConnection.updateOneAttribute("internfields", "number", event.getPhoneNumber(), "callcount",
                    String.valueOf(Integer.valueOf(sqlLiteConnection.query("Select callcount from internfields where number = '" + event.getPhoneNumber() + "'")) + 1));

            internFields.get(event.getPhoneNumber()).incCount();
            updateView(generateReducedSetFromList(searchPaneAValue, new ArrayList<>(internFields.values())));
        }
    }

    public void updateView(List<InternField> i) {
        if (sortByCallCount) {
            //Sort by comparing the callcount
            Collections.sort(i, Comparator.comparingInt(InternField::getCount));
            //Callcount -> Higher is better
            Collections.reverse(i);
        } else {
            //use the saved positions
            Collections.sort(i, Comparator.comparingInt(InternField::getPosition));
        }
        panelA.getChildren().clear();
        panelA.getChildren().addAll(i);
        panelA.getChildren().add(new NewInternField(eventBus));
    }

    @Subscribe
    public void updateAddressFields(UpdateAddressFieldsEvent event) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                panelB.getChildren().clear();
                ArrayList<AddressField> addressFields = new ArrayList<>();
                event.getAddressBookEntries().stream().forEach(ent -> addressFields.add(new AddressField(2, ent, eventBus)));
                panelB.getChildren().addAll(addressFields);
            }
        });
    }

    @Subscribe
    public void closeApplication(CloseApplicationSafelyEvent event) {
        eventBus.unregister(this);
    }

    @Subscribe
    public void viewOptionsChanged(ViewOptionsChangedEvent event) {
        this.sortByCallCount = event.isSortByCallCount();
        internFields.values().stream().forEach(internField -> {
            if (sortByCallCount) {
                internField.disableDragAndDrop();
            } else {
                internField.enableDragAndDrop();
            }
            internFields.put(internField.getNumber(), internField);
        });
        updateView(generateReducedSetFromList(searchPaneAValue, new ArrayList<>(internFields.values())));
    }

    @Subscribe
    public void serverConnectionLost(ConnectionToServerLostEvent event) {
        for (InternField f : internFields.values()) {
            f.setStatus(4);
        }
    }

    @Subscribe
    public void cdrAmountInDatabaseUpdate(CdrCountEvent event) {
        maxHistoryFieldcount = event.getCurrentAmount();
        if ((historyfieldCount + 1) * hFieldPerSite >= maxHistoryFieldcount) {
            this.buttonNext.setDisable(true);
        } else {
            this.buttonNext.setDisable(false);
        }
    }

    public int getAmountHistoryFields() {
        return hFieldPerSite;
    }


    @Subscribe
    public void setHistorySearchNumberNotFoundFeedback(CdrNotFoundOnServerEvent event) {
        if (event.getTimestamp() > searchPaneCTimestamp) {
            historyFields.clear();
            searchPaneCTimestamp = event.getTimestamp();
        }
    }

    public void searchPaneC(String newValue, boolean deleteRefresh) {
        historyfieldCount = 0;
        if (newValue.matches("^[0-9]*$") && newValue.length() > 0) {
            //Search for Number in database on host
            this.eventBus.post(new SearchCdrInDatabaseEvent(newValue, getAmountHistoryFields(), historyfieldCount * hFieldPerSite));
            searchPaneCValue = newValue;
            searchPaneCBlock = true;
        } else if (newValue.length() == 0 && !deleteRefresh) {
            historyfieldCount = 0;
            this.buttonLast.setDisable(true);
            searchPaneCBlock = false;
            this.eventBus.post(new OrderCDRsEvent(historyfieldCount * hFieldPerSite, hFieldPerSite));
        } else {
            //Resolve number from name and search this in database
            if (newValue.length() > 0) {
                ResolveNumberFromNameEvent event = new ResolveNumberFromNameEvent(5, newValue, historyfieldCount * hFieldPerSite);
                this.eventBus.post(event);
                Optional<InternField> found = internFields.values().stream().filter(internField -> internField.getName().toLowerCase().contains(event.getName().toLowerCase())).findFirst();
                if (found.isPresent()) {
                    this.eventBus.post(new SearchCdrInDatabaseEvent(found.get().getNumber(), getAmountHistoryFields(), historyfieldCount * hFieldPerSite, event.getTimestamp()));
                }
                searchPaneCBlock = true;
                buttonLast.setDisable(true);
                buttonNext.setDisable(true);
            }
        }
        searchPaneCString = newValue;
        historyFields.clear();
        panelC.getChildren().clear();
    }


    @Subscribe
    public void reorderDragDropInternfields(ReorderDroppedEvent event) {
        Optional<InternField> drag = internFields.values().stream().filter(node -> node.isWasDragged()).findFirst();
        if (drag.isPresent()) {
            String number = drag.get().getNumber();
            int start = event.getResolvedPosition();
            int replace = start;
            int end = drag.get().getPosition();
            int dz = 1;
            if (end == start || (start - 1) == end) {
                InternField dragged = drag.get();
                dragged.hidePopup();
                internFields.put(dragged.getNumber(), dragged);
                return;
            } else if (end < start) {
                int c = start;
                start = end;
                replace = replace - 1;
                end = c - 1;
                dz = -1;
            }
            for (InternField internField : internFields.values()) {
                if (internField.getPosition() <= end && internField.getPosition() >= start) {
                    internField.setPosition(internField.getPosition() + dz);
                    internFields.put(internField.getNumber(), internField);
                    sqlLiteConnection.updateOneAttribute("internfields", "number", internField.getNumber(), "position", String.valueOf(internField.getPosition()));
                }
            }
            InternField dragged = drag.get();
            dragged.setPosition(replace);
            dragged.hidePopup();
            dragged.setWasDragged(false);
            internFields.put(dragged.getNumber(), dragged);
            sqlLiteConnection.updateOneAttribute("internfields", "number", dragged.getNumber(), "position", String.valueOf(dragged.getPosition()));
            updateView(new ArrayList<>(internFields.values()));
        }
    }

    @Subscribe
    public void pluginLoaded(PluginLoadedEvent event) {
        // prevents doubling of entries -> As without check client would order CDRS twice
        if(historyFields.size() > 0) {
            historyFields.clear();
            panelC.getChildren().clear();
            this.eventBus.post(new OrderCDRsEvent(historyfieldCount * hFieldPerSite, hFieldPerSite));
        }
    }

    private void orderNextSideHistory() {
        if (searchPaneCBlock) {
            ResolveNumberFromNameEvent event = new ResolveNumberFromNameEvent(5, searchPaneCString, historyfieldCount * hFieldPerSite);
            this.eventBus.post(event);
            Optional<InternField> found = internFields.values().stream().filter(internField -> internField.getName().toLowerCase().contains(event.getName().toLowerCase())).findFirst();
            if (found.isPresent()) {
                this.eventBus.post(new SearchCdrInDatabaseEvent(found.get().getNumber(), getAmountHistoryFields(), historyfieldCount * hFieldPerSite, event.getTimestamp()));
            }
        } else {
            this.eventBus.post(new OrderCDRsEvent(historyfieldCount * hFieldPerSite, hFieldPerSite));
        }
    }


}
