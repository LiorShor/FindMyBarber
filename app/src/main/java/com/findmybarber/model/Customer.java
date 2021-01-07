package com.findmybarber.model;

public class Customer implements User {
    private String ID;
    private String FirstName;
    private String LastName;
    private String Email;
    private String PhoneNumber;
    private String Password;

    public Customer(String id, String firstName, String lastName, String email, String phoneNumber, String password) {
        this.ID = id;
        this.FirstName = firstName;
        this.LastName = lastName;
        this.Email = email;
        this.PhoneNumber = phoneNumber;
        this.Password = password;
    }

    public Customer(){}

    public String getID() {
        return ID;
    }

    @Override
    public String getUserEmail() {
        return Email;
    }

    @Override
    public String getUserName() {
        return FirstName;
    }

    @Override
    public String getUserSurname() {
        return LastName;
    }

    @Override
    public String getUserPhoneNumber() {
        return PhoneNumber;
    }

    @Override
    public UserType getUserType() {
        return UserType.Customer;
    }

    @Override
    public String getUserPassword() {
        return Password;
    }
}
