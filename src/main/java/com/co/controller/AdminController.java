package com.co.controller;

import com.co.service.AdminService;
import com.co.model.*;
import com.co.repository.AdminRepository;
import com.co.repository.StudentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
//@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private StudentRepository studentRepository;

    // 🔐 PASSWORD ENCODER
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // =====================================================
    // ✅ CREATE ADMIN (FIXED + IMPORTANT)
    // =====================================================
    @PostMapping("/add")
    public ResponseEntity<?> createAdmin(@RequestBody Admin admin) {

        if (admin.getAdminId() == null || admin.getAdminId().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Admin ID required"));
        }

        String rawPassword;

        // Auto-generate password if not provided
        if (admin.getPassword() == null || admin.getPassword().isBlank()) {
            int random = 1000 + new Random().nextInt(9000);
            rawPassword = admin.getAdminId() + random;

            System.out.println("Generated Admin Password: " + rawPassword);

            admin.setPassword(encoder.encode(rawPassword));
        } else {
            rawPassword = admin.getPassword();
            admin.setPassword(encoder.encode(rawPassword));
        }

        // Force reset on first login
        admin.setTemporary(true);

        adminRepository.save(admin);

        return ResponseEntity.ok(Map.of(
                "message", "Admin created successfully",
                "adminId", admin.getAdminId(),
                "generatedPassword", rawPassword
        ));
    }

    // =====================================================
    // 👨‍🎓 GET ALL STUDENTS
    // =====================================================
    @GetMapping("/students")
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = studentRepository.findAllWithSubmissions();
        return ResponseEntity.ok(students);
    }

    // =====================================================
    // 🔍 SEARCH BY REG NO
    // =====================================================
    @GetMapping("/search/{regNo}")
    public ResponseEntity<?> searchByRegNo(@PathVariable String regNo) {

        Optional<Student> student = studentRepository.findById(regNo.toUpperCase());

        if (student.isPresent()) {
            return ResponseEntity.ok(student.get());
        }

        return ResponseEntity.status(404).body(Map.of("error", "Student not found"));
    }

    // =====================================================
    // 🔍 SEARCH BY DEPARTMENT
    // =====================================================
    @GetMapping("/search/dept")
    public ResponseEntity<?> searchByDept(@RequestParam String department) {

        List<Student> students = studentRepository.findByDepartment(department);

        if (!students.isEmpty()) {
            return ResponseEntity.ok(students);
        }

        return ResponseEntity.status(404).body(Map.of("error", "No students found"));
    }

    // =====================================================
    // 🔐 RESET PASSWORD
    // =====================================================
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {

        String adminId = request.get("adminId");
        String newPassword = request.get("newPassword");

        if (adminId == null || newPassword == null) {
            return ResponseEntity.badRequest().body("Admin ID and New Password are required");
        }

        adminService.resetAdminPassword(adminId, newPassword);

        return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
    }

    // =====================================================
    // 📊 CHECK AVAILABILITY
    // =====================================================
    @PostMapping("/check-availability")
    public ResponseEntity<List<AvailabilityResponse>> checkAvailability(
            @RequestBody AvailabilityRequest request) {

        List<AvailabilityResponse> results = adminService.checkAvailability(request);
        return ResponseEntity.ok(results);
    }


    // =====================================================
    // ⚠️ COLLISIONS (TEMP MOCK)
    // =====================================================
    @GetMapping("/collisions")
    public ResponseEntity<?> getCollisions() {
        return ResponseEntity.ok(List.of(
                Map.of("room", "A101", "time", "10:00 AM", "status", "Conflict"),
                Map.of("room", "B202", "time", "11:00 AM", "status", "Conflict")
        ));
    }
}