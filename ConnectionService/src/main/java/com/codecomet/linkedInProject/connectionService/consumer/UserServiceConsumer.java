package com.codecomet.linkedInProject.connectionService.consumer;

import com.codecomet.linkedInProject.connectionService.service.PersonService;
import com.codecomet.linkedInProject.userService.event.UserCreated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceConsumer {

    private final PersonService personService;

    @KafkaListener(topics = "user_created_topic")
    public void handlePersonCreated(UserCreated userCreated){
        log.info("handlePersonCreated: {}", userCreated);
        personService.createPerson(userCreated.getUserId(),userCreated.getName());
    }
}
