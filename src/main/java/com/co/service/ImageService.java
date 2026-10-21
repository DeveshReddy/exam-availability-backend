package com.co.service;

import com.co.model.Student;
import com.co.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Optional;

@Service
public class ImageService {

    @Autowired
    private StudentRepository studentRepository;

    // This class has ONE job: Handle Image Uploads
    public void uploadProfilePicture(String regNo, MultipartFile file) throws IOException {
        
        // 1. Find the student by their Registration Number
        Optional<Student> studentOptional = studentRepository.findById(regNo);
        
        if (studentOptional.isPresent()) {
            Student student = studentOptional.get();
            
//            // 2. Convert the image file into a byte array (BLOB)
//            student.setProfilePic(file.getBytes());
            
            // 3. Save the updated student back to the database
            studentRepository.save(student);
        } else {
            throw new RuntimeException("Student with Registration Number " + regNo + " not found!");
        }
    }
}