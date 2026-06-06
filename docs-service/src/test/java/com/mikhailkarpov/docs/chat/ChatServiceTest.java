package com.mikhailkarpov.docs.chat;

import com.mikhailkarpov.docs.TestcontainersConfig;
import com.mikhailkarpov.docs.chat.command.CreateConversationCommand;
import com.mikhailkarpov.docs.chat.command.SendMessageCommand;
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

      var conversation = chatService.createConversation(command);

      Assertions.assertThat(conversation)
          .returns(USER_ID, Conversation::userId)
          .returns("My chat", Conversation::title);

      Assertions.assertThat(chatRepository.findConversation(USER_ID, conversation.id()))
          .contains(conversation);

      Assertions.assertThat(chatRepository.findMessages(conversation.id()))
          .singleElement()
          .returns(USER_ID, ChatMessage::getUserId)
          .returns(AuthorType.USER, ChatMessage::getAuthorType)
          .returns("Hello there", ChatMessage::getContent);

      Assertions.assertThat(events.stream(MessageCreatedEvent.class))
          .singleElement()
          .returns("Hello there", e -> e.message().getContent());
    }

    @Test
    void defaultsTitleWhenBlank() {
      var conversation = chatService.createConversation(
          new CreateConversationCommand(USER_ID, "  ", "Hi"));

      Assertions.assertThat(conversation.title())
          .isEqualTo("Untitled");
    }

    @Test
    void defaultsTitleWhenNull() {
      var conversation = chatService.createConversation(
          new CreateConversationCommand(USER_ID, null, "Hi"));

      Assertions.assertThat(conversation.title())
          .isEqualTo("Untitled");
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
          .contains(first.id(), second.id());
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
      var conversation = chatService.createConversation(
          new CreateConversationCommand(USER_ID, "Chat", "first"));
      chatService.sendMessage(
          new SendMessageCommand(conversation.id(), USER_ID, "second", AuthorType.AI));

      Assertions.assertThat(chatService.getMessages(conversation.id(), USER_ID))
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
      var conversation = chatService.createConversation(
          new CreateConversationCommand(USER_ID, "Chat", "hi"));

      Assertions.assertThatThrownBy(() -> chatService.getMessages(conversation.id(), OTHER_USER_ID))
          .isInstanceOf(ConversationNotFound.class);
    }
  }

  @Nested
  class SendMessage {

    @Test
    void persistsMessageAndPublishesEvent() {
      var conversation = chatService.createConversation(
          new CreateConversationCommand(USER_ID, "Chat", "hi"));

      chatService.sendMessage(
          new SendMessageCommand(conversation.id(), USER_ID, "answer", AuthorType.AI));

      Assertions.assertThat(chatRepository.findMessages(conversation.id()))
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
      var conversation = chatService.createConversation(
          new CreateConversationCommand(USER_ID, "Chat", "hi"));
      events.clear();

      Assertions.assertThatThrownBy(() -> chatService.sendMessage(
              new SendMessageCommand(conversation.id(), OTHER_USER_ID, "x", AuthorType.USER)))
          .isInstanceOf(ConversationNotFound.class);

      Assertions.assertThat(events.stream(MessageCreatedEvent.class))
          .isEmpty();
    }
  }
}
