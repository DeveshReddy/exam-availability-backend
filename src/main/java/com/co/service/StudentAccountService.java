package com.co.service;

import com.co.model.Student;
import com.co.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class StudentAccountService {

    @Autowired
    private StudentRepository studentRepository;

    // The Spring Security password encoder tool
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Version 2.0: Secure Student Login
    public Optional<Student> authenticateStudent(String regNo, String rawPassword) {
        return studentRepository.findById(regNo)
                // Use .matches() to compare what they typed with the hashed DB password
                .filter(student -> passwordEncoder.matches(rawPassword, student.getPassword()));
    }

    // Secure Password Update
    public boolean updateStudentPassword(String regNo, String oldPassword, String newPassword) {
        Optional<Student> studentOpt = studentRepository.findById(regNo);

        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();

            // Verify the old password against the stored hash
            if (passwordEncoder.matches(oldPassword, student.getPassword())) {

                // Hash the new password BEFORE saving to Postgres
                student.setPassword(passwordEncoder.encode(newPassword));
                studentRepository.save(student);
                return true;
            }
        }
        return false;
    }
}