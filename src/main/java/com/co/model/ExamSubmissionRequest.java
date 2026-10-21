package com.co.model;

import java.time.LocalDate;

public class ExamSubmissionRequest {
    private String subjectName;
    private String subjectCode; // <-- ADDED THIS
    private LocalDate examDate;
    private String examSlot; // "Morning" or "Afternoon"

    // FA Details from the form
    private String faName;
    private String faMobile;
    private String faEmail;

    // --- GETTERS AND SETTERS ---
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    // <-- NEW GETTER AND SETTER FOR SUBJECT CODE -->
    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public LocalDate getExamDate() { return examDate; }
    public void setExamDate(LocalDate examDate) { this.examDate = examDate; }

    public String getExamSlot() { return examSlot; }
    public void setExamSlot(String examSlot) { this.examSlot = examSlot; }

    public String getFaName() { return faName; }
    public void setFaName(String faName) { this.faName = faName; }

    public String getFaMobile() { return faMobile; }
    public void setFaMobile(String faMobile) { this.faMobile = faMobile; }

    public String getFaEmail() { return faEmail; }
    public void setFaEmail(String faEmail) { this.faEmail = faEmail; }
}