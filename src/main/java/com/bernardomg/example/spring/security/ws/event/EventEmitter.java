
package com.bernardomg.example.spring.security.ws.event;

public interface EventEmitter {

    public void emit(String subject, String message) throws Exception;

}
