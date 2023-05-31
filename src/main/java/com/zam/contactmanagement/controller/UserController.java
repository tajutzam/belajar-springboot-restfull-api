package com.zam.contactmanagement.controller;

import com.zam.contactmanagement.entity.User;
import com.zam.contactmanagement.model.UserRegisterRequest;
import com.zam.contactmanagement.model.UserResponse;
import com.zam.contactmanagement.model.UserUpdateRequest;
import com.zam.contactmanagement.model.WebResponse;
import com.zam.contactmanagement.services.UserService;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(
            path = "api/users",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> register(@RequestBody UserRegisterRequest request) {
        userService.register(request);
        return WebResponse.<String>builder().data("OK").build();
    }

    @GetMapping(
            path = "api/users/current",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
//    use argument resolver , to get token user
    public WebResponse<UserResponse> get(User user) {
        val currentUser = userService.getCurrentUser(user);
        return WebResponse.<UserResponse>builder().
                data(currentUser).
                build();
    }

    @PutMapping(
            path = "api/users/update",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> update(User user , @RequestBody UserUpdateRequest request){
        UserResponse userResponse = userService.update(user, request);
        return WebResponse.<UserResponse>builder().data(userResponse).build();
    }



}
