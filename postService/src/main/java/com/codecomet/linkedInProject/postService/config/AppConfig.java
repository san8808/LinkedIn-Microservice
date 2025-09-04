package com.codecomet.linkedInProject.postService.config;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper getModelMapper(){
        return new ModelMapper();
    }

    @Bean
    public Encoder feignFormEncoder(){
        return new SpringFormEncoder();
    }
}
