package com.example.lwb;

public class Event {
    private String name;
    private String description;
    private String time;
    private String date;
    private String place;
    public int count;

    public Event(String name, String description, String time, String date, String place, int count) {
        this.name = name;
        this.description = description;
        this.time = time;
        this.date = date;
        this.place = place;
        this.count = count;
    }


    public Event(String name, String date, String time) {
        this.name = name;
        this.date=date;
        this.time = time;
    }


//    public Event(String name, String description, String time) {
//        this.name = name;
//        this.description = description;
//        this.time = time;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

}
