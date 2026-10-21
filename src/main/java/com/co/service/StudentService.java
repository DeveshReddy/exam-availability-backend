package com.co.service;

import com.co.model.Student;
import com.co.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    // Business Logic for saving a new student
    public Student registerStudent(Student student) {
        // Automatically set the password to the registration number if it's empty
        if (student.getPassword() == null || student.getPassword().isEmpty()) {
            student.setPassword(student.getRegNo());
        }
        // Save to the database
        return studentRepository.save(student);
    }
}