package com.mikhailkarpov.docs.auth.web;

import com.mikhailkarpov.docs.auth.User;
import com.mikhailkarpov.docs.auth.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
class AuthController {

  private final AuthenticationManager authenticationManager;
  private final UserService userService;
  private final SecurityContextRepository securityContextRepository;

  public AuthController(
      AuthenticationManager authenticationManager,
      UserService userService,
      SecurityContextRepository securityContextRepository) {

    this.authenticationManager = authenticationManager;
    this.userService = userService;
    this.securityContextRepository = securityContextRepository;
  }

  @PostMapping("/login")
  AuthResponse login(
      @Valid @RequestBody AuthRequest authRequest,
      HttpServletRequest request,
      HttpServletResponse response) {

    var username = authRequest.email();
    var password = authRequest.password();
    var token = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
    var authentication = authenticationManager.authenticate(token);

    var contextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    var context = contextHolderStrategy.createEmptyContext();
    context.setAuthentication(authentication);
    contextHolderStrategy.setContext(context);
    securityContextRepository.saveContext(context, request, response);

    var user = userService.loadUserByUsername(username);
    return AuthResponse.from(user);
  }

  @GetMapping("/me")
  AuthResponse me(@AuthenticationPrincipal User user) {

    return AuthResponse.from(user);
  }

}
