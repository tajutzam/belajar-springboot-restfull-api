package com.zam.contactmanagement.services.impl;

import com.zam.contactmanagement.entity.User;
import com.zam.contactmanagement.model.UserRegisterRequest;
import com.zam.contactmanagement.model.UserResponse;
import com.zam.contactmanagement.model.UserUpdateRequest;
import com.zam.contactmanagement.repository.UserRepository;
import com.zam.contactmanagement.security.BCrypt;
import com.zam.contactmanagement.services.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validator;



    @Override
    @Transactional
    public void register(UserRegisterRequest request) {
        // validate
        validator.validate(request);

        if (userRepository.existsById(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"username already registered");
        }


        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(BCrypt.hashpw(request.getPassword() , BCrypt.gensalt()));
        user.setName(request.getName());

        userRepository.save(user);
    }

    @Transactional
    @Override
    public UserResponse getCurrentUser(User user) {
        return UserResponse.builder().
        username(user.getUsername())
                .name(user.getName()).
                build();
    }

    @Transactional
    @Override
    public UserResponse update(User user, UserUpdateRequest request) {

        validator.validate(request);

        if(Objects.nonNull(request.getUsername())){
            user.setUsername(request.getUsername());
        }

        if(Objects.nonNull(request.getPassword())){
            user.setPassword(BCrypt.hashpw(request.getPassword() , BCrypt.gensalt()));
        }
        userRepository.save(user);

        return UserResponse.builder().
                username(user.getUsername())
                        .name(user.getName()).
                build();
    }


}
