package com.karalius.attendance;

import javafx.scene.control.ComboBox;

import java.util.Date;
import java.util.HashMap;
enum Attendance{
    unknown,
    present,
    absent
}
public class Student {

    private String name;
    private final HashMap<Date, Attendance> attendanceOnDate;
    private final ComboBox<Attendance> attendanceComboBox;

    public Student(){
        attendanceOnDate = new HashMap<Date, Attendance>();
        attendanceComboBox = new ComboBox<Attendance>();
        attendanceComboBox.getItems().addAll(Attendance.unknown, Attendance.present, Attendance.absent);
        attendanceComboBox.setValue(Attendance.unknown);
    }

    public void setUpComboBox(Date date){
        Attendance temp = attendanceOnDate.get(date);
        if(temp != null){
            attendanceComboBox.setValue(temp);
        }else{
            attendanceComboBox.setValue(Attendance.unknown);
        }
    }
    public String getStudentInfo(Date date){
        Attendance temp = attendanceOnDate.get(date);
        String attendanceStr;
        if(temp != null){
            attendanceStr = temp.toString();
            return (name + "    " + attendanceStr);
        }else{
            attendanceStr = Attendance.unknown.toString();
            return (name + "    " + attendanceStr);
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Attendance getAttendanceOnDate(Date date) {
        return attendanceOnDate.get(date);
    }

    public void setAttendanceOnDate(Date date, Attendance attendance) {
        attendanceOnDate.put(date, attendance);
    }

    public ComboBox<Attendance> getAttendanceComboBox(){
        return attendanceComboBox;
    }

}
