package com.findmybarber.model;

public class Store {
    private String ID;
    private String Name;
    private String Address;
    private double Rank;
    private String Image;
    private String Description;
    private long PhoneNumber;
    private double Latitude;
    private double Longitude;
    private double Distance;
    public double getDistance() {
        return Distance;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        this.Image = image;
    }

    public void setDistance(double distance) {
        this.Distance = distance;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        this.Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        this.Longitude = longitude;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        this.Address = address;
    }

    public double getRank() {
        return Rank;
    }

    public void setRank(double rank) {
        this.Rank = rank;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        this.Description = description;
    }

    public long getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.PhoneNumber = phoneNumber;
    }

    public Store(String id, String name, String address, double rank,String image, String description, long phoneNumber, double latitude, double longitude) {
        this.ID = id;
        this.Name = name;
        this.Address = address;
        this.Rank = rank;
        this.Image = image;
        this.Description = description;
        this.PhoneNumber = phoneNumber;
        this.Latitude = latitude;
        this.Longitude = longitude;
    }
}

