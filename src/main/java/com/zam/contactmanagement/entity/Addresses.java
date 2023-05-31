package com.zam.contactmanagement.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "addresses")
public class Addresses {

    @Id
    private String id;

    private String street;
    private String city;
    private String province;
    private String country;
    @Column(name = "postal_code")
    private String postalCode;

    @ManyToOne
    @JoinColumn(name = "contact_id" , referencedColumnName = "id")
    private Contacts contacts;

}
