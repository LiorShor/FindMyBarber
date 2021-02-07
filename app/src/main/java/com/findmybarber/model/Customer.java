package com.findmybarber.model;

public class Customer implements User {
    private String FirstName;
    private String LastName;
    private String Email;
    private String PhoneNumber;
    private String Password;

    public Customer(String firstName, String lastName, String email, String phoneNumber, String password) {
        FirstName = firstName;
        LastName = lastName;
        Email = email;
        PhoneNumber = phoneNumber;
        Password = password;
    }

    public Customer(){}


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
