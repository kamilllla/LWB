package com.example.lwb.Models;

public class Booking {
    private String bookId;
    private String name;
    private String surname;
    private String patronomyc;
    private String numberOfPhone;
    private String email;
    private String accountId;
    private String nameOfEvent;
    private String timeOfEvent;
    private String dateOfEvent;
    private int countOfPlaces;

    public Booking() {
    }

    public Booking(String bookId, String name, String surname, String patronomyc, String numberOfPhone, String email, String accountId, String nameOfEvent, String timeOfEvent, String dateOfEvent, int countOfPlaces) {
        this.bookId=bookId;
        this.name = name;
        this.surname = surname;
        this.patronomyc = patronomyc;
        this.numberOfPhone = numberOfPhone;
        this.email = email;
        this.accountId = accountId;
        this.nameOfEvent = nameOfEvent;
        this.timeOfEvent = timeOfEvent;
        this.dateOfEvent = dateOfEvent;
        this.countOfPlaces = countOfPlaces;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getNameOfEvent() {
        return nameOfEvent;
    }

    public void setNameOfEvent(String nameOfEvent) {
        this.nameOfEvent = nameOfEvent;
    }

    public String getTimeOfEvent() {
        return timeOfEvent;
    }

    public void setTimeOfEvent(String timeOfEvent) {
        this.timeOfEvent = timeOfEvent;
    }

    public String getDateOfEvent() {
        return dateOfEvent;
    }

    public void setDateOfEvent(String dateOfEvent) {
        this.dateOfEvent = dateOfEvent;
    }


    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
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
