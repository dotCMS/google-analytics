package com.dotcms.google.analytics.service;

public class NoViewProfilesFoundException extends RuntimeException {
    public NoViewProfilesFoundException(final String msg) {
        super(msg);
    }
}
