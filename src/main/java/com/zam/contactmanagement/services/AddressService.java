package com.zam.contactmanagement.services;

import com.zam.contactmanagement.entity.Contacts;
import com.zam.contactmanagement.entity.User;
import com.zam.contactmanagement.model.AddressResponse;
import com.zam.contactmanagement.model.CreateAddressRequest;
import com.zam.contactmanagement.model.UpdateAddressRequest;
import com.zam.contactmanagement.model.UpdateContactRequest;

import java.util.List;

public interface AddressService {

    AddressResponse create(User user , CreateAddressRequest request);

    AddressResponse get(User user , String contactId , String addressId);

    AddressResponse update(User user , UpdateAddressRequest request);

    void remove(String contactsId, String addressId);

    List<AddressResponse> listAddress(String contactId);

}
