package com.mikhailkarpov.docs.chat;

import com.mikhailkarpov.docs.TestcontainersConfig;
import com.mikhailkarpov.docs.chat.command.CreateConversationCommand;
import com.mikhailkarpov.docs.chat.command.RenameConversationCommand;
import com.mikhailkarpov.docs.chat.command.SendMessageCommand;
import com.mikhailkarpov.docs.chat.event.ConversationCreatedEvent;
import com.mikhailkarpov.docs.chat.event.MessageCreatedEvent;
import com.mikhailkarpov.docs.chat.jdbc.ChatJdbcRepository;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

@DataJdbcTest
@Import(TestcontainersConfig.class)
@RecordApplicationEvents
class ChatServiceTest {

  // Seeded by db/seed/V3__insert_test_users.sql; required by the conversations.user_id FK.
  private static final String USER_ID = "2686f7a3-bd4a-4938-93a7-fe8e9360eb28";
  private static final String OTHER_USER_ID = UUID.randomUUID().toString();

  @Autowired
  private JdbcClient jdbcClient;

  @Autowired
  private ApplicationEventPublisher eventPublisher;

  @Autowired
  private ApplicationEvents events;

  private ChatJdbcRepository chatRepository;
  private ChatService chatService;

  @BeforeEach
  void setUp() {
    this.events.clear();
    this.chatRepository = new ChatJdbcRepository(jdbcClient);
    this.chatService = new ChatService(chatRepository, eventPublisher);
  }

  @Nested
  class CreateConversation {

    @Test
    void persistsConversationWithFirstMessageAndPublishesEvent() {
      var command = new CreateConversationCommand(USER_ID, "My chat", "Hello there");

      var message = chatService.createConversation(command);

      Assertions.assertThat(message)
          .returns(USER_ID, ChatMessage::getUserId)
          .returns(AuthorType.USER, ChatMessage::getAuthorType)
          .returns("Hello there", ChatMessage::getContent);

      Assertions.assertThat(chatRepository.findConversation(USER_ID, message.getConversationId()))
          .isPresent();

      Assertions.assertThat(chatRepository.findMessages(message.getConversationId()))
          .singleElement()
          .returns(USER_ID, ChatMessage::getUserId)
          .returns(AuthorType.USER, ChatMessage::getAuthorType)
          .returns("Hello there", ChatMessage::getContent);

      Assertions.assertThat(events.stream(MessageCreatedEvent.class))
          .singleElement()
          .returns("Hello there", e -> e.message().getContent());
    }

    @Test
    void publishesConversationCreatedEventCarryingConversationAndFirstMessage() {
      var message = chatService.createConversation(
          new CreateConversationCommand(USER_ID, null, "Hello there"));

      Assertions.assertThat(events.stream(ConversationCreatedEvent.class))
          .singleElement()
          .satisfies(e -> {
            Assertions.assertThat(e.conversation().id()).isEqualTo(message.getConversationId());
            Assertions.assertThat(e.conversation().userId()).isEqualTo(USER_ID);
            Assertions.assertThat(e.conversation().title()).isEqualTo("Untitled");
            Assertions.assertThat(e.firstMessage().getContent()).isEqualTo("Hello there");
          });
    }

    @Test
    void defaultsTitleWhenBlank() {
      var message = chatService.createConversation(
          new CreateConversationCommand(USER_ID, "  ", "Hi"));

      Assertions.assertThat(chatRepository.findConversation(USER_ID, message.getConversationId()))
          .isPresent().get()
          .returns("Untitled", Conversation::title);
    }

    @Test
    void defaultsTitleWhenNull() {
      var message = chatService.createConversation(
          new CreateConversationCommand(USER_ID, null, "Hi"));

      Assertions.assertThat(chatRepository.findConversation(USER_ID, message.getConversationId()))
          .isPresent().get()
          .returns("Untitled", Conversation::title);
    }
  }

  @Nested
  class RenameConversation {

    @Test
    void persistsNewTitle() {
      var message = chatService.createConversation(
          new CreateConversationCommand(USER_ID, null, "hi"));

      chatService.renameConversation(
          new RenameConversationCommand(message.getConversationId(), USER_ID, "Generated title"));

      Assertions.assertThat(chatRepository.findConversation(USER_ID, message.getConversationId()))
          .isPresent().get()
          .returns("Generated title", Conversation::title);
    }

