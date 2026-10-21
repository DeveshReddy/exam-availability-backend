package com.co.controller;

import com.co.model.Student;
import com.co.model.ExamSubmission;
import com.co.model.ExamSubmissionRequest;
import com.co.repository.StudentRepository;
import com.co.repository.ExamSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
// THE SECURITY IMPORT IS NOW ACTIVE
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/student")
//@CrossOrigin(origins = "*") // Required for Next.js to connect
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ExamSubmissionRepository examSubmissionRepository;

    // --- 1. Register a new Student (Strict Capitals + Secure Hashing) ---
    @PostMapping("/add")
    public ResponseEntity<?> registerStudent(@RequestBody Student student) {

        // FIX 1: Catch missing Registration Numbers immediately
        if (student.getRegNo() == null || student.getRegNo().trim().isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("error", "Registration Number (regNo) is required!"));
        }

        // FIX 2: STRICT CAPITALS CHECK
        if (!student.getRegNo().equals(student.getRegNo().toUpperCase())) {
            return ResponseEntity.status(400).body(Map.of(
                    "error", "Invalid format: Registration Number must be in STRICTLY CAPITAL LETTERS."
            ));
        }

        String rawPasswordToReturn = student.getPassword();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // FIX 3: Password generation
        if (student.getPassword() == null || student.getPassword().isBlank()) {
            Random random = new Random();
            int randomNumber = 1000 + random.nextInt(9000); // 1000 to 9999

            // Combine RegNo and Number for the user to see
            rawPasswordToReturn = student.getRegNo() + randomNumber;

            System.out.println("Generated Raw Password for " + student.getRegNo() + ": " + rawPasswordToReturn);

            // Hash it before saving to the database
            student.setPassword(encoder.encode(rawPasswordToReturn));
        } else {
            // If the frontend provided a password, hash that one!
            student.setPassword(encoder.encode(student.getPassword()));
        }
        // ✅ FIX APPLIED: Force them to reset on first login!
        student.setTemporary(true);

        // Save to Postgres (Saves the Hash, not the text)
        studentRepository.save(student);

        // Return JSON with the RAW generated password so the frontend can display it
        return ResponseEntity.ok(Map.of(
                "message", "Student added successfully!",
                "regNo", student.getRegNo(),
                "generatedPassword", rawPasswordToReturn
        ));
    }

    // --- 2. Submit Exam Details (With Subject Code & DB ID Return) ---
    @PostMapping("/{regNo}/submit-exam")
    public ResponseEntity<?> submitExam(@PathVariable String regNo, @RequestBody ExamSubmissionRequest request) {

        Optional<Student> studentOpt = studentRepository.findById(regNo);
        if (!studentOpt.isPresent()) {
            return ResponseEntity.status(404).body(Map.of("error", "Student not found!"));
        }

        Student student = studentOpt.get();

        // Update FA Details
        student.setFaName(request.getFaName());
        student.setFaMobile(request.getFaMobile());
        if (request.getFaEmail() != null) {
            student.setFaEmail(request.getFaEmail());
        }

        // Create the new Exam Submission
        ExamSubmission newExam = new ExamSubmission();
        newExam.setSubjectName(request.getSubjectName());
        newExam.setSubjectCode(request.getSubjectCode());
        newExam.setExamDate(request.getExamDate());
        newExam.setExamSlot(request.getExamSlot());

        // Attach the exam to the student
        student.addSubmission(newExam);

        // Save and get the saved instance to return the ID
        Student savedStudent = studentRepository.save(student);
        ExamSubmission savedExam = savedStudent.getSubmissions().get(savedStudent.getSubmissions().size() - 1);

        // Return JSON with the newly created DB ID
        return ResponseEntity.ok(Map.of(
                "message", "Exam details submitted successfully!",
                "id", savedExam.getId()
        ));
    }
    // --- 5. Edit/Update Exam Submission ---
    @PutMapping("/{regNo}/exam/{id}")
    public ResponseEntity<?> updateExam(
            @PathVariable String regNo,
            @PathVariable Long id,
            @RequestBody ExamSubmissionRequest request) {

        // 1. Verify the student exists
        Optional<Student> studentOpt = studentRepository.findById(regNo);
        if (!studentOpt.isPresent()) {
            return ResponseEntity.status(404).body(Map.of("error", "Student not found!"));
        }

        // 2. Verify the exam exists
        Optional<ExamSubmission> examOpt = examSubmissionRepository.findById(id);
        if (!examOpt.isPresent()) {
            return ResponseEntity.status(404).body(Map.of("error", "Exam record not found!"));
        }

        Student student = studentOpt.get();
        ExamSubmission examToUpdate = examOpt.get();

        // 3. Security check: Ensure this exam actually belongs to this student
        if (!examToUpdate.getStudent().getRegNo().equals(regNo)) {
            return ResponseEntity.status(403).body(Map.of("error", "Unauthorized to edit this exam!"));
        }

        // 4. Update the Exam Details
        examToUpdate.setSubjectName(request.getSubjectName());
        examToUpdate.setSubjectCode(request.getSubjectCode());
        examToUpdate.setExamDate(request.getExamDate());
        examToUpdate.setExamSlot(request.getExamSlot());

        // 5. Update the Student's FA Details (just in case they changed those too)
        student.setFaName(request.getFaName());
        student.setFaMobile(request.getFaMobile());
        if (request.getFaEmail() != null) {
            student.setFaEmail(request.getFaEmail());
        }

        // 6. Save both the updated exam and the student
        examSubmissionRepository.save(examToUpdate);
        studentRepository.save(student);

        return ResponseEntity.ok(Map.of(
                "message", "Exam details updated successfully!",
                "id", examToUpdate.getId()
        ));
    }

    // --- 3. Delete Exam Submission by ID ---
    @DeleteMapping("/exam/{id}")
    public ResponseEntity<?> deleteExam(@PathVariable Long id) {
        if (examSubmissionRepository.existsById(id)) {
            examSubmissionRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Exam submission deleted successfully."));
        } else {
            return ResponseEntity.status(404).body(Map.of("error", "Exam record not found."));
        }
    }
    // --- 4. Reset Student Password ---

    // Quick DTO to catch the JSON payload from the frontend
    public static class StudentResetRequest {
        private String regNo;
        private String newPassword;

        public String getRegNo() { return regNo; }
        public void setRegNo(String regNo) { this.regNo = regNo; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody StudentResetRequest request) {

        Optional<Student> studentOpt = studentRepository.findById(request.getRegNo());

        if (!studentOpt.isPresent()) {
            return ResponseEntity.status(404).body(Map.of("error", "Student not found!"));
        }

        Student student = studentOpt.get();

        // Hash the new password using your existing method
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        student.setPassword(encoder.encode(request.getNewPassword()));

        // ⚠️ IMPORTANT: If you have a field in your Student.java model that
        // forces the reset (like isTemporary), make sure to turn it off here!
        // Example:
        // student.setIsTemporary(false);

        // Save the updated student

        // ✅ TURN OFF THE RESET FLAG HERE
        student.setTemporary(false);

        // Save the updated student
        studentRepository.save(student);

        return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
    }
}