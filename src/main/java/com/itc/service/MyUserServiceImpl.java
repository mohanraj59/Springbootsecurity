package com.itc.service;

import com.itc.model.ChangePasswordRequest;
import com.itc.model.ForgotPasswordRequest;
import com.itc.model.MyUsers;
import com.itc.repository.MyUserRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class MyUserServiceImpl implements MyUserService {

    @Autowired
    private MyUserRepo repo;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public MyUsers addUser(MyUsers user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return repo.save(user);
    }

    @Override
    public String initiatePasswordReset(ForgotPasswordRequest request) {
        String username = request.getUsername();
        Optional<MyUsers> userOptional = repo.findByUserName(username);
        
        if (userOptional.isPresent()) {
            MyUsers user = userOptional.get();
            String temporaryPassword = "temp@123";
            String encodedTemporaryPassword = encoder.encode(temporaryPassword);
            user.setPassword(encodedTemporaryPassword);
            repo.save(user);
            return "Temporary password set to 'temp@123'. Please use this to login and change your password.";
        } else {
            throw new RuntimeException("Username not found.");
        }
    }

    @Override
    public String changePassword(ChangePasswordRequest request) {
        String username = request.getUsername();
        String temporaryPassword = request.getTemporaryPassword();
        String newPassword = request.getNewPassword();
        String confirmNewPassword = request.getConfirmNewPassword();

        // Validate inputs
        if (!newPassword.equals(confirmNewPassword)) {
            throw new RuntimeException("New password and confirm password do not match.");
        }

        // Find the user
        MyUsers user = repo.findByUserName(username)
                        .orElseThrow(() -> new RuntimeException("Username not found."));

        // Validate temporary password
        if (!encoder.matches(temporaryPassword, user.getPassword())) {
            throw new RuntimeException("Invalid temporary password.");
        }

        // Set new password
        String encodedNewPassword = encoder.encode(newPassword);
        user.setPassword(encodedNewPassword);
        repo.save(user);

        return "Password changed successfully.";
    }
}