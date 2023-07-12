package com.karalius.attendance;

import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

public class StudentsTable extends CustomTableView{
    public StudentsTable(){
        setPrefSize(150, 300);
        setEditable(false);
        createColumns();
    }

    public void createColumns(){
        TableColumn<Student, String> nameColumn = new TableColumn<Student, String>("Student name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(getPrefWidth());
        nameColumn.setEditable(false);
        nameColumn.setSortable(false);
        getColumns().add(nameColumn);
    }

}
