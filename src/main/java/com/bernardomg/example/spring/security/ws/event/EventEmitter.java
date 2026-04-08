
package com.bernardomg.example.spring.security.ws.event;

public interface EventEmitter {

    public void emit(final String subject, final Event<?> event) throws Exception;

}
