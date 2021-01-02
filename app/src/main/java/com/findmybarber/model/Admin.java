package com.findmybarber.model;


import com.findmybarber.controller.User;
import com.findmybarber.controller.UserType;

public class Admin extends Customer implements User {
    public Admin(String firstName, String lastName, String email, String phoneNumber, String password) {
        super(firstName, lastName, email, phoneNumber, password);
    }

    public Admin() {
    }

    @Override
    public UserType getUserType() {
        return UserType.Admin;
    }
}
