package com.galvanize.invoicify.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.format.DateTimeFormatter;

@Configuration
public class InvoicifyConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public @Bean
    ObjectMapper getObjectMapper(){
        return JsonMapper.builder()
                .findAndAddModules()
                .build();
    }

    public @Bean
    DateTimeFormatter getDateFormatter(){
        return DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

}
