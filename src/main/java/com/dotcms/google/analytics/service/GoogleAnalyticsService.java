package com.dotcms.google.analytics.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.AnalyticsScopes;
import com.google.api.services.analytics.model.Accounts;
import com.google.api.services.analytics.model.GaData;
import com.google.api.services.analytics.model.Profiles;
import com.google.api.services.analytics.model.Webproperties;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class provides a service to interact with Google Analytics.
 * @author jsanca
 */
public class GoogleAnalyticsService {

    private final Analytics analytics;
    private final JsonFactory jsonFactory = new JacksonFactory();

    public GoogleAnalyticsService(final char [] jsonKeyFile, final String applicationName) throws Exception {

        final HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        try (final InputStream inputStream = new ByteArrayInputStream(new String(jsonKeyFile).getBytes())){
            final GoogleCredential credential = GoogleCredential
                    .fromStream(inputStream)
                    .createScoped(AnalyticsScopes.all());

            // Construct the Analytics service object.
            this.analytics = new Analytics.Builder(httpTransport, jsonFactory, credential)
                    .setApplicationName(applicationName).build();
        }
    }

    /**
     * Get the first view (profile) ID for the authorized user.
     * @return String
     * @throws IOException
     */
    public  String getFirstProfileId() throws IOException {

        // Query for the list of all accounts associated with the service account.
        final Accounts accounts = this.analytics.management().accounts().list().execute();

        if (accounts.getItems().isEmpty()) {
            throw new NotAccountFoundException("No accounts found");
        }

        final String firstAccountId = accounts.getItems().get(0).getId();

        // Query for the list of properties associated with the first account.
        Webproperties properties = analytics.management().webproperties()
                .list(firstAccountId).execute();

        if (properties.getItems().isEmpty()) {
            throw new NoWebProperesFoundException("No Webproperties found");
        }

        final String firstWebpropertyId = properties.getItems().get(0).getId();

        // Query for the list views (profiles) associated with the property.
        final Profiles profiles = analytics.management().profiles()
                .list(firstAccountId, firstWebpropertyId).execute();

        if (profiles.getItems().isEmpty()) {
            throw new NoViewProfilesFoundException("No views (profiles) found");
        }

        // Return the first (view) profile associated with the property.
        return  profiles.getItems().get(0).getId();
    }

    /**
     * Query the Core Reporting API for the number of sessions
     *  in the past seven days.
     * @param analytics
     * @param profileId
     * @return
     * @throws IOException
     */
    public GaData getPastWeekResults(final String profileId) throws IOException {
        // Query the Core Reporting API for the number of sessions
        // in the past seven days.
        return analytics.data().ga()
                .get("ga:" + profileId, "7daysAgo", "today", "ga:sessions")
                .execute();
    }
}
