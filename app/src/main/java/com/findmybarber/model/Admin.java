package com.findmybarber.model;


public class Admin extends Customer implements User {
    public Admin(String id,String firstName, String lastName, String email, String phoneNumber, String password) {
        super(id, firstName, lastName, email, phoneNumber, password);
    }

    public Admin() {
    }

    @Override
    public UserType getUserType() {
        return UserType.Admin;
    }
}
