package com.codecomet.linkedInProject.connectionService.service;

import com.codecomet.linkedInProject.connectionService.entity.Person;
import com.codecomet.linkedInProject.connectionService.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    public void  createPerson(Long userId,String name){
        Person person = Person.builder()
                .userId(userId)
                .name(name)
                .build();

        personRepository.save(person);

    }
}
