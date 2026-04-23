
package com.bernardomg.example.nats.jetstream;

import java.io.IOException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.jackson.JsonFormat;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.JetStream;
import io.nats.client.JetStreamApiException;
import io.nats.client.MessageHandler;
import io.nats.client.PushSubscribeOptions;

public final class JetStreamEventConsumer {

    /**
     * Logger for the class.
     */
    private static final Logger log    = LoggerFactory.getLogger(JetStreamEventConsumer.class);

    private final Connection    connection;

    private final EventFormat   format = EventFormatProvider.getInstance()
        .resolveFormat(JsonFormat.CONTENT_TYPE);

    private final JetStream     jetStream;

    private final String        stream;

    private final String        subject;

    public JetStreamEventConsumer(final String stream, final String subject, final Connection connection,
            final JetStream jetStream) {
        super();

        this.stream = Objects.requireNonNull(stream);
        this.subject = Objects.requireNonNull(subject);
        this.connection = Objects.requireNonNull(connection);
        this.jetStream = Objects.requireNonNull(jetStream);
    }

    public final void subscribe() {
        final PushSubscribeOptions options;
        final Dispatcher           dispatcher;
        final MessageHandler       handler;

        log.info("Subscribing to stream {} and subject {}", stream, subject);

        options = PushSubscribeOptions.builder()
            .stream(stream)
            .durable("event-consumer")
            .build();

        dispatcher = connection.createDispatcher(msg -> {
            final CloudEvent event;

            event = format.deserialize(msg.getData());
            log.info("Received event on dispatcher: {}", event);

            msg.ack();
        });

        handler = msg -> {
            final CloudEvent event;

            event = format.deserialize(msg.getData());
            log.info("Received event on handler: {}", event);

            msg.ack();
        };

        try {
            jetStream.subscribe(subject, dispatcher, handler, false, options);
        } catch (IOException | JetStreamApiException e) {
            throw new RuntimeException(e);
        }
    }

}
