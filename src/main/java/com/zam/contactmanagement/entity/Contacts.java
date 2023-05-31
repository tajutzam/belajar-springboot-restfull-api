package com.zam.contactmanagement.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Contacts {

    @Id
    private String id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    private String phone;
    private String email;

    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username") // name field in contacts and user
    private User user;

    @OneToMany(mappedBy = "contacts")
    private List<Addresses> addresses;


}
