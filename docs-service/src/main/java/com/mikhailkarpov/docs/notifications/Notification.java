package com.mikhailkarpov.docs.notifications;

public record Notification<T>(String userId, T payload) {

}
