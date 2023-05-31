package com.zam.contactmanagement.services.impl;

import com.zam.contactmanagement.entity.User;
import com.zam.contactmanagement.model.TokenResponse;
import com.zam.contactmanagement.model.UserLoginRequest;
import com.zam.contactmanagement.repository.UserRepository;
import com.zam.contactmanagement.security.BCrypt;
import com.zam.contactmanagement.services.AuthService;
import jakarta.transaction.Transactional;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Calendar;
import java.util.UUID;


@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;

    @Override
    @Transactional
    public TokenResponse login(UserLoginRequest request) {

        validationService.validate(request);

        val userOptional = userRepository.findById(request.getUsername());
             userOptional.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED , "Username or password wrong"));

        val user = userOptional.get();
        if(BCrypt.checkpw(request.getPassword() , user.getPassword())){
            user.setToken(UUID.randomUUID().toString());
            user.setTokenExpired(next3DAYS());
            userRepository.save(user);
        }else{
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED , "Username or password wrong");
        }
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setToken(user.getToken());
        tokenResponse.setExpired_at(user.getTokenExpired());
        return tokenResponse;
    }

    private Long next3DAYS(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH , 3);
        return calendar.getTimeInMillis();
    }

    @Override
    @Transactional
    public void logout(User user) {
        user.setToken(null);
        user.setTokenExpired(null);
        userRepository.save(user);
    }
}
