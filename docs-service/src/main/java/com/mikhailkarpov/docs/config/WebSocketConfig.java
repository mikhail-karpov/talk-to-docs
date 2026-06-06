package com.mikhailkarpov.docs.config;

import com.mikhailkarpov.docs.auth.User;
import com.mikhailkarpov.docs.chat.web.MessageResponse;
import com.mikhailkarpov.docs.documents.DocumentMetadata;
import com.mikhailkarpov.docs.notifications.websocket.WebsocketNotificationPublisher;
import java.security.Principal;
import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Value("${app.frontend.url}")
  private String frontendUrl;

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker("/queue");
    registry.setUserDestinationPrefix("/user");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws")
        .setAllowedOrigins(frontendUrl)
        .setHandshakeHandler(new UserIdHandshakeHandler())
        .withSockJS();
  }

  @Bean
  WebsocketNotificationPublisher<DocumentMetadata> documentNotificationPublisher(
      SimpMessagingTemplate messagingTemplate) {

    return new WebsocketNotificationPublisher<>(messagingTemplate) {
      @Override
      protected String destination() {
        return "/queue/documents";
      }
    };
  }

  @Bean
  WebsocketNotificationPublisher<MessageResponse> messageNotificationPublisher(
      SimpMessagingTemplate messagingTemplate) {

    return new WebsocketNotificationPublisher<>(messagingTemplate) {
      @Override
      protected String destination() {
        return "/queue/messages";
      }
    };
  }

  static class UserIdHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(
        ServerHttpRequest request,
        @NonNull WebSocketHandler wsHandler,
        @NonNull Map<String, Object> attributes) {

      var principal = request.getPrincipal();
      if (principal instanceof Authentication auth && auth.getPrincipal() instanceof User user) {
        return user::getId;
      }
      return principal;
    }
  }
}
