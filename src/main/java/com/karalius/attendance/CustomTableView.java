package com.karalius.attendance;
import javafx.scene.control.TableView;

public abstract class CustomTableView extends TableView<Student>{
    public abstract void createColumns();

}
