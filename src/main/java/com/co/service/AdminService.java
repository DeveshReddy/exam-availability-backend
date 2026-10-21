package com.co.service;

import com.co.model.Student;
import com.co.model.Admin;
import com.co.model.ExamSubmission;
import com.co.model.AvailabilityRequest;
import com.co.model.AvailabilityResponse;
import com.co.repository.StudentRepository;
import com.co.repository.AdminRepository;
import com.co.repository.ExamSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ExamSubmissionRepository examSubmissionRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

//    // --- Admin Authentication ---
//    public Optional<Admin> authenticateAdmin(String id, String password) {
//        return adminRepository.findById(id)
//                .filter(admin -> encoder.matches(password, admin.getPassword()));
//    }
public Optional<Admin> authenticateAdmin(String id, String password) {

    System.out.println("Entered ID: '" + id + "'");

    Optional<Admin> adminOpt = adminRepository.findById(id);

    if (adminOpt.isEmpty()) {
        System.out.println("❌ ADMIN NOT FOUND");
        return Optional.empty();
    }

    Admin admin = adminOpt.get();

    System.out.println("DB Password: " + admin.getPassword());

    boolean match = encoder.matches(password, admin.getPassword());
    System.out.println("Password match: " + match);

    if (match) {
        System.out.println("✅ LOGIN SUCCESS");
        return Optional.of(admin);
    }

    System.out.println("❌ WRONG PASSWORD");
    return Optional.empty();
}


    // --- Admin Password Reset ---
    public void resetAdminPassword(String adminId, String newPassword) {
        adminRepository.findById(adminId).ifPresent(admin -> {
            admin.setPassword(encoder.encode(newPassword));
            admin.setTemporary(false);
            adminRepository.save(admin);
        });
    }

    public List<Student> getCollidingStudents() {
        return studentRepository.findAll();
    }

    // --- Availability Checker (Restore this method!) ---
    public List<AvailabilityResponse> checkAvailability(AvailabilityRequest request) {
        List<AvailabilityResponse> results = new ArrayList<>();
        for (String regNo : request.getRegNos()) {
            AvailabilityResponse response = new AvailabilityResponse();
            response.setRegNo(regNo);

            Optional<Student> studentOpt = studentRepository.findById(regNo);
            if (studentOpt.isPresent()) {
                Student student = studentOpt.get();
                response.setName(student.getName());
                response.setDepartment(student.getDepartment());
                response.setFaName(student.getFaName());
                response.setFaMobile(student.getFaMobile());

                List<ExamSubmission> collidingExams = examSubmissionRepository
                        .findByExamDateAndExamSlotAndStudentRegNo(request.getDate(), request.getSlot(), regNo);

                if (!collidingExams.isEmpty()) {
                    ExamSubmission exam = collidingExams.get(0);
                    response.setStatus("HAS EXAM");
                    response.setExamSubject(exam.getSubjectName());
                    response.setSubjectCode(exam.getSubjectCode());
                } else {
                    response.setStatus("NO EXAM");
                    response.setExamSubject("-");
                    response.setSubjectCode("-");
                }
            } else {
                response.setName("Unknown Student");
                response.setStatus("NOT FOUND");
                response.setExamSubject("-");
                response.setSubjectCode("-");
            }
            results.add(response);
        }
        return results;
    }


    public List<Map<String, Object>> getCollisions() {
        return adminRepository.findCollisions();
    }
}