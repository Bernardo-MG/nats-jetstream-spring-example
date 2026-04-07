
package com.bernardomg.example.spring.security.ws.nats.event;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bernardomg.example.spring.security.ws.nats.config.NatsProperties;

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

    public JetStreamEventConsumer(final Connection connection, final JetStream jetStream,
            final NatsProperties natsProperties) {
        this.connection = Objects.requireNonNull(connection);
        this.jetStream = Objects.requireNonNull(jetStream);
        this.natsProperties = Objects.requireNonNull(natsProperties);
    }

    public final void subscribe() throws Exception {
        final PushSubscribeOptions options;
        final Dispatcher           dispatcher;
        final MessageHandler       handler;

        log.info("Subscribing to stream {}", natsProperties.stream());

        options = PushSubscribeOptions.builder()
            .stream(natsProperties.stream())
            .durable("event-service")
            .build();

        dispatcher = connection.createDispatcher(msg -> {
            final String data = new String(msg.getData());
            log.info("Received event {}", data);

            msg.ack();
        });

        handler = msg -> {
            final String data = new String(msg.getData());
            log.info("Received event {}", data);

            msg.ack();
        };

        jetStream.subscribe("events.person", dispatcher, handler, false, options);
    }

}
