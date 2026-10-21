package com.co.service;

import com.co.model.Admin;
import com.co.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.Optional;

@Service
public class FacultyAuthService {

    @Autowired 
    private AdminRepository adminRepository;

    // Generate random temp password for new Faculty
    public Admin registerNewFaculty(String adminId) {
        Admin admin = new Admin();
        admin.setAdminId(adminId);
        
        // Random 8-character string
        String tempPass = UUID.randomUUID().toString().substring(0, 8);
        admin.setPassword(tempPass);
        admin.setTemporary(true); 
        
        return adminRepository.save(admin);
    }

    public Optional<Admin> authenticateFaculty(String id, String password) {
        return adminRepository.findById(id)
                .filter(admin -> admin.getPassword().equals(password));
    }
}