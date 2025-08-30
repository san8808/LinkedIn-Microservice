package com.codecomet.linkedInProject.userService.service;

import com.codecomet.linkedInProject.userService.dto.LoginRequestDto;
import com.codecomet.linkedInProject.userService.dto.SignupRequestDto;
import com.codecomet.linkedInProject.userService.dto.UserDto;
import com.codecomet.linkedInProject.userService.entity.User;
import com.codecomet.linkedInProject.userService.event.UserCreated;
import com.codecomet.linkedInProject.userService.exception.BadRequestException;
import com.codecomet.linkedInProject.userService.exception.ResourceNotFoundException;
import com.codecomet.linkedInProject.userService.repository.UserRepository;
import com.codecomet.linkedInProject.userService.util.BCrypt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final JWTService jwtService;
    private final KafkaTemplate<Long, UserCreated> userCreatedKafkaTemplate;

    public UserDto signUp(SignupRequestDto signupRequestDto) {
        log.info("Signup a user with email: {}", signupRequestDto.getEmail());

        boolean exists = userRepository.existsByEmail(signupRequestDto.getEmail());
        if(exists) throw new BadRequestException("User already exists");

        User user = modelMapper.map(signupRequestDto,User.class);
        user.setPassword(BCrypt.hash(signupRequestDto.getPassword()));

        user = userRepository.save(user);

        UserCreated userCreated = UserCreated.builder()
                .userId(user.getId())
                .name(user.getName())
                .build();

        userCreatedKafkaTemplate.send("user_created_topic",userCreated);

        return modelMapper.map(user,UserDto.class);

    }

    public String login(LoginRequestDto loginRequestDto) {
        log.info("Loggin request for the user with email: {}", loginRequestDto.getEmail());

        User user = userRepository.findByEmail(loginRequestDto.getEmail()).orElseThrow(
                () -> new BadRequestException("Incorrect email or password"));

        boolean isPasswordMatch = BCrypt.match(loginRequestDto.getPassword(), user.getPassword());

        if(!isPasswordMatch){
            throw new BadRequestException("Incorrect email or password");
        }

        return jwtService.generateAccessToken(user);
    }
}
