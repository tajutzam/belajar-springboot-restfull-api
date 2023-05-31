package com.zam.contactmanagement.services;

import com.zam.contactmanagement.entity.User;
import com.zam.contactmanagement.model.UserRegisterRequest;
import com.zam.contactmanagement.model.UserResponse;
import com.zam.contactmanagement.model.UserUpdateRequest;

public interface UserService {

    void register(UserRegisterRequest request);

    UserResponse getCurrentUser(User user);

    UserResponse update(User user , UserUpdateRequest request);


}
