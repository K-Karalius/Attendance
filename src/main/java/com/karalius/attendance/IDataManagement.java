package com.karalius.attendance;

import javafx.event.ActionEvent;
public interface IDataManagement {
    void modifyGroups(ActionEvent event);
    void modifyStudents(ActionEvent event);
    void saveAttendance();
    void saveToPdf();
    void saveToCSV() throws Exception;
    void saveToXLSX() throws Exception;
    void uploadFromCSV() throws Exception;
    void uploadFromXLSX()throws Exception;
}
