package com.zam.contactmanagement.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zam.contactmanagement.entity.User;
import com.zam.contactmanagement.model.TokenResponse;
import com.zam.contactmanagement.model.UserLoginRequest;
import com.zam.contactmanagement.model.UserRegisterRequest;
import com.zam.contactmanagement.model.WebResponse;
import com.zam.contactmanagement.repository.UserRepository;
import com.zam.contactmanagement.services.AuthService;
import com.zam.contactmanagement.services.UserService;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        UserRegisterRequest request = new UserRegisterRequest();
        request.setPassword("rahasia");
        request.setUsername("user");
        request.setName("zam");
        userService.register(request);
    }

    @Test
    public void testLoginUserNotFound() throws Exception {
        UserLoginRequest request = new UserLoginRequest();
        request.setPassword("not found user");
        request.setUsername("rahasia");

        mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(
               result -> {
                   val webResponse = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
                   });
                   Assertions.assertNotNull(webResponse.getErrors());
               }
        );
    }

    @Test
    public void testLoginWrongPassword() throws Exception {
        UserLoginRequest request = new UserLoginRequest();
        request.setPassword("user");
        request.setUsername("wrong");

        mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(
                result -> {
                    val webResponse = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
                    });
                    Assertions.assertNotNull(webResponse.getErrors());
                }
        );
    }

    @Test
    public void testLoginSuccess() throws Exception {
        UserLoginRequest request = new UserLoginRequest();
        request.setUsername("user");
        request.setPassword("rahasia");

        mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                    val webResponse = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<TokenResponse>>() {
                    });
                    Assertions.assertNotNull(webResponse.getData().getToken());
                }
        );
    }


    @Test
    public void testLogoutFailed() throws Exception {
        mockMvc.perform(
                delete("/api/auth/logout")
                        .accept(MediaType.APPLICATION_JSON)

        ).andExpectAll(
                status().isUnauthorized()
        )
                .andDo(result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
                    });
                    Assertions.assertNotNull(response.getErrors());
                });
    }


    @Test
    public void testLogoutSuccess() throws Exception{

        User user = new User();
        user.setUsername("test");
        user.setPassword("rahasia");
        user.setTokenExpired(System.currentTimeMillis() + 10000000L);
        user.setName("zam");
        user.setToken("test");

        userRepository.save(user);

        mockMvc.perform(
                delete("/api/auth/logout")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN" , "test")
        ).andExpectAll(status().isOk())
                .andDo(result -> {
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
                    });
                    Assertions.assertNull(response.getErrors());
                    Assertions.assertEquals("OK" , response.getData());
                });
    }

}
