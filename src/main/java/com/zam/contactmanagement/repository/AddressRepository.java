package com.zam.contactmanagement.repository;

import com.zam.contactmanagement.entity.Addresses;
import com.zam.contactmanagement.entity.Contacts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface AddressRepository extends JpaRepository<Addresses , String> {

    Optional<Addresses> findFirstByContactsAndId(Contacts contacts , String id);

}
