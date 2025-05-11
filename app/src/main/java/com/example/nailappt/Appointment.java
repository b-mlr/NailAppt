package com.example.nailappt;

import androidx.annotation.NonNull;

import java.util.Map;

public class Appointment {
    public String advertiserID;
    public String bookedByID;
    public String date;
    public String time;
    public Map<String, String> location;
    public String phoneNum;
    public String otherContact;

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
                "date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", advertiserID='" + advertiserID + '\'' +
                ", phoneNum='" + phoneNum + '\'' +
                ", otherContact='" + otherContact + '\'' +
                ", location='" + location + '\'' +
                '}';
    }


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
