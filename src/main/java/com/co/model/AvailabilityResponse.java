package com.co.model;

public class AvailabilityResponse {
    private String regNo;
    private String name;
    private String department;
    private String status;      // "HAS EXAM" or "NO EXAM"
    private String examSubject; // Will be null or empty if they are free

    // --- ADDED THIS FIELD ---
    private String subjectCode;

    private String faName;
    private String faMobile;

    // --- GETTERS AND SETTERS ---
    public String getRegNo() { return regNo; }
    public void setRegNo(String regNo) { this.regNo = regNo; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getExamSubject() { return examSubject; }
    public void setExamSubject(String examSubject) { this.examSubject = examSubject; }

    // --- ADDED THESE TWO METHODS ---
    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public String getFaName() { return faName; }
    public void setFaName(String faName) { this.faName = faName; }

    public String getFaMobile() { return faMobile; }
    public void setFaMobile(String faMobile) { this.faMobile = faMobile; }
}