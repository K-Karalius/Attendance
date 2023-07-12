package com.karalius.attendance;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Date;

public class Group {

    private String name;
    private final ObservableList<Student> students;

    public Group(){
        students = FXCollections.observableArrayList();
    }

    public void setUpComboBoxes(Date date){
        for(Student student : students){
            student.setUpComboBox(date);
        }
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ObservableList<Student> getStudents() {
        return students;
    }

}
