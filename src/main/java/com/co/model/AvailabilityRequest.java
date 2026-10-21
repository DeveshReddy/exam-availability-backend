package com.co.model;

import java.time.LocalDate;
import java.util.List;

public class AvailabilityRequest {
    private LocalDate date;
    private String slot; // "Morning" or "Afternoon"
    private List<String> regNos; // The list of students to check

    // --- GETTERS AND SETTERS ---
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getSlot() { return slot; }
    public void setSlot(String slot) { this.slot = slot; }

    public List<String> getRegNos() { return regNos; }
    public void setRegNos(List<String> regNos) { this.regNos = regNos; }
}