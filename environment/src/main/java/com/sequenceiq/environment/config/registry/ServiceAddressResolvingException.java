package com.sequenceiq.environment.config.registry;

public class ServiceAddressResolvingException extends Exception {

    public ServiceAddressResolvingException(String message) {
        super(message);
    }

    public ServiceAddressResolvingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceAddressResolvingException(Throwable cause) {
        super(cause);
    }

}
