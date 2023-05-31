package com.zam.contactmanagement.services.impl;

import com.zam.contactmanagement.entity.Addresses;
import com.zam.contactmanagement.entity.Contacts;
import com.zam.contactmanagement.entity.User;
import com.zam.contactmanagement.model.AddressResponse;
import com.zam.contactmanagement.model.CreateAddressRequest;
import com.zam.contactmanagement.repository.AddressRepository;
import com.zam.contactmanagement.repository.ContactRepository;
import com.zam.contactmanagement.services.AddressService;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class AddressServiceImpl implements AddressService {


    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ValidationService validationService;

    @Override
    @Transactional
    public AddressResponse create(User user ,CreateAddressRequest request) {
        validationService.validate(request);
        val contacts = contactRepository.findById(request.getContactId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "contact not found"));

        Addresses addresses = new Addresses();
        addresses.setId(UUID.randomUUID().toString());
        addresses.setContacts(contacts);
        addresses.setCountry(request.getCountry());
        addresses.setProvince(request.getProvince());
        addresses.setStreet(request.getStreet());
        addresses.setPostalCode(request.getPostalCode());
        addresses.setCity(request.getCity());

        addressRepository.save(addresses);

        return AddressResponse.builder().
                id(addresses.getId())
                .city(addresses.getCity())
                .country(addresses.getCountry())
                .province(addresses.getProvince())
                .street(addresses.getStreet())
                .postalCode(addresses.getPostalCode()).
                build();
    }

    @Override
    public AddressResponse get(User user, String contactId, String addressId) {
       Contacts contacts =  contactRepository.findFirstByUserAndId(user , contactId)
               .orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST  , "contact not found"));

       Addresses addresses = addressRepository.findFirstByContactsAndId(contacts, addressId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "address not found"));

        return AddressResponse.builder().
                city(addresses.getCity())
                        .country(addresses.getCountry())
                                .street(addresses.getStreet())
                                        .id(addresses.getId())
                                                .province(addresses.getProvince())
                                                        .postalCode(addresses.getPostalCode()).
                build();
    }
}
