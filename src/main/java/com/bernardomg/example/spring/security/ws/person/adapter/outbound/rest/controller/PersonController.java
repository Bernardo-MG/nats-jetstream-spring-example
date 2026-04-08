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

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bernardomg.example.spring.security.ws.event.EventEmitter;
import com.bernardomg.example.spring.security.ws.person.domain.event.PersonEvent;
import com.bernardomg.example.spring.security.ws.person.domain.model.Person;

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

    public PersonController(final EventEmitter eventEmitter) {
        super();

        this.eventEmitter = Objects.requireNonNull(eventEmitter);
    }

    @PostMapping
    public Person create(@RequestBody final Person person) throws Exception {

        log.info("Person creation");

        eventEmitter.emit("events.person.create", new PersonEvent(person));

        return person;
    }

}
