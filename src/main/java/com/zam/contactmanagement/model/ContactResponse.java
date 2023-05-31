package com.zam.contactmanagement.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Builder
@Data
@AllArgsConstructor
public class ContactResponse {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;

}
