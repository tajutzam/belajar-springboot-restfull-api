package com.zam.contactmanagement.controller;


import com.zam.contactmanagement.entity.User;
import com.zam.contactmanagement.model.AddressResponse;
import com.zam.contactmanagement.model.ContactResponse;
import com.zam.contactmanagement.model.CreateAddressRequest;
import com.zam.contactmanagement.model.WebResponse;
import com.zam.contactmanagement.services.AddressService;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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
            @PathVariable("contactId") String contactId , @RequestBody CreateAddressRequest request){
        request.setContactId(contactId);
        val addressResponse = addressService.create(user, request);
        return WebResponse.<AddressResponse>builder().data(addressResponse).build();
    }

    @GetMapping(
            path = "api/contacts/{contactsId}/addresses/{addressesId}"
    )
    public WebResponse<AddressResponse> get(
            User user,
            @PathVariable("contactsId") String contactsId ,@PathVariable("addressesId") String addressId ){
        AddressResponse addressResponse = addressService.get(user, contactsId, addressId);
        return WebResponse.<AddressResponse>builder().data(addressResponse).build();
    }

}
