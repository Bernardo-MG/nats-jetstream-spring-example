
package com.bernardomg.example.spring.security.ws.nats.jetstream;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private final String              subjects;

    public JetStreamInitializer(final String stream, final String subjects, final JetStreamManagement jsm) {
        super();

        this.stream = Objects.requireNonNull(stream);
        this.subjects = Objects.requireNonNull(subjects);
        this.jsm = Objects.requireNonNull(jsm);
    }

    public final void setup() {
        Boolean loaded;

        log.info("Initializing stream {}", stream);

        try {
            jsm.getStreamInfo(stream);
            log.info("Stream {} already exists", stream);
            loaded = true;
        } catch (final Exception ex) {
            loaded = false;
        }

        if (!loaded) {
            log.info("Stream {} not existing, adding a new instance", stream);
            loadStream();
        }
    }

    private final void loadStream() {
        final StreamConfiguration streamConfig;

        streamConfig = StreamConfiguration.builder()
            .name(stream)
            .subjects(subjects)
            .storageType(StorageType.File)
            .build();

        try {
            jsm.addStream(streamConfig);
        } catch (final Exception e) {
            // TODO: handle the exception
            throw new RuntimeException(e);
        }
    }

}
