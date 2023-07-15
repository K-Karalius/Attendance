package com.karalius.attendance;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class EditingController implements Initializable {

    public abstract void add();

    public abstract void delete();

    public void backToMainWindow(ActionEvent event){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("mainWindowView.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setTitle("Attendance of students");
            stage.setScene(scene);
            stage.show();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}
