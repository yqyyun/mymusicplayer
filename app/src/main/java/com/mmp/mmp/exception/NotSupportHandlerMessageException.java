package com.mmp.mmp.exception;

public class NotSupportHandlerMessageException extends Exception{
    public NotSupportHandlerMessageException(String message) {
        super(message);
    }

    public NotSupportHandlerMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
