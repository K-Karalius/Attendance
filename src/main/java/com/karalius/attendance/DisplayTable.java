package com.karalius.attendance;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class DisplayTable extends TableView<Student> {

    public DisplayTable(){
        setPrefSize(250, 300);
        setEditable(false);
        //setItems(FXCollections.observableArrayList());
        createColumns();
    }

    public void createColumns(){
        double columnWidth = getPrefWidth() / 2;
        TableColumn<Student, String> nameColumn = new TableColumn<>("Student name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setSortable(false);
        nameColumn.setPrefWidth(columnWidth);

        TableColumn<Student, ComboBox<Attendance>> attendance = new TableColumn<>("Student attendance");
        attendance.setCellValueFactory(new PropertyValueFactory<>("attendanceComboBox"));
        attendance.setSortable(false);
        attendance.setPrefWidth(columnWidth);

        getColumns().add(nameColumn);
        getColumns().add(attendance);
    }
}
