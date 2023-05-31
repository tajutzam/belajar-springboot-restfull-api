package com.zam.contactmanagement.repository;

import com.zam.contactmanagement.entity.Contacts;
import com.zam.contactmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contacts , String> , JpaSpecificationExecutor<Contacts> {
    Optional<Contacts> findFirstByUserAndId(User user, String id);
}
