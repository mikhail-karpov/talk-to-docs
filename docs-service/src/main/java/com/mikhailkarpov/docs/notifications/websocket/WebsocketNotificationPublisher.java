package com.mikhailkarpov.docs.notifications.websocket;

import com.mikhailkarpov.docs.notifications.Notification;
import com.mikhailkarpov.docs.notifications.NotificationPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;

public abstract class WebsocketNotificationPublisher<T> implements NotificationPublisher<T> {

  private final SimpMessagingTemplate messagingTemplate;

  public WebsocketNotificationPublisher(SimpMessagingTemplate messagingTemplate) {
    this.messagingTemplate = messagingTemplate;
  }

  @Async("applicationTaskExecutor")
  @Override
  public void publish(Notification<T> notification) {
    String userId = notification.userId();
    T payload = notification.payload();
    messagingTemplate.convertAndSendToUser(userId, destination(), payload);
  }

  protected abstract String destination();
}
