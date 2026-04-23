
package com.bernardomg.example.nats.test.unit.jetstream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bernardomg.example.nats.jetstream.JetStreamEventConsumer;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.JetStream;
import io.nats.client.MessageHandler;
import io.nats.client.PushSubscribeOptions;

@DisplayName("JetStreamEventConsumer")
@ExtendWith(MockitoExtension.class)
public class TestJetStreamEventConsumer {

    private static final String                  STREAM  = "mystream";

    private static final String                  SUBJECT = "events.test";

    @Mock
    private Connection                           connection;

    private JetStreamEventConsumer               consumer;

    @Mock
    private Dispatcher                           dispatcher;

    @Captor
    private ArgumentCaptor<MessageHandler>       handlerCaptor;

    @Mock
    private JetStream                            jetStream;

    @Captor
    private ArgumentCaptor<PushSubscribeOptions> optionsCaptor;

    @BeforeEach
    void setUp() {
        consumer = new JetStreamEventConsumer(STREAM, SUBJECT, connection, jetStream);
    }

    @Test
    @DisplayName("When an IO exception is received then it is wrapped")
    void subscribe_shouldWrapExceptionsAsRuntimeException() throws Exception {
        // Given
        when(connection.createDispatcher(any(MessageHandler.class))).thenReturn(dispatcher);
        when(jetStream.subscribe(any(), any(), any(), anyBoolean(), any())).thenThrow(new IOException("boom"));

        // When / Then
        assertThatThrownBy(() -> consumer.subscribe()).isInstanceOf(RuntimeException.class)
            .hasCauseInstanceOf(IOException.class);
    }

    @Test
    @DisplayName("When subscribed then the options are sent")
    void testSubscribe_Options() throws Exception {
        // Given
        when(connection.createDispatcher(any(MessageHandler.class))).thenReturn(dispatcher);

        // When
        consumer.subscribe();

        // Then
        verify(jetStream).subscribe(eq(SUBJECT), eq(dispatcher), handlerCaptor.capture(), eq(false),
            optionsCaptor.capture());

        // Combined assertions for options
        assertThat(optionsCaptor.getValue())
            .extracting(PushSubscribeOptions::getStream, PushSubscribeOptions::getDurable)
            .containsExactly(STREAM, "event-consumer");
    }

}
