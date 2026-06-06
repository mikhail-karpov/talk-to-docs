package com.mikhailkarpov.docs.chat.web;

import com.mikhailkarpov.docs.auth.User;
import com.mikhailkarpov.docs.chat.AuthorType;
import com.mikhailkarpov.docs.chat.ChatService;
import com.mikhailkarpov.docs.chat.command.CreateConversationCommand;
import com.mikhailkarpov.docs.chat.command.RenameConversationCommand;
import com.mikhailkarpov.docs.chat.command.SendMessageCommand;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

  private final ChatService chatService;

  public ChatController(ChatService chatService) {
    this.chatService = chatService;
  }

  @GetMapping
  public Map<String, List<ConversationResponse>> getConversations(
      @AuthenticationPrincipal User user) {

    var conversations = chatService.getConversations(user.getId())
        .stream()
        .map(ConversationResponse::from)
        .toList();

    return Map.of("items", conversations);
  }

  @PostMapping
  public MessageResponse createConversation(
      @AuthenticationPrincipal User user, @Valid @RequestBody CreateConversationRequest request) {

    var command = new CreateConversationCommand(user.getId(), request.title(), request.content());
    var message = chatService.createConversation(command);
    return MessageResponse.from(message);
  }

  @PutMapping("/{conversationId}")
  public ConversationResponse renameConversation(
      @PathVariable String conversationId,
      @AuthenticationPrincipal User user,
      @Valid @RequestBody RenameConversationRequest request) {

    var command = new RenameConversationCommand(conversationId, user.getId(), request.title());
    var conversation = chatService.renameConversation(command);
    return ConversationResponse.from(conversation);
  }

  @PostMapping("/{conversationId}/messages")
  public MessageResponse sendMessage(
      @PathVariable String conversationId,
      @AuthenticationPrincipal User user,
      @Valid @RequestBody SendMessageRequest request) {

    var command = new SendMessageCommand(conversationId, user.getId(), request.content(), AuthorType.USER);
    var message = chatService.sendMessage(command);
    return MessageResponse.from(message);
  }

  @GetMapping("/{conversationId}/messages")
  public Map<String, List<MessageResponse>> getMessages(
      @PathVariable String conversationId,
      @AuthenticationPrincipal User user) {

    var messages = chatService.getMessages(conversationId, user.getId())
        .stream()
        .map(MessageResponse::from)
        .toList();

    return Map.of("items", messages);
  }
}
