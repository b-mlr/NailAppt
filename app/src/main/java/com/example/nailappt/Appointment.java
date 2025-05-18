package com.example.nailappt;

import androidx.annotation.NonNull;

import java.util.Map;

public class Appointment {
    public String appointmentID;
    private String advertiserID;
    private String bookedByID;
    private String date;
    private String time;
    private Map<String, String> location;
    private String phoneNum;
    private String otherContact;

    public Appointment(){};

    public Appointment(String advertiserID, String bookedByID, String date, String time, Map<String, String> location, String phoneNum, String otherContact) {
        this.advertiserID = advertiserID;
        this.bookedByID = bookedByID;
        this.date = date;
        this.time = time;
        this.location = location;
        this.phoneNum = phoneNum;
        this.otherContact = otherContact;
    }

    @NonNull
    @Override
    public String toString() {
        return "Appointment{" +
                "appointmentID='" + appointmentID + '\'' +
                ", advertiserID='" + advertiserID + '\'' +
                ", bookedByID='" + bookedByID + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", location=" + location +
                ", phoneNum='" + phoneNum + '\'' +
                ", otherContact='" + otherContact + '\'' +
                '}';
    }

    public String getAppointmentID(){return appointmentID.trim();}
    public String getAdvertiserID() {
        return advertiserID;
    }

    public String getBookedByID() {
        return bookedByID;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public Map<String, String> getLocation() {
        return location;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public String getOtherContact() {
        return otherContact;
    }

    public void setAppointmentID(String appointmentID) { this.appointmentID = appointmentID;}

    public void setBookedByID(String bookedByID) {
        this.bookedByID = bookedByID;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setLocation(Map<String, String> location) {
        this.location = location;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public void setOtherContact(String otherContact) {
        this.otherContact = otherContact;
    }

    public void setAdvertiserID(String advertiserID) {
        this.advertiserID = advertiserID;
    }
}
