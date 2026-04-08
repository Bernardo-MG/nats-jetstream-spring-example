
package com.bernardomg.example.spring.security.ws.person.domain.event;

import com.bernardomg.example.spring.security.ws.event.Event;
import com.bernardomg.example.spring.security.ws.person.domain.model.Person;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class PersonEvent implements Event<Person> {

    private final Person body;

    @JsonCreator
    public PersonEvent(@JsonProperty("body") final Person body) {
        super();

        this.body = body;
    }

    @Override
    public final Person getBody() {
        return body;
    }

    @Override
    public final String getType() {
        return this.getClass()
            .getName();
    }

    @Override
    public String toString() {
        return "PersonEvent [body=" + body + "]";
    }

}
