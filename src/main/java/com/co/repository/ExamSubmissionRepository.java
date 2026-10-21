package com.co.repository;

import com.co.model.ExamSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExamSubmissionRepository extends JpaRepository<ExamSubmission, Long> {
    
    // Custom query to help the "Availability Checker" (Image 1 & 5)
    // Find exams on a specific date, in a specific slot, for a specific student
    List<ExamSubmission> findByExamDateAndExamSlotAndStudentRegNo(LocalDate date, String slot, String regNo);
}