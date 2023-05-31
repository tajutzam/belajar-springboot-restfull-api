package com.zam.contactmanagement.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zam.contactmanagement.entity.Contacts;
import com.zam.contactmanagement.entity.User;
import com.zam.contactmanagement.model.CreateContactRequest;
import com.zam.contactmanagement.model.ContactResponse;
import com.zam.contactmanagement.model.UpdateContactRequest;
import com.zam.contactmanagement.model.WebResponse;
import com.zam.contactmanagement.repository.ContactRepository;
import com.zam.contactmanagement.repository.UserRepository;
import com.zam.contactmanagement.security.BCrypt;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class ContactControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;



    @BeforeEach
    void setUp() {

        userRepository.deleteAll();
        contactRepository.deleteAll();

        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("test" , BCrypt.gensalt()));
        user.setName("test");
        user.setToken("test");
        user.setTokenExpired(System.currentTimeMillis() + 10000000L);
        userRepository.save(user);

    }


    @Test
    public void testAddContactSuccess() throws Exception {
        CreateContactRequest request = new CreateContactRequest();
        request.setEmail("mohammadtajutzamzami07@gmail.com");
        request.setFirstName("tajut");
        request.setLastName("zamzami");
        request.setPhone("085607185972");

        mockMvc.perform(
                post("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN" , "test")

        )
                .andExpectAll(
                        status().isOk()
                ).andDo(result -> {
                    WebResponse<ContactResponse> webResponse = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<ContactResponse>>() {
                    });
                    Assertions.assertNotNull(webResponse.getData().getId());
                });
    }

    @Test
    public void testAddContactsBadRequest() throws Exception {
        CreateContactRequest request = new CreateContactRequest();
        request.setEmail("mohammadtajutzamzami07@gmail.com");
        request.setLastName("zamzami");
        request.setPhone("085607185972");

        mockMvc.perform(
                        post("/api/contacts")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .header("X-API-TOKEN" , "test")

                )
                .andExpectAll(
                        status().isBadRequest()
                ).andDo(result -> {
                    WebResponse<ContactResponse> webResponse = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<ContactResponse>>() {
                    });
                    Assertions.assertNotNull(webResponse.getErrors());
                    Assertions.assertEquals("firstName: must not be blank" , webResponse.getErrors());
                });
    }


    @Test
    public void testAddContactUnauthorized() throws Exception {
        CreateContactRequest request = new CreateContactRequest();
        request.setEmail("mohammadtajutzamzami07@gmail.com");
        request.setFirstName("tajut");
        request.setLastName("zamzami");
        request.setPhone("085607185972");

        mockMvc.perform(
                        post("/api/contacts")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                                .header("X-API-TOKEN" , "salah token")

                )
                .andExpectAll(
                        status().isUnauthorized()
                ).andDo(result -> {
                    WebResponse<ContactResponse> webResponse = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<ContactResponse>>() {
                    });
                    Assertions.assertNotNull(webResponse.getErrors());
                });
    }

    @Test
    public void testGetContactSuccess() throws Exception {

        User user = new User();
        user.setUsername("zam");
        user.setPassword(BCrypt.hashpw("zam", BCrypt.gensalt()));
        user.setToken("token");
        user.setTokenExpired(System.currentTimeMillis() + 10000000L);
        user.setName("zam");
        userRepository.save(user);

        Contacts contacts = new Contacts();
        contacts.setId(UUID.randomUUID().toString());
        contacts.setUser(user);
        contacts.setId(UUID.randomUUID().toString());
        contacts.setEmail("mohammadtajutzamzami07@gmail.com");
        contacts.setFirstName("zam");
        contactRepository.save(contacts);


        mockMvc.perform(
                get("/api/contacts/"+contacts.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "token")
        ).andExpectAll(status().isOk())
                .andDo(result -> {
                    WebResponse<ContactResponse> webResponse = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<ContactResponse>>() {
                    });
                    Assertions.assertNull(webResponse.getErrors());
                    Assertions.assertEquals(contacts.getId() , webResponse.getData().getId());
                });
    }


    @Test
    public void testGetContactNotFound() throws Exception{
        User user = new User();
        user.setUsername("zam");
        user.setPassword(BCrypt.hashpw("zam", BCrypt.gensalt()));
        user.setToken("token");
        user.setTokenExpired(System.currentTimeMillis() + 10000000L);
        user.setName("zam");
        userRepository.save(user);

        Contacts contacts = new Contacts();
        contacts.setId(UUID.randomUUID().toString());
        contacts.setUser(user);
        contacts.setId(UUID.randomUUID().toString());
        contacts.setEmail("mohammadtajutzamzami07@gmail.com");
        contacts.setFirstName("zam");
        contactRepository.save(contacts);


        mockMvc.perform(
                        get("/api/contacts/notfound")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-API-TOKEN", "token")
                ).andExpectAll(status().isBadRequest())
                .andDo(result -> {
                    WebResponse<ContactResponse> webResponse = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<ContactResponse>>() {
                    });
                    Assertions.assertNotNull(webResponse.getErrors());

                });
    }

    @Test
    public void testGetContactUserNotFound() throws Exception{
        User user = new User();
        user.setUsername("zam");
        user.setPassword(BCrypt.hashpw("zam", BCrypt.gensalt()));
        user.setToken("token");
        user.setTokenExpired(System.currentTimeMillis() + 10000000L);
        user.setName("zam");
        userRepository.save(user);

        Contacts contacts = new Contacts();
        contacts.setId(UUID.randomUUID().toString());
        contacts.setUser(user);
        contacts.setId(UUID.randomUUID().toString());
        contacts.setEmail("mohammadtajutzamzami07@gmail.com");
        contacts.setFirstName("zam");
        contactRepository.save(contacts);


        mockMvc.perform(
                        get("/api/contacts/"+contacts.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-API-TOKEN", "salah token")
                ).andExpectAll(status().isUnauthorized())
                .andDo(result -> {
                    WebResponse<ContactResponse> webResponse = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<ContactResponse>>() {
                    });
                    Assertions.assertNotNull(webResponse.getErrors());
                });
    }


    @Test
    public void testUpdateContactSuccess() throws Exception {
        User user = new User();
        user.setUsername("zam");
        user.setPassword(BCrypt.hashpw("zam", BCrypt.gensalt()));
        user.setToken("token");
        user.setTokenExpired(System.currentTimeMillis() + 10000000L);
        user.setName("zam");
        userRepository.save(user);

        Contacts contacts = new Contacts();
        contacts.setId(UUID.randomUUID().toString());
        contacts.setUser(user);
        contacts.setId(UUID.randomUUID().toString());
        contacts.setEmail("mohammadtajutzamzami07@gmail.com");
        contacts.setFirstName("zam");
        contactRepository.save(contacts);


        UpdateContactRequest request = new UpdateContactRequest();
        request.setId(contacts.getId());
        request.setEmail("email@gmail.com");
        request.setFirstName("firstname contact");

        mockMvc.perform(
                put("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN" , "token")

        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<ContactResponse>>() {
            });
            Assertions.assertNotNull(response.getData());
        });
    }


    @Test
    public void testUpdateBadRequestValidation() throws Exception{
        User user = new User();
        user.setUsername("zam");
        user.setPassword(BCrypt.hashpw("zam", BCrypt.gensalt()));
        user.setToken("token");
        user.setTokenExpired(System.currentTimeMillis() + 10000000L);
        user.setName("zam");
        userRepository.save(user);

        Contacts contacts = new Contacts();
        contacts.setId(UUID.randomUUID().toString());
        contacts.setUser(user);
        contacts.setId(UUID.randomUUID().toString());
        contacts.setEmail("mohammadtajutzamzami07@gmail.com");
        contacts.setFirstName("zam");
        contactRepository.save(contacts);


        UpdateContactRequest request = new UpdateContactRequest();
        request.setEmail("email@gmail.com");

        mockMvc.perform(
                put("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN" , "token")

        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<ContactResponse>>() {
            });
            Assertions.assertNotNull(response.getErrors());
        });
    }


    @Test
    public void testUpdateUnauthorized() throws Exception{
        User user = new User();
        user.setUsername("zam");
        user.setPassword(BCrypt.hashpw("zam", BCrypt.gensalt()));
        user.setToken("token");
        user.setTokenExpired(System.currentTimeMillis() + 10000000L);
        user.setName("zam");
        userRepository.save(user);

        Contacts contacts = new Contacts();
        contacts.setId(UUID.randomUUID().toString());
        contacts.setUser(user);
        contacts.setId(UUID.randomUUID().toString());
        contacts.setEmail("mohammadtajutzamzami07@gmail.com");
        contacts.setFirstName("zam");
        contactRepository.save(contacts);


        UpdateContactRequest request = new UpdateContactRequest();
        request.setEmail("email@gmail.com");

        mockMvc.perform(
                put("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN" , "salah token")

        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<ContactResponse>>() {
            });
            Assertions.assertNotNull(response.getErrors());
        });
    }


    @Test
    public void testRemoveSuccess() throws Exception {
        User user = new User();
        user.setUsername("zam");
        user.setPassword(BCrypt.hashpw("zam", BCrypt.gensalt()));
        user.setToken("token");
        user.setTokenExpired(System.currentTimeMillis() + 10000000L);
        user.setName("zam");
        userRepository.save(user);

        Contacts contacts = new Contacts();
        contacts.setId(UUID.randomUUID().toString());
        contacts.setUser(user);
        contacts.setId(UUID.randomUUID().toString());
        contacts.setEmail("mohammadtajutzamzami07@gmail.com");
        contacts.setFirstName("zam");
        contactRepository.save(contacts);

        mockMvc.perform(
                delete("/api/contacts/"+contacts.getId())
                        .header("X-API-TOKEN" , user.getToken())
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> webResponse = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
            });
            Assertions.assertEquals("OK" , webResponse.getData());
        });
    }

    @Test
    public void testRemoveFailed() throws Exception {
        User user = new User();
        user.setUsername("zam");
        user.setPassword(BCrypt.hashpw("zam", BCrypt.gensalt()));
        user.setToken("token");
        user.setTokenExpired(System.currentTimeMillis() + 10000000L);
        user.setName("zam");
        userRepository.save(user);

        Contacts contacts = new Contacts();
        contacts.setId(UUID.randomUUID().toString());
        contacts.setUser(user);
        contacts.setId(UUID.randomUUID().toString());
        contacts.setEmail("mohammadtajutzamzami07@gmail.com");
        contacts.setFirstName("zam");
        contactRepository.save(contacts);

        mockMvc.perform(
                delete("/api/contacts/12")
                        .header("X-API-TOKEN" , user.getToken())
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String> webResponse = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {
            });
            Assertions.assertNotNull(webResponse.getErrors());
        });
    }

    @Test
    public void testSearchContact() throws Exception{
        User user = userRepository.findById("test").orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));

        for (int i = 0; i < 100  ; i++) {

            Contacts contacts = new Contacts();
            contacts.setUser(user);
            contacts.setId(UUID.randomUUID().toString());
            contacts.setPhone(String.valueOf(i));
            contacts.setEmail("example@gmail.com");
            contacts.setFirstName("zam" + i);
            contacts.setLastName("zami" + i);
            contactRepository.save(contacts);
        }

        mockMvc.perform(
                get("/api/contacts")
                        .header("X-API-TOKEN" , user.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("name" , "zam")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            Assertions.assertNull(response.getErrors());
            Assertions.assertNotNull(response.getData());
            Assertions.assertEquals(0 , response.getPaging().getCurrentPage());
            Assertions.assertEquals(10,response.getPaging().getSize());
        });

        mockMvc.perform(
                get("/api/contacts")
                        .header("X-API-TOKEN" , user.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("name" , "zam10")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            Assertions.assertNull(response.getErrors());
            Assertions.assertNotNull(response.getData());
            Assertions.assertEquals(0 , response.getPaging().getCurrentPage());
            Assertions.assertEquals(1,response.getPaging().getSize());
        });
    }

}
