package com.mikhailkarpov.docs.notifications;

public interface NotificationPublisher<T> {

  void publish(Notification<T> notification);
}
