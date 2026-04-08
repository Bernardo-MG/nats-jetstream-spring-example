
package com.bernardomg.example.spring.security.ws.nats.event;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bernardomg.example.spring.security.ws.event.Event;
import com.bernardomg.example.spring.security.ws.event.EventEmitter;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.nats.client.JetStream;
import io.nats.client.api.PublishAck;

public final class JetStreamEventEmitter implements EventEmitter {

    /**
     * Logger for the class.
     */
    private static final Logger log = LoggerFactory.getLogger(JetStreamEventEmitter.class);

    private final JetStream     jetStream;

    private final ObjectMapper  objectMapper;

    public JetStreamEventEmitter(final JetStream jetStream, final ObjectMapper objectMapper) {
        super();

        this.jetStream = Objects.requireNonNull(jetStream);
        this.objectMapper = Objects.requireNonNull(objectMapper);
    }

    @Override
    public final void emit(final String subject, final Event<?> event) throws Exception {
        final PublishAck ack;
        final byte[]     message;

        log.info("Sending to subject {} the event {}", subject, event);

        message = objectMapper.writeValueAsBytes(event);

        ack = jetStream.publish(subject, message);

        log.info("Sent event. Received ack {}", ack);
    }

}
