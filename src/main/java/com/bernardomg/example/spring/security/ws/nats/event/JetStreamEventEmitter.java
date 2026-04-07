
package com.bernardomg.example.spring.security.ws.nats.event;

import java.io.IOException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bernardomg.example.spring.security.ws.event.EventEmitter;

import io.nats.client.JetStream;
import io.nats.client.JetStreamApiException;
import io.nats.client.api.PublishAck;

public final class JetStreamEventEmitter implements EventEmitter {

    /**
     * Logger for the class.
     */
    private static final Logger log = LoggerFactory.getLogger(JetStreamEventEmitter.class);

    private final JetStream     jetStream;

    public JetStreamEventEmitter(final JetStream jetStream) {
        this.jetStream = Objects.requireNonNull(jetStream);
    }

    @Override
    public final void emit(final String subject, final String message) {
        final PublishAck ack;

        log.info("Sending event to subject {} with message {}", subject, message);

        try {
            ack = jetStream.publish(subject, message.getBytes());
        } catch (IOException | JetStreamApiException e) {
            log.error("Error sending event", e);
            throw new RuntimeException(e);
        }

        log.info("Sent event. Received ack {}", ack);
    }

}
