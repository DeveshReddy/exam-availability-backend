package com.co.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "student")
public class Student {

    @Id
    @Column(name = "reg_no", length = 50) // Explicitly maps to our VARCHAR(50) fix
    private String regNo;

    private String name;
    private String password;
    private String email;
    private String department;
    private boolean isTemporary;


    // Better naming convention (camelCase) and DB column mapping
//    @Column(name = "subject_code")
//    private String subjectCode;

    @Column(name = "fa_name")
    private String faName;

    @Column(name = "fa_mobile")
    private String faMobile;
    @Column(name = "fa_email")
    private String faEmail;

    // --- RELATIONSHIP: One Student to Many Exam Submissions ---
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ExamSubmission> submissions = new ArrayList<>();

    // --- CONSTRUCTORS ---
    public Student() {}

    public Student(String regNo, String name, String email) {
        this.regNo = regNo;
        this.name = name;
        this.email = email;
    }

    // --- GETTERS AND SETTERS ---

    public String getRegNo() { return regNo; }
    public void setRegNo(String regNo) { this.regNo = regNo; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    // -> NEW: Getters and Setters for Subject Code
//    public String getSubjectCode() { return subjectCode; }
//    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public String getFaName() { return faName; }
    public void setFaName(String faName) { this.faName = faName; }

    public String getFaMobile() { return faMobile; }
    public void setFaMobile(String faMobile) { this.faMobile = faMobile; }

    public List<ExamSubmission> getSubmissions() { return submissions; }
    public void setSubmissions(List<ExamSubmission> submissions) { this.submissions = submissions; }

    // --- THE HELPER METHOD ---
    /**
     * Use this method instead of .getSubmissions().add() 
     * to ensure the link is established in the database.
     */
    public void addSubmission(ExamSubmission submission) {
        submissions.add(submission);
        submission.setStudent(this); // This populates the student_reg_no column
    }
    public boolean isTemporary() { return isTemporary; }
    public void setTemporary(boolean temporary) { isTemporary = temporary;
    }
    // --- Add these Getters & Setters ---
    public String getFaEmail() {
        return faEmail;
    }

    public void setFaEmail(String faEmail) {
        this.faEmail = faEmail;
    }
}
