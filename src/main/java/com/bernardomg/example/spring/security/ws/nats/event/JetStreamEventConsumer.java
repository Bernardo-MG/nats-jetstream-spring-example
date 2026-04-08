
package com.bernardomg.example.spring.security.ws.nats.event;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bernardomg.example.spring.security.ws.nats.config.NatsProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.JetStream;
import io.nats.client.MessageHandler;
import io.nats.client.PushSubscribeOptions;

public final class JetStreamEventConsumer {

    /**
     * Logger for the class.
     */
    private static final Logger  log = LoggerFactory.getLogger(JetStreamEventConsumer.class);

    private final Connection     connection;

    private final JetStream      jetStream;

    private final NatsProperties natsProperties;

    private final ObjectMapper   objectMapper;

    public JetStreamEventConsumer(final Connection connection, final JetStream jetStream,
            final NatsProperties natsProperties, final ObjectMapper objectMapper) {
        super();

        this.connection = Objects.requireNonNull(connection);
        this.jetStream = Objects.requireNonNull(jetStream);
        this.natsProperties = Objects.requireNonNull(natsProperties);
        this.objectMapper = Objects.requireNonNull(objectMapper);
    }

    public final void subscribe() throws Exception {
        final PushSubscribeOptions options;
        final Dispatcher           dispatcher;
        final MessageHandler       handler;

        log.info("Subscribing to stream {}", natsProperties.stream());

        options = PushSubscribeOptions.builder()
            .stream(natsProperties.stream())
            .durable("event-consumer")
            .build();

        dispatcher = connection.createDispatcher(msg -> {
            final String data;

            data = new String(msg.getData());
            log.info("Received event on dispatcher");
            logEvent(data);

            msg.ack();
        });

        handler = msg -> {
            final String data;

            data = new String(msg.getData());
            log.info("Received event");
            logEvent(data);

            msg.ack();
        };

        jetStream.subscribe("events.>", dispatcher, handler, false, options);
    }

    private void logEvent(final String data) {
        final Object   event;
        final JsonNode root;
        final String   className;
        final Class<?> eventClass;

        try {
            root = objectMapper.readTree(data);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        className = root.get("type")
            .asText();

        try {
            eventClass = Class.forName(className);
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            event = objectMapper.readValue(data, eventClass);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        log.info("Received event: {}", event);
    }

}
