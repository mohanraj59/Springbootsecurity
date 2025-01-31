package com.itc.service;

import com.itc.model.ChangePasswordRequest;
import com.itc.model.ForgotPasswordRequest;
import com.itc.model.MyUsers;

public interface MyUserService {
	
	MyUsers addUser(MyUsers user);
	
	String initiatePasswordReset(ForgotPasswordRequest request);
    
    String changePassword(ChangePasswordRequest request);
}
