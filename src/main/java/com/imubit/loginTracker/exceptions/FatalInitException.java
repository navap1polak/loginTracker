package com.imubit.loginTracker.exceptions;

public class FatalInitException extends RuntimeException{

    public FatalInitException() {
    }

    public FatalInitException(String message) {
        super(message);
    }

    public FatalInitException(String message, Throwable cause) {
        super(message, cause);
    }
}
