package com.zam.contactmanagement.model;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegisterRequest {

    @NotBlank
    @Size(max = 100 , message = "Username cannot be more than 100")
    private String username;
    @NotBlank
    @Size(max = 100 , message = "Password cannot be more than 100")
    private String password;
    @NotBlank
    @Size(max = 100 , message = "Name cannot be more than 100")
    private String name;

}