    @Test
    void doesNotRenameConversationOfAnotherUser() {
      var message = chatService.createConversation(
          new CreateConversationCommand(USER_ID, null, "hi"));

      chatService.renameConversation(
          new RenameConversationCommand(message.getConversationId(), OTHER_USER_ID, "Hacked"));

      Assertions.assertThat(chatRepository.findConversation(USER_ID, message.getConversationId()))
          .isPresent().get()
          .returns("Untitled", Conversation::title);
    }
  }

  @Nested
  class GetConversations {

    @Test
    void returnsConversationsForUserNewestFirst() {
      var first = chatService.createConversation(
          new CreateConversationCommand(USER_ID, "First", "1"));
      var second = chatService.createConversation(
          new CreateConversationCommand(USER_ID, "Second", "2"));

      Assertions.assertThat(chatService.getConversations(USER_ID))
          .extracting(Conversation::id)
          .contains(first.getConversationId(), second.getConversationId());
    }

    @Test
    void returnsEmptyListWhenUserHasNoConversations() {
      Assertions.assertThat(chatService.getConversations(OTHER_USER_ID))
          .isEmpty();
    }
  }

  @Nested
  class GetMessages {

    @Test
    void returnsMessagesInChronologicalOrder() {
      var firstMessage = chatService.createConversation(
          new CreateConversationCommand(USER_ID, "Chat", "first"));
      chatService.sendMessage(
          new SendMessageCommand(firstMessage.getConversationId(), USER_ID, "second", AuthorType.AI));

      Assertions.assertThat(chatService.getMessages(firstMessage.getConversationId(), USER_ID))
          .extracting(ChatMessage::getContent)
          .containsExactly("first", "second");
    }

    @Test
    void throwsWhenConversationMissing() {
      var missingId = UUID.randomUUID().toString();

      Assertions.assertThatThrownBy(() -> chatService.getMessages(missingId, USER_ID))
          .isInstanceOf(ConversationNotFound.class);
    }

    @Test
    void throwsWhenConversationBelongsToAnotherUser() {
      var message = chatService.createConversation(
          new CreateConversationCommand(USER_ID, "Chat", "hi"));

      Assertions.assertThatThrownBy(() -> chatService.getMessages(message.getConversationId(), OTHER_USER_ID))
          .isInstanceOf(ConversationNotFound.class);
    }
  }

  @Nested
  class SendMessage {

    @Test
    void persistsMessageAndPublishesEvent() {
      var message = chatService.createConversation(
          new CreateConversationCommand(USER_ID, "Chat", "hi"));

      chatService.sendMessage(
          new SendMessageCommand(message.getConversationId(), USER_ID, "answer", AuthorType.AI));

      Assertions.assertThat(chatRepository.findMessages(message.getConversationId()))
          .extracting(ChatMessage::getContent)
          .contains("answer");

      Assertions.assertThat(events.stream(MessageCreatedEvent.class))
          .hasSize(2)
          .map(e -> e.message().getContent())
          .containsExactly("hi", "answer");
    }

    @Test
    void throwsWhenConversationMissing() {
      var missingId = UUID.randomUUID().toString();

      Assertions.assertThatThrownBy(() -> chatService.sendMessage(
              new SendMessageCommand(missingId, USER_ID, "x", AuthorType.USER)))
          .isInstanceOf(ConversationNotFound.class);

      Assertions.assertThat(events.stream(MessageCreatedEvent.class))
          .isEmpty();
    }

    @Test
    void throwsAndPublishesNothingWhenConversationBelongsToAnotherUser() {
      var message = chatService.createConversation(
          new CreateConversationCommand(USER_ID, "Chat", "hi"));
      events.clear();

      Assertions.assertThatThrownBy(() -> chatService.sendMessage(
              new SendMessageCommand(message.getConversationId(), OTHER_USER_ID, "x", AuthorType.USER)))
          .isInstanceOf(ConversationNotFound.class);

      Assertions.assertThat(events.stream(MessageCreatedEvent.class))
          .isEmpty();
    }
  }
}
