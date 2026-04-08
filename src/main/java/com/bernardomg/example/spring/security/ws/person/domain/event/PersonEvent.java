
package com.bernardomg.example.spring.security.ws.person.domain.event;

import com.bernardomg.example.spring.security.ws.event.Event;
import com.bernardomg.example.spring.security.ws.person.domain.model.Person;

public final class PersonEvent implements Event<Person> {

    private final Person body;

    public PersonEvent(final Person body) {
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

}
