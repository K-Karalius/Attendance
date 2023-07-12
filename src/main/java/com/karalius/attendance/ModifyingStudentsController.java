package com.karalius.attendance;

import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import java.net.URL;
import java.util.ResourceBundle;

public class ModifyingStudentsController extends EditingController {

    private ObservableList<Group> groups;
    private StudentsTable table;
    private Group selectedGroup;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private ComboBox<String> groupSelector;
    @FXML
    private TextField newName;
    @FXML
    private Button addButton;



    public void initialize(URL url, ResourceBundle resourceBundle){
        groups = DataHolder.getInstance().getStudentGroups();
        table = new StudentsTable();
        AnchorPane.setTopAnchor(table, 10.00);
        AnchorPane.setRightAnchor(table, 40.00);
        anchorPane.getChildren().add(table);
        if(groups.isEmpty()){
            groupSelector.setDisable(true);
            newName.setDisable(true);
            addButton.setDisable(true);
            showAlertBox("There are no groups created!");
        }else{
            updateGroupSelector();
            groupSelector.setDisable(false);
            newName.setDisable(false);
            addButton.setDisable(false);
        }
    }


    @FXML
    public void groupIsSelected() {
        selectedGroup = findGroup(groupSelector.getValue());
        if(selectedGroup != null){
            table.setItems(selectedGroup.getStudents());
        }

    }

    @FXML
    public void add() {
        try{
            if(selectedGroup != null){
                if(findStudent(selectedGroup, newName.getText()) == null){
                    Student student = new Student();
                    student.setName(newName.getText());
                    selectedGroup.getStudents().add(student);
                    groupIsSelected();                                     // update the table after adding the student
                }else{
                    throw new Exception("Student with the same name already exists in this group!");
                }
            }
        }catch (Exception e){
            showAlertBox(e.getMessage());
        }
    }

    @FXML
    public void delete() {
        Student student = table.getSelectionModel().getSelectedItem();
        if(student != null && selectedGroup != null){
            selectedGroup.getStudents().remove(student);
            table.refresh();
        }
    }

    public void updateGroupSelector(){
        groupSelector.getItems().clear();
        for(Group group : groups){
            groupSelector.getItems().add(group.getName());
        }
    }

    public Student findStudent(Group group, String name){
        for(Student student : group.getStudents()){
            if(student.getName().equals(name)){
                return student;
            }
        }
        return null;
    }

    public Group findGroup(String name){
        for(Group group : groups){
            if(group.getName().equals(name)){
                return group;
            }
        }
        return null;
    }

    public void showAlertBox(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Message");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
