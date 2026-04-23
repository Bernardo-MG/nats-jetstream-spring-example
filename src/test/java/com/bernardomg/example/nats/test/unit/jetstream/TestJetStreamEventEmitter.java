
package com.bernardomg.example.nats.test.unit.jetstream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bernardomg.example.nats.jetstream.JetStreamEventEmitter;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.format.EventFormat;
import io.cloudevents.core.provider.EventFormatProvider;
import io.cloudevents.jackson.JsonFormat;
import io.nats.client.JetStream;
import io.nats.client.api.PublishAck;

@DisplayName("JetStreamEventEmitter")
@ExtendWith(MockitoExtension.class)
public class TestJetStreamEventEmitter {

    private static final String     JSON       = "{\"name\":\"John\"}";

    private static final String     EVENT_JSON = "{\"specversion\":\"1.0\",\"id\":\"123\",\"source\":\"test-source\",\"type\":\"type\",\"datacontenttype\":\"application/json\",\"data\":{\"name\":\"John\"}}";

    private static final String     SUBJECT    = "events.test";

    private static final CloudEvent EVENT      = CloudEventBuilder.v1()
        .withId("123")
        .withType("type")
        .withSource(URI.create("test-source"))
        .withDataContentType("application/json")
        .withData(JSON.getBytes())
        .build();

    @InjectMocks
    private JetStreamEventEmitter   emitter;

    @Mock
    private JetStream               jetStream;

    @Test
    @DisplayName("When an event is received it is serialized and emitted")
    void testEmit() throws Exception {
        final byte[]      payload;
        final PublishAck  ack;

        // Given
        payload = EVENT_JSON.getBytes();

        ack = mock(PublishAck.class);
        when(jetStream.publish(SUBJECT, payload)).thenReturn(ack);

        // When
        emitter.emit(SUBJECT, EVENT);

        // Then
        verify(jetStream).publish(SUBJECT, payload);
    }

    @Test
    void testEmit_IoException() throws Exception {
        final EventFormat format;
        final byte[]      payload;

        // Given
        format = EventFormatProvider.getInstance()
            .resolveFormat(JsonFormat.CONTENT_TYPE);
        payload = format.serialize(EVENT);

        doThrow(new IOException("io error")).when(jetStream)
            .publish(SUBJECT, payload);

        // When / Then
        assertThatThrownBy(() -> emitter.emit(SUBJECT, EVENT)).isInstanceOf(RuntimeException.class)
            .hasCauseInstanceOf(IOException.class);
    }

}
