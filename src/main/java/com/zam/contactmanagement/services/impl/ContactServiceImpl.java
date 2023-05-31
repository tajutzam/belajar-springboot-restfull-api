package com.zam.contactmanagement.services.impl;

import com.zam.contactmanagement.entity.Contacts;
import com.zam.contactmanagement.entity.User;
import com.zam.contactmanagement.model.CreateContactRequest;
import com.zam.contactmanagement.model.ContactResponse;
import com.zam.contactmanagement.model.SearchContactRequest;
import com.zam.contactmanagement.model.UpdateContactRequest;
import com.zam.contactmanagement.repository.ContactRepository;
import com.zam.contactmanagement.services.ContactService;

import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Service
public class ContactServiceImpl implements ContactService {


    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private ValidationService validationService;

    @Override
    @Transactional
    public ContactResponse create(User user, CreateContactRequest request) {

        validationService.validate(request);


        Contacts contacts = new Contacts();
        contacts.setId(UUID.randomUUID().toString());
        contacts.setEmail(request.getEmail());
        contacts.setPhone(request.getPhone());
        contacts.setFirstName(request.getFirstName());
        contacts.setUser(user);

        contactRepository.save(contacts);


        return ContactResponse.builder().
                id(contacts.getId())
                .email(contacts.getEmail())
                .phone(contacts.getPhone())
                .firstName(contacts.getFirstName())
                .lastName(contacts.getLastName()).
                build();
    }

    @Override
    @Transactional
    public ContactResponse get(User user, String id) {
        Contacts contacts = contactRepository.findFirstByUserAndId(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "user contact not found"));

        return ContactResponse.builder()
                .email(contacts.getEmail())
                .phone(contacts.getPhone())
                .lastName(contacts.getLastName())
                .firstName(contacts.getFirstName())
                .id(contacts.getId()).
                build();
    }

    @Override
    public ContactResponse update(User user, UpdateContactRequest request) {

        validationService.validate(request);

        Contacts contacts = contactRepository.findFirstByUserAndId(user, request.getId()).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST, "user contact not found"));

        contacts.setFirstName(request.getFirstName());
        contacts.setEmail(request.getEmail());
        contacts.setPhone(request.getPhone());
        contacts.setLastName(request.getLastName());
        contacts.setId(request.getId());
        contacts.setUser(user);

        contactRepository.save(contacts);
        
        return ContactResponse.builder().id(contacts.getId())
                .firstName(contacts.getFirstName())
                .lastName(contacts.getLastName())
                .phone(contacts.getPhone())
                .email(contacts.getEmail())
                .build();
    }


    @Override
    public void remove(User user, String id) {
        Contacts contacts = contactRepository.findFirstByUserAndId(user, id).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST, "user contact not found"));
        contactRepository.delete(contacts);
    }

    @Override
    @Transactional(readOnly = true)
    public PageImpl search(User user, SearchContactRequest request) {
        Specification<Contacts> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>(); //predicate is used to add several possibilities
            predicates.add(builder.equal(root.get("user"),user));
            if(Objects.nonNull(request.getName())){
                predicates.add(
                       builder.or(
                               builder.like(root.get("firstName") , "%" + request.getName() +"%"),
                               builder.like(root.get("lastName") , "%" + request.getName() +"%")
                               )
                );
            }
            if(Objects.nonNull(request.getEmail())){
                predicates.add(builder.like(root.get("email"), "%"+request.getEmail()+"%"));
            }
            if(Objects.nonNull(request.getPhone())){
                predicates.add(builder.like(root.get("email"), "%"+request.getPhone()+"%"));
            }
            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };
        Pageable pageable = PageRequest.of(request.getPage() , request.getTotal());
        Page<Contacts> contacts = contactRepository.findAll(specification, pageable);
        List<ContactResponse> contactResponses = contacts.getContent().stream().map(contact -> toContactResponse(contact)).toList();
        return new PageImpl<>(contactResponses,pageable,contacts.getTotalElements());
    }


    private ContactResponse toContactResponse(Contacts contact) {
        return ContactResponse.builder()
                .id(contact.getId())
                .firstName(contact.getFirstName())
                .lastName(contact.getLastName())
                .email(contact.getEmail())
                .phone(contact.getPhone())
                .build();
    }

}
