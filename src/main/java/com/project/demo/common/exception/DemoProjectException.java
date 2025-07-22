package com.project.demo.common.exception;


public class DemoProjectException extends RuntimeException {
    public DemoProjectException() {
        super();
    }


    public DemoProjectException(String message) {
        super(message);
    }


    public DemoProjectException(String message, Throwable cause) {
        super(message, cause);
    }


    public DemoProjectException(Throwable cause) {
        super(cause);
    }


    protected DemoProjectException(String message, Throwable cause,
                                   boolean enableSuppression,
                                   boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
