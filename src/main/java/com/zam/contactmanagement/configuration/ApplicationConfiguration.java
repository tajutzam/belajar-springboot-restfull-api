package com.zam.contactmanagement.configuration;


import com.zam.contactmanagement.services.AddressService;
import com.zam.contactmanagement.services.AuthService;
import com.zam.contactmanagement.services.ContactService;
import com.zam.contactmanagement.services.UserService;
import com.zam.contactmanagement.services.impl.AddressServiceImpl;
import com.zam.contactmanagement.services.impl.AuthServiceImpl;
import com.zam.contactmanagement.services.impl.ContactServiceImpl;
import com.zam.contactmanagement.services.impl.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {


    @Bean
    public UserService userService(){
        return new UserServiceImpl();
    }


    @Bean
    public AuthService authService(){
        return new AuthServiceImpl();
    }


    @Bean
    public ContactService contactService(){
        return new ContactServiceImpl();
    }

    @Bean
    public AddressService addressService(){
        return new AddressServiceImpl();
    };

}
