package com.co.controller;

import com.co.service.StudentAccountService;
import com.co.service.FacultyAuthService;
import com.co.service.AdminService;
import com.co.model.LoginRequest;
import com.co.model.Admin;
import com.co.model.Student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
//@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private StudentAccountService studentAccountService;

    @Autowired
    private AdminService adminService;

    @PostMapping("/login")
    public ResponseEntity<?> handleLogin(@RequestBody LoginRequest req) {

        // 1. ADMIN LOGIN
        Optional<Admin> adminOpt = adminService.authenticateAdmin(req.getId(), req.getPassword());

        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (admin.isTemporary()) {
                return ResponseEntity.ok(Map.of(
                        "status", "MUST_RESET",
                        "adminId", admin.getAdminId()
                ));
            }

            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "adminId", admin.getAdminId(),
                    "role", "admin"
            ));
        }

        // 2. STUDENT LOGIN
        // Note: Using 'authenticateStudent' which matches your Service logic
        Optional<Student> studentOpt = studentAccountService.authenticateStudent(req.getId(), req.getPassword());

        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            // 🔥 ADDED: Check if the student must reset their password
            if (student.isTemporary()) {
                return ResponseEntity.ok(Map.of(
                        "status", "MUST_RESET",
                        "regNo", student.getRegNo()
                ));
            }
            // Inside AuthController.java -> student login success block
            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "regNo", student.getRegNo(),
                    "name", student.getName(),
                    "role", "student",
                    "email", student.getEmail() != null ? student.getEmail() : "",
                    "department", student.getDepartment() != null ? student.getDepartment() : ""
            ));
        }

        // 3. IF BOTH FAIL
        return ResponseEntity.status(401).body("Invalid Credentials");
    }
}