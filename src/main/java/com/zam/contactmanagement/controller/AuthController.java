package com.zam.contactmanagement.controller;

import com.zam.contactmanagement.entity.User;
import com.zam.contactmanagement.model.TokenResponse;
import com.zam.contactmanagement.model.UserLoginRequest;
import com.zam.contactmanagement.model.WebResponse;
import com.zam.contactmanagement.services.AuthService;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping(
            path = "/api/auth/login",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )

    public WebResponse<TokenResponse> login(@RequestBody UserLoginRequest request){
        val tokenResponse = authService.login(request);
        return WebResponse.<TokenResponse>builder().data(tokenResponse).build();
    }


    @DeleteMapping(
            path = "api/auth/logout",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> logout(User user){
        authService.logout(user);
        return WebResponse.<String>builder().data("OK").build();
    }
}
