package com.zam.contactmanagement.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zam.contactmanagement.entity.Addresses;
import com.zam.contactmanagement.entity.Contacts;
import com.zam.contactmanagement.entity.User;
import com.zam.contactmanagement.model.AddressResponse;
import com.zam.contactmanagement.model.CreateAddressRequest;
import com.zam.contactmanagement.model.WebResponse;
import com.zam.contactmanagement.repository.AddressRepository;
import com.zam.contactmanagement.repository.ContactRepository;
import com.zam.contactmanagement.repository.UserRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUsername("nano");
        user.setName("nano");
        user.setTokenExpired(System.currentTimeMillis() + 10000000L);
        user.setToken("nano");
        user.setPassword("nano");

        userRepository.save(user);

        Contacts contacts = new Contacts();
        contacts.setId("id-contact");
        contacts.setEmail("mohammadtajutzamzami07@gmail.com");
        contacts.setFirstName("zam");
        contacts.setUser(user);
        contactRepository.save(contacts);

    }

    @Test
    public void testCreateAddressSuccess() throws Exception{
        Contacts contacts = contactRepository.findById("id-contact").orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "contact not found"));
        CreateAddressRequest request = new CreateAddressRequest();
        request.setContactId(contacts.getId());
        request.setCity("banyuwangi");
        request.setProvince("jawa timur");
        request.setCountry("indonesia");

        mockMvc.perform(
                post("/api/contacts/"+contacts.getId()+"/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN" , "nano")

        ).andExpectAll(
                status().isOk()
        ).andDo(
                result -> {
                   WebResponse<AddressResponse>  response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    Assertions.assertNotNull(response);
                }
        );
    }

    @Test
    public void testCreateAddressUnauthorized() throws Exception{
        Contacts contacts = contactRepository.findById("id-contact").orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "contact not found"));
        CreateAddressRequest request = new CreateAddressRequest();
        request.setContactId(contacts.getId());
        request.setCity("banyuwangi");
        request.setProvince("jawa timur");
        request.setCountry("indonesia");

        mockMvc.perform(
                post("/api/contacts/"+contacts.getId()+"/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN" , "noooo")

        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(
                result -> {
                    WebResponse<AddressResponse>  response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    Assertions.assertNull(response.getData());
                }
        );
    }


    @Test
    public void testCreateAddressBadRequest() throws Exception{
        Contacts contacts = contactRepository.findById("id-contact").orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "contact not found"));
        CreateAddressRequest request = new CreateAddressRequest();
        request.setContactId(contacts.getId());
        mockMvc.perform(
                post("/api/contacts/"+contacts.getId()+"/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN" , "nano")

        ).andExpectAll(
                status().isBadRequest()
        ).andDo(
                result -> {
                    WebResponse<AddressResponse>  response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    Assertions.assertNull(response.getData());
                }
        );
    }


    @Test
    public void testGetSuccess() throws Exception {
        User user = userRepository.findById("nano").orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
        Contacts contacts = contactRepository.findById("id-contact").orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
        Addresses addresses = new Addresses();
        addresses.setId("id-address");
        addresses.setContacts(contacts);
        addresses.setCountry("indonesia");
        addressRepository.save(addresses);
        mockMvc.perform(
                get("/api/contacts/"+contacts.getId()+"/addresses/"+addresses.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN" , user.getToken())
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<AddressResponse>>() {
            });
            Assertions.assertNotNull(response.getData());
            Assertions.assertEquals(addresses.getCountry() , response.getData().getCountry());
        });
    }
}
