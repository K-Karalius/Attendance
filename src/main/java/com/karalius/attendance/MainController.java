package com.karalius.attendance;

import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;


public class MainController implements Initializable, IDataManagement {

    private ObservableList<Group> groups;
    private TableView<Student> table;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<String> groupSelector;
    @FXML
    private Button saveButton;
    @FXML
    private TextField fileName;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        groups = DataHolder.getInstance().getStudentGroups();
        table = new DisplayTable();
        AnchorPane.setTopAnchor(table, 10.00);
        AnchorPane.setRightAnchor(table, 40.00);
        anchorPane.getChildren().add(table);
        checkIfGroupsEmpty();
    }

    public void checkIfGroupsEmpty(){
        if(groups.isEmpty()){
            datePicker.setDisable(true);
            groupSelector.setDisable(true);
            saveButton.setDisable(true);
        }else{
            updateGroupSelector();
            datePicker.setDisable(false);
            groupSelector.setDisable(false);
            saveButton.setDisable(false);
        }
    }
    @FXML
    public void dateIsSelected() {
        display();
    }

    @FXML
    public void groupSelected() {
        display();
    }

    public void display(){
        if(datePicker.getValue() != null && groupSelector.getValue() != null){
            LocalDate localDate = datePicker.getValue();
            Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
            Date selectedDate = Date.from(instant);
            Group selecetedGroup = findGroup(groupSelector.getValue());
            selecetedGroup.setUpComboBoxes(selectedDate);
            table.setItems(selecetedGroup.getStudents());
        }
    }


    @FXML
    public void modifyGroups(ActionEvent event) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("modifyingGroups.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setTitle("Add/modify/delete groups");
            stage.setScene(scene);
            stage.show();
        }catch(Exception e){
            showAlertBox("Cannot open group modifier window!");
        }
    }

    @FXML
    public void modifyStudents(ActionEvent event) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("modifyingStudents.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setTitle("Add/modify/delete groups");
            stage.setScene(scene);
            stage.show();
        }catch(Exception e){
            showAlertBox("Cannot open student modifier window!");
        }
    }

    @FXML
    public void saveAttendance() {
        try{
            if(groupSelector.getValue() != null && datePicker.getValue() != null){
                Group selectedGroup = findGroup(groupSelector.getValue());
                for(Student student : selectedGroup.getStudents()){
                    if(student.getAttendanceComboBox().getValue() == Attendance.present){
                        LocalDate localDate = datePicker.getValue();
                        Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
                        Date selectedDate = Date.from(instant);
                        student.setAttendanceOnDate(selectedDate, Attendance.present);
                    }else if (student.getAttendanceComboBox().getValue() == Attendance.absent){
                        LocalDate localDate = datePicker.getValue();
                        Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
                        Date selectedDate = Date.from(instant);
                        student.setAttendanceOnDate(selectedDate, Attendance.absent);
                    }
                }
            }
        }catch(Exception e){
            showAlertBox(e.getMessage());
        }
    }
    @FXML
    public void saveToPdf(){
        if(datePicker.getValue() != null){

            try{
                LocalDate localDate = datePicker.getValue();
                Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
                Date selectedDate = Date.from(instant);

                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream("output.pdf"));
                document.open();

                document.add(new Paragraph("Date: " + datePicker.getValue().toString()));
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("\n"));

                for(Group group : groups){
                    document.add(new Paragraph("Group: " + group.getName()));
                    document.add(new Paragraph("(Name, attendance)"));
                    for(Student student : group.getStudents()){
                        document.add(new Paragraph(student.getStudentInfo(selectedDate)));
                    }
                    document.add(new Paragraph("\n"));
                }
                document.close();

                showAlertBox("Data saved to a pdf file!");
            }catch(Exception e){
                showAlertBox("Unable to save!");
            }

        }else{
            showAlertBox("Please select the date and group!");
        }

    }

    public void saveToFile(){
        try{
            if(fileName.getText().endsWith(".csv")){
                saveToCVS();
                showAlertBox("Students uploaded successfully!");
            }else if(fileName.getText().endsWith(".xlsx")){
                saveToXLSX();
                showAlertBox("Students uploaded successfully!");
            }else{
                showAlertBox(".cvs and .xlsx files only!");
            }
        }catch(Exception e){
            showAlertBox("Unable to save data to file");
        }
    }

    public void saveToCVS() throws Exception{
        String fileNameStr = fileName.getText();

        File file = new File(fileNameStr);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter writer = new FileWriter(fileNameStr);

        int rows = 0;
        for(Group group : groups){
            writer.write(group.getName() + ',');
            if(rows < group.getStudents().size()){
                rows = group.getStudents().size();
            }
        }
        writer.write('\n');

        for(int i = 1; i <= rows; i++){
            for(Group group : groups){
                if(i - 1 < group.getStudents().size()){
                    writer.write(group.getStudents().get(i - 1).getName());
                }

                if(groups.size() - 1 != groups.indexOf(group)){
                    writer.write(',');
                }

            }
            writer.write('\n');
        }
        writer.close();
    }
    public void saveToXLSX()throws Exception{
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Students");

        int rowCount = -1;
        Row row = sheet.createRow(0);
        Group group;
        for(int i = 0; i < groups.size(); i++){
            group = groups.get(i);
            Cell groupNameCell = row.createCell(i);
            groupNameCell.setCellValue(group.getName());
            if(rowCount < group.getStudents().size()){
                rowCount = group.getStudents().size();
            }
        }
        for(int i = 1; i <= rowCount; i++){
            row = sheet.createRow(i);
            for(int j = 0; j < groups.size(); j++) {
                group = groups.get(j);
                if(i - 1 < group.getStudents().size()){
                    Cell studentNameCell = row.createCell(j);
                    studentNameCell.setCellValue(group.getStudents().get(i - 1).getName());
                }else{
                    Cell studentNameCell = row.createCell(j);
                    studentNameCell.setCellValue("");
                }
            }
        }

        FileOutputStream outputStream = new FileOutputStream(fileName.getText());
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    public void uploadFromFile() {
        try{
            if(fileName.getText().endsWith(".csv")){
                uploadFromCVS();
                checkIfGroupsEmpty();
                showAlertBox("Students uploaded successfully!");
            } else if(fileName.getText().endsWith(".xlsx")){
                uploadFromXLSX();
                checkIfGroupsEmpty();
                showAlertBox("Students uploaded successfully!");
            } else{
                showAlertBox(".cvs and .xlsx files only!");
            }
        }catch(Exception e){
            showAlertBox("Unable to upload data from file!");
        }
    }

    public void uploadFromCVS() throws Exception{
        String fileNameStr = fileName.getText();
        String line;
        BufferedReader reader = new BufferedReader(new FileReader(fileNameStr));
        String[] groupNames;
        String[] studentNames;

        line = reader.readLine();
        groupNames = line.split(",");
        for(String name : groupNames){
            if(findGroup(name) == null){
                Group group = new Group();
                group.setName(name);
                groups.add(group);
            }
        }
        while((line = reader.readLine()) != null){
            studentNames = line.split(",");
            for(int i = 0; i < studentNames.length; i++){
                Group group = findGroup(groupNames[i]);
                if(findStudent(findGroup(groupNames[i]),studentNames[i]) == null
                        && !studentNames[i].isEmpty()){
                    Student student = new Student();
                    student.setName(studentNames[i]);
                    group.getStudents().add(student);
                }
            }
        }
        reader.close();
    }

    public void uploadFromXLSX() throws Exception{
        Workbook workbook = WorkbookFactory.create(new File(fileName.getText()));
        Sheet sheet = workbook.getSheetAt(0);
        Row groupRow = sheet.getRow(0);
        int numOfGroups = groupRow.getLastCellNum();

        String groupName;
        for(int i = 0; i < numOfGroups; i++){
            groupName = groupRow.getCell(i).getStringCellValue();
            if(findGroup(groupName) == null){
                Group group = new Group();
                group.setName(groupName);
                groups.add(group);
            }
        }
        String studentName;
        for(int i = 1; i <= sheet.getLastRowNum(); i++){
            Row studentRow = sheet.getRow(i);
            for(int j = 0; j < numOfGroups; j++){
                if(studentRow.getCell(j) == null){
                    continue;
                }
                studentName = studentRow.getCell(j).getStringCellValue();
                Group group = findGroup(groupRow.getCell(j).getStringCellValue());
                if(findStudent(group,studentName) == null){
                    Student student = new Student();
                    student.setName(studentName);
                    group.getStudents().add(student);
                }
            }
        }
        workbook.close();
    }

    public void updateGroupSelector(){
        groupSelector.getItems().clear();
        for(Group group : groups){
            groupSelector.getItems().add(group.getName());
        }
    }

    public Group findGroup(String name){
        for(Group group : groups){
            if(group.getName().equals(name)){
                return group;
            }
        }
        return null;
    }
    public Student findStudent(Group group, String name){
        for(Student student : group.getStudents()){
            if(student.getName().equals(name)){
                return student;
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


