package com.co.repository;

import com.co.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {

    // 1. Search for all students in a specific department
    List<Student> findByDepartment(String department);

    // 2. Find a specific student by their email address
    Student findByEmail(String email);

    // 3. Fetch all students with their exam submissions in one query (fixes empty submissions in admin dashboard)
    @Query("SELECT DISTINCT s FROM Student s LEFT JOIN FETCH s.submissions")
    List<Student> findAllWithSubmissions();
}