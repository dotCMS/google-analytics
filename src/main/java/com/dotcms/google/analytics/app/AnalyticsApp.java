package com.dotcms.google.analytics.app;

public class AnalyticsApp {

    private final char[] jsonKeyFile;
    private final String applicationName;

    public AnalyticsApp(final char[] jsonKeyFile,
                        final String applicationName) {
        this.jsonKeyFile = jsonKeyFile;
        this.applicationName = applicationName;
    }

    public char [] getJsonKeyFile() {
        return
                jsonKeyFile;
    }

    public String getApplicationName() {
        return applicationName;
    }
}
