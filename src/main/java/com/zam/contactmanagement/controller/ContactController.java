package com.zam.contactmanagement.controller;


import com.zam.contactmanagement.entity.User;
import com.zam.contactmanagement.model.*;
import com.zam.contactmanagement.services.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;
import java.util.List;

@RestController
public class ContactController {
    @Autowired
    private ContactService contactService;

    @PostMapping(
            path = "api/contacts",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ContactResponse> add(User user, @RequestBody CreateContactRequest request){
        ContactResponse contactResponse = contactService.create(user, request);
        return WebResponse.<ContactResponse>builder().data(contactResponse).build();
    }


    @GetMapping(
            path = "api/contacts/{contactId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ContactResponse> get(User user, @PathVariable("contactId") String id){
        ContactResponse contactResponse = contactService.get(user, id);
        return WebResponse.<ContactResponse>builder()
                .data(contactResponse).build();
    }


    @PutMapping(
            path = "api/contacts",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ContactResponse> update(User user , @RequestBody UpdateContactRequest request){
        ContactResponse contactResponse = contactService.update(user, request);
        return WebResponse.<ContactResponse>builder().data(contactResponse).build();
    }


    @DeleteMapping(
            path = "api/contacts/{contactId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> delete(User user , @PathVariable("contactId") String idContact){
        contactService.remove(user , idContact);
        return WebResponse.<String>builder().data("OK").build();
    }


    @GetMapping(
            path = "api/contacts",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<ContactResponse>> search(User user ,
                                                     @RequestParam(value = "name" , required = false) String name ,
                                                     @RequestParam(value = "phone" , required = false) String phone,
                                                     @RequestParam(value = "email" , required = false) String email ,
                                                     @RequestParam(value = "page" , required = false , defaultValue = "0") Integer page ,
                                                     @RequestParam(value = "size" , required = false , defaultValue = "10") Integer size){
        SearchContactRequest request = new SearchContactRequest();
        request.setPhone(phone);
        request.setName(name);
        request.setEmail(email);
        request.setPage(page);
        request.setTotal(size);

        Page<ContactResponse> contactResponses = contactService.search(user, request);
        return WebResponse.<List<ContactResponse>>builder()
                .data(contactResponses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(contactResponses.getNumber())
                        .totalPage(contactResponses.getTotalPages())
                        .size(contactResponses.getContent().size())
                        .build()).build();
    }
}
