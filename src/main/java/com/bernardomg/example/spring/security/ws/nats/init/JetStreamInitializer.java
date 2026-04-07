
package com.bernardomg.example.spring.security.ws.nats.init;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.nats.client.JetStreamManagement;
import io.nats.client.api.StorageType;
import io.nats.client.api.StreamConfiguration;

public class JetStreamInitializer {

    /**
     * Logger for the class.
     */
    private static final Logger       log = LoggerFactory.getLogger(JetStreamInitializer.class);

    private final JetStreamManagement jsm;

    public JetStreamInitializer(final JetStreamManagement jsm) {
        this.jsm = Objects.requireNonNull(jsm);
    }

    public void setup() throws Exception {
        final StreamConfiguration streamConfig;

        streamConfig = StreamConfiguration.builder()
            .name("EVENTS")
            .subjects("events.*")
            .storageType(StorageType.File)
            .build();

        try {
            jsm.getStreamInfo("EVENTS");
            log.info("Stream {} already exists", streamConfig.getName());
        } catch (final Exception ex) {
            log.info("Stream {} not existing, adding a new instance", streamConfig.getName());
            jsm.addStream(streamConfig);
        }
    }
}
