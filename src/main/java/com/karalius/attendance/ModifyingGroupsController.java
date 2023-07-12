package com.karalius.attendance;


import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ModifyingGroupsController extends EditingController {

    private ObservableList<Group> groups;
    private GroupsTable table;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TextField newName;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        groups = DataHolder.getInstance().getStudentGroups();
        table = new GroupsTable();
        AnchorPane.setTopAnchor(table, 10.00);
        AnchorPane.setRightAnchor(table, 40.00);
        anchorPane.getChildren().add(table);
        if(!groups.isEmpty()){
            display();
        }
    }
    @FXML
    public void add() {
        String tempName = newName.getText();
        if(findGroup(tempName) == null){
            Group group = new Group();
            group.setName(tempName);
            groups.add(group);
        }else {
            showAlertBox("Group with this name already exists");
        }
        display();

    }

    public void delete(){
        Group selectedGroup = table.getSelectionModel().getSelectedItem();
        if(selectedGroup != null){
            groups.remove(selectedGroup);
            table.refresh();
        }else{
            showAlertBox("Select a group to delete!");
        }
    }
    public void display(){
        table.setItems(groups);
    }
    @FXML
    public void backToMainWindow(ActionEvent event) {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("mainWindowView.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setTitle("Attendance of students");
            stage.setScene(scene);
            stage.show();
        }catch(Exception e){
            showAlertBox(e.getMessage());
        }
    }

    public Group findGroup(String name){
        for(Group group: groups){
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
