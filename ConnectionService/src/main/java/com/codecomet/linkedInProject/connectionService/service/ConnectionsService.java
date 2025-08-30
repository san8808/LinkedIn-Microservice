package com.codecomet.linkedInProject.connectionService.service;

import com.codecomet.linkedInProject.connectionService.auth.AuthContextHolder;
import com.codecomet.linkedInProject.connectionService.entity.Person;
import com.codecomet.linkedInProject.connectionService.event.ConnectionAccepted;
import com.codecomet.linkedInProject.connectionService.event.ConnectionRequested;
import com.codecomet.linkedInProject.connectionService.exception.BadRequestException;
import com.codecomet.linkedInProject.connectionService.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConnectionsService {

    private  final PersonRepository personRepository;
    private final KafkaTemplate<Long, ConnectionAccepted> connectionAcceptedKafkaTemplate;
    private final KafkaTemplate<Long, ConnectionRequested> connectionRequestedKafkaTemplate;

    public List<Person> getFirstDegreeConnectionsOfUser(Long userId){
        log.info("Getting first degree connections of user with id: {}", userId);

        return personRepository.getFirstDegreeConnections(userId);
    }

    public void sendConnectionRequest(Long receiverId){
        Long senderId = AuthContextHolder.getCurrentUserId();


        log.info("sending connection request with senderId: {}, receiverId: {}", senderId, receiverId);

        if (senderId.equals(receiverId)) {
            throw new BadRequestException("Both sender and receiver are the same");
        }

        boolean alreadySentRequest = personRepository.connectionRequestExists(senderId, receiverId);
        if (alreadySentRequest) {
            throw new BadRequestException("Connection request already exists, cannot send again");
        }

        boolean alreadyConnected = personRepository.alreadyConnected(senderId, receiverId);
        if (alreadyConnected) {
            throw new BadRequestException("Already connected users, cannot add connection request");
        }

        personRepository.addConnectionRequest(senderId, receiverId);

        Optional<Person> sender = personRepository.findByUserId(senderId);

        ConnectionRequested connectionRequested = ConnectionRequested.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .senderName(sender.map(Person::getName).orElse(null))
                .build();

        connectionRequestedKafkaTemplate.send("connection_requested_topic", connectionRequested);
        log.info("Successfully sent the connection request");
    }

    public void acceptConnectionRequest(Long senderId){
        Long receiverId = AuthContextHolder.getCurrentUserId();
        log.info("accepting connection request with senderId: {}, receiverId: {}", senderId, receiverId);

        if (senderId.equals(receiverId)) {
            throw new BadRequestException("Both sender and receiver are the same");
        }

        boolean alreadyConnected = personRepository.alreadyConnected(senderId, receiverId);
        if (alreadyConnected) {
            throw new BadRequestException("Already connected users, cannot accept connection request again");
        }

        boolean alreadySentRequest = personRepository.connectionRequestExists(senderId, receiverId);
        if (!alreadySentRequest) {
            throw new BadRequestException("No Connection request exists, cannot accept without request");
        }

        personRepository.acceptConnectionRequest(senderId,receiverId);

        Optional<Person> receiver = personRepository.findByUserId(receiverId);

        ConnectionAccepted connectionAcceptedEvent = ConnectionAccepted.builder()
                .receiverId(receiverId)
                .senderId(senderId)
                .receiverName(receiver.map(Person::getName).orElse(null))
                .build();

        connectionAcceptedKafkaTemplate.send("connection_accepted_topic",connectionAcceptedEvent);

        log.info("Successfully accepted the connection request with senderId: {}, receiverId: {}",senderId,receiverId);

    }

    public void rejectConnectionRequest(Long senderId) {
        Long receiverId = AuthContextHolder.getCurrentUserId();
        log.info("Rejecting a connection request with senderId: {}, receiverId: {}", senderId, receiverId);

        if (senderId.equals(receiverId)) {
            throw new BadRequestException("Both sender and receiver are the same");
        }

        boolean alreadySentRequest = personRepository.connectionRequestExists(senderId, receiverId);
        if (!alreadySentRequest) {
            throw new BadRequestException("No Connection request exists, cannot reject it");
        }

        personRepository.rejectConnectionRequest(senderId, receiverId);

        log.info("Successfully rejected the connection request with senderId: {}, receiverId: {}", senderId,
                receiverId);
    }

}
