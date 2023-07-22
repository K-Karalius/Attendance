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
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
                    Date selectedDate = getDate();
                    if(student.getAttendanceComboBox().getValue() == Attendance.present){
                        student.setAttendanceOnDate(selectedDate, Attendance.present);
                    }else if (student.getAttendanceComboBox().getValue() == Attendance.absent){
                        student.setAttendanceOnDate(selectedDate, Attendance.absent);
                    }else{
                        student.setAttendanceOnDate(selectedDate, Attendance.unknown);
                    }
                }
            }
        }catch(Exception e){
            showAlertBox(e.getMessage());
        }
    }

    public Date getDate(){
        LocalDate localDate = datePicker.getValue();
        Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
        return Date.from(instant);
    }
    @FXML
    public void saveToPdf(){
        if(datePicker.getValue() != null){

            try{

                Date selectedDate = getDate();

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
                saveToCSV();
                showAlertBox("Data saved!");
            }else if(fileName.getText().endsWith(".xlsx")){
                saveToXLSX();
                showAlertBox("Data saved!");
            }else{
                showAlertBox(".cvs and .xlsx files only!");
            }
        }catch(Exception e){
            showAlertBox("Unable to save data to file");
        }
    }

    public void saveToCSV() throws Exception{

        String selectedGroup = groupSelector.getValue();
        if(selectedGroup != null){
            String fileNameStr = fileName.getText();

            File file = new File(fileNameStr);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(fileNameStr);

            Group group = findGroup(selectedGroup);
            writer.write(selectedGroup + ',');

            ArrayList<Date> tempDates = new ArrayList<>();

            if(group.getStudents().size() != 0){
                HashMap<Date, Attendance> attendanceDates = group.getStudents().get(0).getAttendanceDates();
                String dateString;
                int i = 1;
                for(Date date : attendanceDates.keySet()) {
                    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                    dateString = formatter.format(date);
                    tempDates.add(date);
                    writer.write(dateString);
                    if(attendanceDates.size() != i){
                        writer.write(',');
                    }
                    i++;
                }
                writer.write('\n');
            }

            Student student;
            for(int i = 0; i < group.getStudents().size(); i++){
                student = group.getStudents().get(i);
                writer.write(student.getName() + ',');

                Attendance attendance;
                for(int j = 0 ; j < tempDates.size(); j++){
                    attendance = student.getAttendanceOnDate(tempDates.get(j));
                    if(attendance == Attendance.present){
                        writer.write("+");
                    }else if(attendance == Attendance.absent){
                        writer.write("-");
                    }

                    if(j != tempDates.size() - 1){
                        writer.write(',');
                    }
                }
                writer.write('\n');
            }
            writer.close();
        }
    }
    public void saveToXLSX() throws Exception{

        String selectedGroup = groupSelector.getValue();
        if(selectedGroup != null){
            XSSFWorkbook workbook = new XSSFWorkbook();
            CreationHelper createHelper = workbook.getCreationHelper();
            Sheet sheet = workbook.createSheet(selectedGroup);

            Group group = findGroup(selectedGroup);
            Row row = sheet.createRow(0);
            row.createCell(0).setCellValue(selectedGroup);
            if(group.getStudents().size() != 0){
                HashMap<Date, Attendance> attendanceDates = group.getStudents().get(0).getAttendanceDates();
                CellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("m/d/yy"));
                int i = 1;
                for(Date date : attendanceDates.keySet()){
                    Cell cell = row.createCell(i);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(date);
                    i++;
                }


                Row newRow;
                Student student;
                for(int j = 1; j <= group.getStudents().size(); j++) {
                    newRow = sheet.createRow(j);
                    student = group.getStudents().get(j - 1);
                    newRow.createCell(0).setCellValue(student.getName());

                    for (int k = 1; k <= attendanceDates.size(); k++) {
                        Date date = sheet.getRow(0).getCell(k).getDateCellValue();
                        Attendance attendance = student.getAttendanceOnDate(date);
                        Cell cell = newRow.createCell(k);
                        if (attendance == Attendance.present) {
                            cell.setCellValue("+");
                        } else if (attendance == Attendance.absent) {
                            cell.setCellValue("-");
                        }
                    }
                }
                sheet.autoSizeColumn(0);
            }

            FileOutputStream outputStream = new FileOutputStream(fileName.getText());
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        }
    }

    public void uploadFromFile() {
        try{
            if(fileName.getText().endsWith(".csv")){
                uploadFromCSV();
                checkIfGroupsEmpty();
            } else if(fileName.getText().endsWith(".xlsx")){
                uploadFromXLSX();
                checkIfGroupsEmpty();
            } else{
                showAlertBox(".cvs and .xlsx files only!");
            }
        }catch(Exception e){
            showAlertBox("Unable to upload data from file!");
        }
    }

    public void uploadFromCSV() throws Exception{
        String fileNameStr = fileName.getText();
        String line;
        BufferedReader reader = new BufferedReader(new FileReader(fileNameStr));
        String[] firstLine;
        String[] data;
        ArrayList<Date> dates = new ArrayList<>();

        line = reader.readLine();
        firstLine = line.split(",");


        String groupName = firstLine[0];
        Group group = findGroup(groupName);
        if(group == null){
            group = new Group();
            group.setName(groupName);
            groups.add(group);
        }

        String dateInString;
        Date date;
        for(int i = 1; i < firstLine.length; i++){
            dateInString = firstLine[i];
            date = new SimpleDateFormat("MM/dd/yyyy").parse(dateInString);
            dates.add(date);
        }


        String studentName;
        String attendance;
        while((line = reader.readLine()) != null){
            data = line.split(",");
            studentName = data[0];

            for(int j = 1; j < data.length; j++){
                Student student = findStudent(group, studentName);
                if(student == null){
                    student = new Student();
                    student.setName(studentName);
                    group.getStudents().add(student);
                }

                attendance = data[j];
                if(attendance.equals("+")){
                    student.setAttendanceOnDate(dates.get(j - 1), Attendance.present);
                }else if(attendance.equals("-")){
                    student.setAttendanceOnDate(dates.get(j - 1), Attendance.absent);
                }else{
                    student.setAttendanceOnDate(dates.get(j - 1), Attendance.unknown);
                }
            }
        }
        reader.close();
    }

    public void uploadFromXLSX() throws Exception{

        Workbook workbook = WorkbookFactory.create(new File(fileName.getText()));
        Sheet sheet = workbook.getSheetAt(0);
        Row dateRow = sheet.getRow(0);
        int numOfDates = dateRow.getLastCellNum();


        String groupName = dateRow.getCell(0).getStringCellValue();
        Group group = findGroup(groupName);
        if(group == null){
            group = new Group();
            group.setName(groupName);
            groups.add(group);
        }

        int lastRowNum = sheet.getLastRowNum();
        for(int i = 1; i <= lastRowNum; i++){
            String studentName = sheet.getRow(i).getCell(0).getStringCellValue();
            Student student = findStudent(group, studentName);
            if(student == null){
                student = new Student();
                student.setName(studentName);
                group.getStudents().add(student);
            }

            for(int j = 1; j < numOfDates; j++){

                Cell cell = sheet.getRow(i).getCell(j);
                Date date = sheet.getRow(0).getCell(j).getDateCellValue();

                if(cell != null && cell.getCellType() != CellType.BLANK){
                    String cellValue = cell.getStringCellValue();
                    if(cellValue.equals("+")){
                        student.setAttendanceOnDate(date, Attendance.present);
                    }else if(cellValue.equals("-")) {
                        student.setAttendanceOnDate(date, Attendance.absent);
                    }
                }else{
                    student.setAttendanceOnDate(date, Attendance.unknown);
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


