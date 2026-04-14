
package com.bernardomg.example.spring.security.ws.event;

import io.cloudevents.CloudEvent;

public interface EventEmitter {

    public void emit(final String subject, final CloudEvent event);

}
