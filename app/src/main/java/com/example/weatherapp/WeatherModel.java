package com.example.weatherapp;

public class WeatherModel {
    String time,tempa,icon,winapeed;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTempa() {
        return tempa;
    }

    public void setTempa(String tempa) {
        this.tempa = tempa;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getWinapeed() {
        return winapeed;
    }

    public void setWinapeed(String winapeed) {
        this.winapeed = winapeed;
    }

    public WeatherModel(String time, String tempa, String icon, String winapeed) {
        this.time = time;
        this.tempa = tempa;
        this.icon = icon;
        this.winapeed = winapeed;
    }
}
