package com.karalius.attendance;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class DataHolder {


    private final ObservableList<Group> studentGroups = FXCollections.observableArrayList();

    private final static DataHolder dataholder = new DataHolder();
    private DataHolder() {

    }

    public static DataHolder getInstance(){
        return dataholder;
    }

    public ObservableList<Group> getStudentGroups() {
        return studentGroups;
    }

}
