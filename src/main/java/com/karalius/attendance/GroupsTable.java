package com.karalius.attendance;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class GroupsTable extends TableView<Group> {
    public GroupsTable(){
        setPrefSize(150, 300);
        setEditable(false);
        createColumns();

    }

    public void createColumns(){
        TableColumn<Group, String> nameColumn = new TableColumn<Group, String>("Group name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setSortable(false);
        nameColumn.setEditable(false);
        nameColumn.setPrefWidth(getPrefWidth());
        getColumns().add(nameColumn);
    }
}
