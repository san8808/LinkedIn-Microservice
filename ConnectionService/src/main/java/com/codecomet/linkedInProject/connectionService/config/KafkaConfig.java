package com.codecomet.linkedInProject.connectionService.config;

import feign.Capability;
import feign.micrometer.MicrometerCapability;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic connectionAcceptedTopic(){
        return new NewTopic("connection_accepted_topic",3,(short)1);
    }

    @Bean
    public NewTopic connectionRequestedTopic(){
        return new NewTopic("connection_requested_topic",3,(short)1);
    }

    @Bean
    public Capability capability(final MeterRegistry registry) {
        return new MicrometerCapability(registry);
    }

}
