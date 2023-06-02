package com.zam.contactmanagement.controller;


import com.zam.contactmanagement.entity.User;
import com.zam.contactmanagement.model.*;
import com.zam.contactmanagement.services.AddressService;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AddressController {

    @Autowired
    private AddressService addressService;

    @PostMapping(
            path = "api/contacts/{contactId}/addresses",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AddressResponse> create(
            User user,
            @PathVariable("contactId") String contactId, @RequestBody CreateAddressRequest request) {
        request.setContactId(contactId);
        val addressResponse = addressService.create(user, request);
        return WebResponse.<AddressResponse>builder().data(addressResponse).build();
    }

    @GetMapping(
            path = "api/contacts/{contactsId}/addresses/{addressesId}"
    )
    public WebResponse<AddressResponse> get(
            User user,
            @PathVariable("contactsId") String contactsId, @PathVariable("addressesId") String addressId) {
        AddressResponse addressResponse = addressService.get(user, contactsId, addressId);
        return WebResponse.<AddressResponse>builder().data(addressResponse).build();
    }

    @PutMapping(
            path = "api/contacts/{contactId}/addresses/{addressId}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AddressResponse> update(User user,
                                               @PathVariable("contactId") String contactId
            , @PathVariable("addressId") String addressId, @RequestBody UpdateAddressRequest request) {
        request.setAddressId(addressId);
        request.setContactId(contactId);

        AddressResponse response = addressService.update(user, request);
        return WebResponse.<AddressResponse>builder().data(response).build();
    }

    @DeleteMapping(
            path = "api/contacts/{contactId}/addresses/{addressId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> delete(User user ,  @PathVariable("contactId") String contactId
            , @PathVariable("addressId") String addressId){
        addressService.remove(contactId , addressId);
        return WebResponse.<String>builder().data("OK").build();
    }
    @GetMapping(
            path = "api/contacts/{contactId}/addresses",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<AddressResponse>> listAddresses(User user , @PathVariable("contactId") String contactId){
        List<AddressResponse> responses = addressService.listAddress(contactId);
        return WebResponse.<List<AddressResponse>>builder().data(responses).build();
    }


}
