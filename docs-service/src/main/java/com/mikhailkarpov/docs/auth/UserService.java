package com.mikhailkarpov.docs.auth;

import java.util.UUID;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  @NonNull
  public User loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
    return userRepository.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }

  @Transactional
  public void registerUser(RegisterUserCommand command) {
    boolean emailIsTaken = userRepository.existsByEmail(command.email());
    if (emailIsTaken) {
      throw new UserRegisteredException();
    }

    var user = new User(
        UUID.randomUUID().toString(),
        command.email(),
        passwordEncoder.encode(command.password()),
        command.firstName(),
        command.lastName()
    );
    userRepository.add(user);
  }
}
