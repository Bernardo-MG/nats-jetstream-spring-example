/**
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2021-2025 the original author or authors.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.bernardomg.example.spring.security.ws.person.adapter.outbound.rest.controller;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bernardomg.example.spring.security.ws.event.EventEmitter;
import com.bernardomg.example.spring.security.ws.person.domain.model.Person;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;

/**
 * Rest controller for the example model.
 *
 * @author Bernardo Mart&iacute;nez Garrido
 */
@RestController
@RequestMapping("/person")
public class PersonController {

    /**
     * Logger for the class.
     */
    private static final Logger log = LoggerFactory.getLogger(PersonController.class);

    /**
     * Event emitter.
     */
    private final EventEmitter  eventEmitter;

    private final ObjectMapper  objectMapper;

    public PersonController(final ObjectMapper objectMapper, final EventEmitter eventEmitter) {
        super();

        this.objectMapper = Objects.requireNonNull(objectMapper);
        this.eventEmitter = Objects.requireNonNull(eventEmitter);
    }

    @PostMapping
    public Person create(@RequestBody final Person person) throws Exception {
        final CloudEvent cloudEvent;

        log.info("Person creation");

        cloudEvent = createEvent(person);
        eventEmitter.emit("events.person.create", cloudEvent);

        return person;
    }

    private final CloudEvent createEvent(final Person person) {
        final UUID           id;
        final OffsetDateTime time;
        final URI            uri;
        final String         eventType;
        final byte[]         content;

        try {
            content = objectMapper.writeValueAsBytes(person);
        } catch (final JsonProcessingException e) {
            // TODO handle the exception
            throw new RuntimeException(e);
        }

        id = UUID.randomUUID();
        time = OffsetDateTime.now();
        eventType = "com.bernardomg.example.spring.security.ws.person.domain.event.PersonEvent";
        // TODO: use valid source
        uri = URI.create("urn:example:source");
        return CloudEventBuilder.v1()
            .withId(id.toString())
            .withType(eventType)
            .withTime(time)
            .withDataContentType("application/json")
            .withData(content)
            .withSource(uri)
            .build();
    }

}
