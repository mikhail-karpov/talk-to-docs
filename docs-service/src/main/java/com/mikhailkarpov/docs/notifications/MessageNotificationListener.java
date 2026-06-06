package com.mikhailkarpov.docs.notifications;

import com.mikhailkarpov.docs.chat.event.MessageCreatedEvent;
import com.mikhailkarpov.docs.chat.web.MessageResponse;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class MessageNotificationListener {

  private final NotificationPublisher<MessageResponse> publisher;

  public MessageNotificationListener(NotificationPublisher<MessageResponse> publisher) {
    this.publisher = publisher;
  }

  @TransactionalEventListener(value = MessageCreatedEvent.class)
  void onMessageCreated(MessageCreatedEvent event) {
    var payload = MessageResponse.from(event.message());
    var notification = new Notification<>(payload.userId(), payload);
    publisher.publish(notification);
  }
}
