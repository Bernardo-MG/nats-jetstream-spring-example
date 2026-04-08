
package com.bernardomg.example.spring.security.ws.nats.event;

import java.io.IOException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bernardomg.example.spring.security.ws.nats.config.NatsProperties;

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

    private final NatsProperties      natsProperties;

    public JetStreamInitializer(final JetStreamManagement jsm, final NatsProperties natsProperties) {
        super();

        this.jsm = Objects.requireNonNull(jsm);
        this.natsProperties = Objects.requireNonNull(natsProperties);
    }

    public void setup() throws Exception {
        log.info("Initializing stream {}", natsProperties.stream());

        try {
            jsm.getStreamInfo(natsProperties.stream());
            log.info("Stream {} already exists", natsProperties.stream());
        } catch (final Exception ex) {
            log.info("Stream {} not existing, adding a new instance", natsProperties.stream());
            loadStream();
        }
    }

    private final void loadStream() throws IOException, JetStreamApiException {
        final StreamConfiguration streamConfig;

        streamConfig = StreamConfiguration.builder()
            .name(natsProperties.stream())
            .subjects("events.>")
            .storageType(StorageType.File)
            .build();

        jsm.addStream(streamConfig);
    }

}
