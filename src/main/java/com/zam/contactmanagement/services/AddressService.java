package com.zam.contactmanagement.services;

import com.zam.contactmanagement.entity.User;
import com.zam.contactmanagement.model.AddressResponse;
import com.zam.contactmanagement.model.CreateAddressRequest;

public interface AddressService {

    AddressResponse create(User user , CreateAddressRequest request);

    AddressResponse get(User user , String contactId , String addressId);

}
