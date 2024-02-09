package com.dotcms.google.analytics.app;

public class AppNotPresentException extends RuntimeException {
    public AppNotPresentException(final String message) {
        super(message);
    }
}
