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
    private final HashMap<Date, Attendance> attendanceDates;
    private final ComboBox<Attendance> attendanceComboBox;

    public Student(){
        attendanceDates = new HashMap<Date, Attendance>();
        attendanceComboBox = new ComboBox<Attendance>();
        attendanceComboBox.getItems().addAll(Attendance.unknown, Attendance.present, Attendance.absent);
        attendanceComboBox.setValue(Attendance.unknown);
    }

    public void setUpComboBox(Date date){
        Attendance temp = attendanceDates.get(date);
        if(temp != null){
            attendanceComboBox.setValue(temp);
        }else{
            attendanceComboBox.setValue(Attendance.unknown);
        }
    }
    public String getStudentInfo(Date date){
        Attendance temp = attendanceDates.get(date);
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

    public void setAttendanceOnDate(Date date, Attendance attendance) {
        attendanceDates.put(date, attendance);
    }
    public Attendance getAttendanceOnDate(Date date){
        return attendanceDates.get(date);
    }

    public ComboBox<Attendance> getAttendanceComboBox(){
        return attendanceComboBox;
    }
    public HashMap<Date, Attendance> getAttendanceDates(){
        return attendanceDates;
    }

}
