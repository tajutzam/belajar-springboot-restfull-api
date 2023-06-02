package com.zam.contactmanagement.services.impl;

import com.zam.contactmanagement.entity.Addresses;
import com.zam.contactmanagement.entity.Contacts;
import com.zam.contactmanagement.entity.User;
import com.zam.contactmanagement.model.AddressResponse;
import com.zam.contactmanagement.model.CreateAddressRequest;
import com.zam.contactmanagement.model.UpdateAddressRequest;
import com.zam.contactmanagement.model.UpdateContactRequest;
import com.zam.contactmanagement.repository.AddressRepository;
import com.zam.contactmanagement.repository.ContactRepository;
import com.zam.contactmanagement.services.AddressService;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
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
    public AddressResponse create(User user, CreateAddressRequest request) {
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
        Contacts contacts = contactRepository.findFirstByUserAndId(user, contactId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "contact not found"));

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

    @Override
    @Transactional
    public AddressResponse update(User user, UpdateAddressRequest request) {

        validationService.validate(request);

        Contacts contacts = contactRepository.findById(request.getContactId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "contact not found"));

        Addresses addresses = addressRepository.findFirstByContactsAndId(contacts, request.getAddressId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "addresses not found"));

        addresses.setId(request.getAddressId());
        addresses.setCountry(request.getCountry());
        addresses.setStreet(request.getStreet());
        addresses.setProvince(request.getProvince());
        addresses.setPostalCode(request.getPostalCode());
        addressRepository.save(addresses);

        return AddressResponse.builder().
                id(addresses.getId())
                .city(addresses.getCity())
                .postalCode(addresses.getPostalCode())
                .country(addresses.getCountry())
                .province(addresses.getProvince())
                .city(addresses.getCity()).
                build();

    }

    @Override
    public void remove(String contactsId, String addressId) {
        Contacts contacts = contactRepository.findById(contactsId).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Contacts not found"));

        Addresses addresses = addressRepository.
                findFirstByContactsAndId(contacts, addressId).orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.BAD_REQUEST, "address not found"));
        addressRepository.delete(addresses);
    }


    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> listAddress(String contactId) {

        Contacts contacts = contactRepository.findById(contactId).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST , "contacts not found"));

        List<Addresses> addressesList = addressRepository.findAllByContacts(contacts);

        return addressesList.stream().map(this::addressToAddressResponse).toList();
    }


    private AddressResponse addressToAddressResponse(Addresses addresses){
        return AddressResponse.builder().
                street(addresses.getStreet())
                .id(addresses.getId())
                .postalCode(addresses.getPostalCode())
                .country(addresses.getCountry())
                .city(addresses.getCity()).
                build();
    }
}
