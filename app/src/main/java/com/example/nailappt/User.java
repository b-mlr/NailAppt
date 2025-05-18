package com.example.nailappt;

import java.util.Map;

public class User {
    private String uid;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private Map<String, String> location;
    private String otherContact;

    public User(){}

    public User(String uid, String email, String firstName, String lastName) {
        this.uid = uid;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public User(String uid, String email, String firstName, String lastName, Map<String, String> location,  String phone, String otherContact) {
        this.uid = uid;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.location = location;
        this.otherContact = otherContact;
    }


    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Map<String, String> getLocation() {
        return location;
    }

    public String getPhone() {
        return phone;
    }

    public String getOtherContact() {
        return otherContact;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setLocation(Map<String, String> location) {
        this.location = location;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setOtherContact(String otherContact) {
        this.otherContact = otherContact;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
