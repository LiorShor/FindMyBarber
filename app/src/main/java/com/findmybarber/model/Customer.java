package com.findmybarber.model;

import com.findmybarber.controller.User;
import com.findmybarber.controller.UserType;

public class Customer implements User {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;

    public Customer(String firstName, String lastName, String email, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;

    }

    public Customer(){}

    @Override
    public String getUserEmail() {
        return email;
    }

    @Override
    public String getUserName() {
        return firstName;
    }

    @Override
    public String getUserSurname() {
        return lastName;
    }

    @Override
    public String getUserPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public UserType getUserType() {
        return UserType.Customer;
    }
}
