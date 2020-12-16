package com.findmybarber.model;

import android.media.Image;

public class Store {
    private String id;
    private String name;
    private String location;
    private double rank;
//    private Image image;
    private String description;
    private long phoneNumber;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getRank() {
        return rank;
    }

    public void setRank(double rank) {
        this.rank = rank;
    }

//    public Image getImage() {
//        return image;
//    }
//
//    public void setImage(Image image) {
//        this.image = image;
//    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Store(String id, String name, String location, double rank, String description, long phoneNumber) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.rank = rank;
//        this.image = image;
        this.description = description;
        this.phoneNumber = phoneNumber;
    }
}

