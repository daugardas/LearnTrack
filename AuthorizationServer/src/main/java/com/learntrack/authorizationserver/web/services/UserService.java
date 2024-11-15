package com.learntrack.authorizationserver.web.services;

import com.learntrack.authorizationserver.models.User;
import com.learntrack.authorizationserver.models.UserUpdateRequestDTO;
import com.learntrack.authorizationserver.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Iterable<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public User updateUser(Long id, UserUpdateRequestDTO userUpdateDTO) {
        Optional<User> userOptional = findById(id);
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User with id " + id + " not found");
        }

        User user = userOptional.get();
        user.setUsername(userUpdateDTO.getUsername());
        return save(user);
    }
}
