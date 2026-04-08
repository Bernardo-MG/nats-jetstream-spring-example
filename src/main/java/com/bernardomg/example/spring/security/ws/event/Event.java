
package com.bernardomg.example.spring.security.ws.event;

public interface Event<T> {

    public T getBody();

    public String getType();

}
