package com.example.lwb.Models;

public class Booking {
    private String name;
    private String surname;
    private String patronomyc;
    private String numberOfPhone;
    private String email;
    private int countOfPlaces;

    public Booking(String name, String surname, String patronomyc, String numberOfPhone, String email, int countOfPlaces) {
        this.name = name;
        this.surname = surname;
        this.patronomyc = patronomyc;
        this.numberOfPhone = numberOfPhone;
        this.email = email;
        this.countOfPlaces = countOfPlaces;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPatronomyc() {
        return patronomyc;
    }

    public void setPatronomyc(String patronomyc) {
        this.patronomyc = patronomyc;
    }

    public String getNumberOfPhone() {
        return numberOfPhone;
    }

    public void setNumberOfPhone(String numberOfPhone) {
        this.numberOfPhone = numberOfPhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCountOfPlaces() {
        return countOfPlaces;
    }

    public void setCountOfPlaces(int countOfPlaces) {
        this.countOfPlaces = countOfPlaces;
    }
}
