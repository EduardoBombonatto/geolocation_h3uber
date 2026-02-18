package com.eduardo.geolocation_h3uber.config;

import com.uber.h3core.H3Core;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

public class AppConfig {
    @Bean
    public H3Core h3Core() throws IOException {
        return H3Core.newInstance();
    }

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }
}

