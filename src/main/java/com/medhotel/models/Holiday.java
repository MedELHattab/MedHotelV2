package com.medhotel.models;

import java.time.LocalDate;

public class Holiday {
    private int id;
    private LocalDate holidayDate;

    public Holiday(int id, LocalDate holidayDate) {
        this.id = id;
        this.holidayDate = holidayDate;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getHolidayDate() {
        return holidayDate;
    }

    public void setHolidayDate(LocalDate holidayDate) {
        this.holidayDate = holidayDate;
    }

    @Override
    public String toString() {
        return "Holiday{" +
                "id=" + id +
                ", holidayDate=" + holidayDate +
                '}';
    }
}
