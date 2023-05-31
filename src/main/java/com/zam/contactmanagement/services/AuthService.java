package com.zam.contactmanagement.services;

import com.zam.contactmanagement.entity.User;
import com.zam.contactmanagement.model.TokenResponse;
import com.zam.contactmanagement.model.UserLoginRequest;

public interface AuthService {

    TokenResponse login(UserLoginRequest request);


    void logout(User user);

}
