package com.mj.loyalty.userservice.service;

import com.mj.loyalty.common.events.UserRegisteredEvent;
import com.mj.loyalty.common.service.OutboxService;
import com.mj.loyalty.userservice.dto.CreateUserRequest;
import com.mj.loyalty.userservice.dto.UserResponse;
import com.mj.loyalty.userservice.entity.User;
import com.mj.loyalty.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final OutboxService outboxService;

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        // Check if user already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with email " + request.getEmail() + " already exists");
        }

        // Create new user
        User user = new User();
        user.setUserId(UUID.randomUUID().toString());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());

        User savedUser = userRepository.save(user);

        // Publish UserRegisteredEvent
        UserRegisteredEvent event = new UserRegisteredEvent(
            UUID.randomUUID().toString(),
            savedUser.getUserId(),
            savedUser.getEmail(),
            savedUser.getFirstName(),
            savedUser.getLastName()
        );
        
        outboxService.saveEvent("User", savedUser.getUserId(), "UserRegistered", event);

        log.info("User created with ID: {}", savedUser.getUserId());
        
        return mapToResponse(savedUser);
    }

    public UserResponse getUserById(String userId) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        return mapToResponse(user);
    }

    private UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setIsActive(user.getIsActive());
        return response;
    }
}