package com.zam.contactmanagement.services;

import com.zam.contactmanagement.entity.User;
import com.zam.contactmanagement.model.CreateContactRequest;
import com.zam.contactmanagement.model.ContactResponse;
import com.zam.contactmanagement.model.SearchContactRequest;
import com.zam.contactmanagement.model.UpdateContactRequest;
import org.springframework.data.domain.Page;

public interface ContactService {


    public ContactResponse create(User user , CreateContactRequest request);

    ContactResponse get(User user , String id);

    ContactResponse update(User user , UpdateContactRequest request);

    void remove(User user , String id);

    Page<ContactResponse> search(User user , SearchContactRequest contactRequest);


}
