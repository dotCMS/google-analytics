package com.dotcms.google.analytics.service;

import com.dotcms.google.analytics.model.AnalyticsRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.analyticsreporting.v4.AnalyticsReporting;
import com.google.api.services.analyticsreporting.v4.AnalyticsReportingScopes;
import com.google.api.services.analyticsreporting.v4.model.DateRange;
import com.google.api.services.analyticsreporting.v4.model.Dimension;
import com.google.api.services.analyticsreporting.v4.model.GetReportsRequest;
import com.google.api.services.analyticsreporting.v4.model.GetReportsResponse;
import com.google.api.services.analyticsreporting.v4.model.Metric;
import com.google.api.services.analyticsreporting.v4.model.OrderBy;
import com.google.api.services.analyticsreporting.v4.model.ReportRequest;
import com.google.api.services.analyticsreporting.v4.model.SearchUserActivityRequest;
import com.google.api.services.analyticsreporting.v4.model.Segment;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class provides a service to interact with Google Analytics.
 * @author jsanca
 */
public class GoogleAnalyticsService {

    private final AnalyticsReporting analytics;
    private final JsonFactory jsonFactory = new JacksonFactory();

    public GoogleAnalyticsService(final char [] jsonKeyFile, final String applicationName) throws Exception {

        final HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        try (final InputStream inputStream = new ByteArrayInputStream(new String(jsonKeyFile).getBytes())){
            final GoogleCredential credential = GoogleCredential
                    .fromStream(inputStream)
                    .createScoped(AnalyticsReportingScopes.all());

            // Construct the Analytics service object.
            this.analytics = new AnalyticsReporting.Builder(httpTransport, jsonFactory, credential)
                    .setApplicationName(applicationName).build();
        }
    }

    /**
     * Get the first view (profile) ID for the authorized user.
     * @return String
     * @throws IOException
     */
    public  String getFirstProfileId() throws IOException {

        final SearchUserActivityRequest searchUserActivityRequest = new SearchUserActivityRequest();
        final AnalyticsReporting.UserActivity.Search search = this.analytics.userActivity().search(searchUserActivityRequest);
        return search.containsKey("viewId")? search.get("viewId").toString(): null;
    }

    /**
     * Query the Core Reporting API for the number of sessions
     *  in the past seven days.
     * @param profileId
     * @return GoData
     * @throws IOException
     */
    public GetReportsResponse getPastWeekResults(final String profileId) throws IOException {
        // Query the Core Reporting API for the number of sessions
        // in the past seven days.

        final DateRange dateRange = new DateRange();
        dateRange.setStartDate("7DaysAgo");
        dateRange.setEndDate("today");

        // Create the Metrics object.
        final Metric sessions = new Metric()
                .setExpression("ga:sessions")
                .setAlias("sessions");

        final Dimension pageTitle = new Dimension().setName("ga:pageTitle");

        // Create the ReportRequest object.
        final ReportRequest request = new ReportRequest()
                .setViewId(profileId) //
                .setDateRanges(Arrays.asList(dateRange))
                .setMetrics(Arrays.asList(sessions))
                .setDimensions(Arrays.asList(pageTitle));

        final ArrayList<ReportRequest> requests = new ArrayList<>();
        requests.add(request);

        // Create the GetReportsRequest object.
        final GetReportsRequest getReport = new GetReportsRequest()
                .setReportRequests(requests);

        // Call the batchGet method.
        GetReportsResponse response = this.analytics.reports().batchGet(getReport).execute();

        // Return the response.
        return response;
    }


    /**
     * Runs a query against the Google Analytics API.
     * @param analyticsRequest
     * @return GoData
     */
    public GetReportsResponse query(final AnalyticsRequest analyticsRequest) throws IOException {



        final ReportRequest request = new ReportRequest().setViewId(analyticsRequest.getProfileId());
        final DateRange dateRange = new DateRange();
        dateRange.setStartDate(analyticsRequest.getStartDate());
        dateRange.setEndDate(analyticsRequest.getEndDate());

        final Metric sessions = new Metric()
                .setExpression(analyticsRequest.getMetrics());

        request.setDateRanges(Arrays.asList(dateRange));
        request.setMetrics(Arrays.asList(sessions));

        if (analyticsRequest.getDimensions() != null && !analyticsRequest.getDimensions().equals("")) {

            final Dimension dimension = new Dimension().setName(analyticsRequest.getDimensions());
            request.setDimensions(Arrays.asList(dimension));
        }

        if (analyticsRequest.getSegment() != null && !analyticsRequest.getSegment().equals("")) {

            request.setSegments(Arrays.asList(new Segment().setSegmentId(analyticsRequest.getSegment())));
        }

        if (analyticsRequest.getSort() != null && !analyticsRequest.getSort().equals("")) {

            request.setOrderBys(Arrays.asList(new OrderBy().setSortOrder(analyticsRequest.getSort())));
        }

        if (analyticsRequest.getFilters() != null && !analyticsRequest.getFilters().equals("")) {

            request.setFiltersExpression(analyticsRequest.getFilters());
        }

        if (analyticsRequest.getStartIndex() >= 1) {
            // todo: it seems this should be replace for the next page token.
            //get = get.setStartIndex(analyticsRequest.getStartIndex());
        }

        if (analyticsRequest.getMaxResults() >= 1) {
            request.setPageSize(analyticsRequest.getMaxResults());
        }

        final ArrayList<ReportRequest> requests = new ArrayList<>();
        requests.add(request);

        // Create the GetReportsRequest object.
        final GetReportsRequest getReport = new GetReportsRequest()
                .setReportRequests(requests);

        // Call the batchGet method.
        final GetReportsResponse response = this.analytics.reports().batchGet(getReport).execute();

        // Return the response.
        return response;
    }
}
