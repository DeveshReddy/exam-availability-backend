package com.co.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

@Entity
@Table(name = "exam_submissions")
public class ExamSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subjectName;

    @Column(name = "subject_code")
    private String subjectCode;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate examDate;

    private String examSlot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_reg_no", nullable = false)
    @JsonIgnore
    private Student student;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public LocalDate getExamDate() { return examDate; }
    public void setExamDate(LocalDate examDate) { this.examDate = examDate; }

    public String getExamSlot() { return examSlot; }
    public void setExamSlot(String examSlot) { this.examSlot = examSlot; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
}