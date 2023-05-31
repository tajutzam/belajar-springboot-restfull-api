package com.zam.contactmanagement.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zam.contactmanagement.entity.User;
import com.zam.contactmanagement.model.UserRegisterRequest;
import com.zam.contactmanagement.model.UserResponse;
import com.zam.contactmanagement.model.UserUpdateRequest;
import com.zam.contactmanagement.model.WebResponse;
import com.zam.contactmanagement.repository.UserRepository;
import com.zam.contactmanagement.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;


    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }


    @Test
    public void testRegisterSuccess() throws Exception {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setName("test");
        request.setUsername("test");
        request.setPassword("rahasia");

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
            });

            Assertions.assertEquals("OK", response.getData());
        });
    }


    @Test
    public void testRegisterFailedValidation() throws Exception {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setName("");
        request.setUsername("");
        request.setPassword("");

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
            });

            Assertions.assertNotNull(response.getErrors());
        });
    }

    @Test
    public void testRegisterFailedDuplicateUser() throws Exception {
        User user = new User();
        user.setName("test");
        user.setUsername("test");
        user.setPassword("test");
        userRepository.save(user);

        UserRegisterRequest request = new UserRegisterRequest();
        request.setName("test");
        request.setUsername("test");
        request.setPassword("test");

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
            });

            Assertions.assertNotNull(response.getErrors());
        });
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }


    @Test
    public void testUserUnauthorizedTokenFailed() throws Exception {
        mockMvc.perform(
                get("/api/users/current").
                        accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "salah token")

        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
            });
            Assertions.assertNotNull(response.getErrors());
        });
    }

    @Test
    public void testUserUnauthorizedTokenNull() throws Exception {
        mockMvc.perform(
                get("/api/users/current").
                        accept(MediaType.APPLICATION_JSON)

        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
            });
            Assertions.assertNotNull(response.getErrors());
        });
    }

    @Test
    public void testGetUserCurrentSuccess() throws Exception {
        User user = new User();
        user.setToken("test");
        user.setName("test");
        user.setTokenExpired(System.currentTimeMillis() + (1000 * 16 * 24 * 30));
        user.setPassword("test");
        user.setUsername("test");

        userRepository.save(user);

        mockMvc.perform(
                get("/api/users/current").
                        accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")

        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<UserResponse>>() {
            });
            Assertions.assertNull(response.getErrors());
            Assertions.assertEquals("test", response.getData().getUsername());
        });
    }

    @Test
    public void testGetUserCurrentTokenExpired() throws Exception {
        User user = new User();
        user.setToken("test");
        user.setName("test");
        user.setTokenExpired(System.currentTimeMillis() - (1000 * 16 * 24 * 30));
        user.setPassword("test");
        user.setUsername("test");

        userRepository.save(user);

        mockMvc.perform(
                get("/api/users/current").
                        accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")

        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<UserResponse>>() {
            });
            Assertions.assertNotNull(response.getErrors());
        });
    }


    @Test
    public void testUpdateSuccess() throws Exception {

        User user = new User();
        user.setToken("test");
        user.setName("test");
        user.setTokenExpired(System.currentTimeMillis() + (1000 * 16 * 24 * 30));
        user.setPassword("test");
        user.setUsername("test");

        userRepository.save(user);

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setUsername("test");
        userUpdateRequest.setPassword("rahasia");
        mockMvc.perform(
                put("/api/users/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateRequest))
                        .header("X-API-TOKEN" , "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<UserResponse> webResponse = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<UserResponse>>() {
            });
            Assertions.assertNotNull(webResponse.getData().getUsername());
        });

    }

    @Test
    public void testUpdateUnauthorized() throws Exception {

        User user = new User();
        user.setToken("test");
        user.setName("test");
        user.setTokenExpired(System.currentTimeMillis() + (1000 * 16 * 24 * 30));
        user.setPassword("test");
        user.setUsername("test");

        userRepository.save(user);

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setUsername("test");
        userUpdateRequest.setPassword("rahasia");
        mockMvc.perform(
                put("/api/users/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateRequest))
                        .header("X-API-TOKEN" , "wrong")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> webResponse = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
            });
            Assertions.assertNull(webResponse.getData());
            Assertions.assertNotNull(webResponse.getErrors());
        });

    }
}
