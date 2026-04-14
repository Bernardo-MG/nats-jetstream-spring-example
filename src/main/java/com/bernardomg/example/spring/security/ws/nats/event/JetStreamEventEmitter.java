
package com.bernardomg.example.spring.security.ws.nats.event;

import java.io.IOException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bernardomg.example.spring.security.ws.event.EventEmitter;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.jackson.JsonFormat;
import io.nats.client.JetStream;
import io.nats.client.JetStreamApiException;
import io.nats.client.api.PublishAck;

public final class JetStreamEventEmitter implements EventEmitter {

    /**
     * Logger for the class.
     */
    private static final Logger log    = LoggerFactory.getLogger(JetStreamEventEmitter.class);

    private final EventFormat   format = EventFormatProvider.getInstance()
        .resolveFormat(JsonFormat.CONTENT_TYPE);

    private final JetStream     jetStream;

    public JetStreamEventEmitter(final JetStream jetStream) {
        super();

        this.jetStream = Objects.requireNonNull(jetStream);
    }

    @Override
    public final void emit(final String subject, final CloudEvent event) {
        final PublishAck ack;
        final byte[]     message;

        log.info("Sending event to subject {}: {}", subject, event);

        message = format.serialize(event);

        try {
            ack = jetStream.publish(subject, message);
        } catch (IOException | JetStreamApiException e) {
            throw new RuntimeException(e);
        }

        log.info("Sent event. Received ack {}", ack);
    }

}
