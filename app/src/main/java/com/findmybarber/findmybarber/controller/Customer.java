package com.findmybarber.findmybarber.controller;

public class Customer implements User {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phoneNumber;

    public Customer(String firstName, String lastName, String email, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
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
