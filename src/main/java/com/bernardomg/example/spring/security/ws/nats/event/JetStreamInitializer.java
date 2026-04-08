
package com.bernardomg.example.spring.security.ws.nats.event;

import java.io.IOException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.nats.client.JetStreamApiException;
import io.nats.client.JetStreamManagement;
import io.nats.client.api.StorageType;
import io.nats.client.api.StreamConfiguration;

public final class JetStreamInitializer {

    /**
     * Logger for the class.
     */
    private static final Logger       log = LoggerFactory.getLogger(JetStreamInitializer.class);

    private final JetStreamManagement jsm;

    private final String              stream;

    public JetStreamInitializer(final String stream, final JetStreamManagement jsm) {
        super();

        this.stream = Objects.requireNonNull(stream);
        this.jsm = Objects.requireNonNull(jsm);
    }

    public void setup() throws Exception {
        log.info("Initializing stream {}", stream);

        try {
            jsm.getStreamInfo(stream);
            log.info("Stream {} already exists", stream);
        } catch (final Exception ex) {
            log.info("Stream {} not existing, adding a new instance", stream);
            loadStream();
        }
    }

    private final void loadStream() throws IOException, JetStreamApiException {
        final StreamConfiguration streamConfig;

        streamConfig = StreamConfiguration.builder()
            .name(stream)
            .subjects("events.>")
            .storageType(StorageType.File)
            .build();

        jsm.addStream(streamConfig);
    }

}
