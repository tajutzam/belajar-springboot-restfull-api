package com.zam.contactmanagement.model;


import lombok.Data;

@Data
public class TokenResponse {

    private String token;
    private Long expired_at;

}
