package com.findmybarber.model;


public class Admin extends Customer implements User {
    private String StoreID;
    public Admin(String firstName, String lastName, String email, String phoneNumber, String password, String storeID) {
        super(firstName, lastName, email, phoneNumber, password);
        this.StoreID = storeID;
    }

    public Admin() {
    }

    public String getStoreID() {
        return StoreID;
    }

    @Override
    public UserType getUserType() {
        return UserType.Admin;
    }
}
