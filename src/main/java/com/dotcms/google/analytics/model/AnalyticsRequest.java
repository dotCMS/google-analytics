package com.dotcms.google.analytics.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A model class for representing a Google Analytics query request.
 *
 */
public class AnalyticsRequest {
    /**
     * The expected date format.
     */
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("YYYY-MM-dd");

    /**
     * The default metrics value.
     */
    public static final String DEFAULT_METRICS = "ga:visits";

    /**
     * The default start date value.
     */
    private final String defaultStartDate = DATE_FORMAT.format(new Date());

    /**
     * The default end date value.
     */
    private final String defaultEndDate = DATE_FORMAT.format(new Date());

    // Required parameters

    /**
     * The profile ID of the profile from which to request data.
     */
    private String profileId;

    /**
     * A comma delimited list of Google Analytics metrics data to be retrieved from the API. A single request is
     * limited to a maximum of 10 metrics.
     */
    private String metrics;

    /**
     * Beginning date to retrieve data in format YYYY-MM-DD.
     */
    private String startDate;

    /**
     * Final date to retrieve data in format YYYY-MM-DD.
     */
    private String endDate;

    // Optional parameters

    /**
     * The dimension data to be retrieved from the API. A single request is limited to a maximum of 7 dimensions.
     */
    private String dimensions;

    /**
     * Specifies a subset of visits based on either an expression or a filter. The subset of visits matched happens
     * before dimensions and metrics are calculated.
     */
    private String segment;

    /**
     * Specifies a subset of all data matched in analytics.
     */
    private String filters;

    /**
     * The order and direction to retrieve the results. Can have multiple dimensions and metrics.
     */
    private String sort;

    /**
     * The index starting point for the request. The index starts from 1 and the default is 1.
     */
    private int startIndex;

    /**
     * The page token to retrieve the next page of results. The default is to retrieve the first page of results.
     */
    private String pageToken;

    /**
     * Maximum number of results to retrieve from the API. The default is 1,000 but can be set up to 10,000.
     */
    private int maxResults;

    /**
     * Creates a new AnalyticsRequest instance for the given profile ID.
     *
     * @param newProfileId The Google Analytics profile ID to query against.
     */
    public AnalyticsRequest(final String newProfileId) {
        if (newProfileId == null || newProfileId.equals("")) {
            throw new IllegalArgumentException("profileId cannot be null or empty");
        }

        profileId = newProfileId;
    }

    /**
     * Gets the value of the profileId property.
     *
     * @return The value of profileId.
     */
    public final String getProfileId() {
        return profileId;
    }

    /**
     * Gets the value of the metrics property.
     *
     * @return The value of metrics.
     */
    public final String getMetrics() {
        if (metrics != null && !metrics.equals("")) {
            return metrics;
        } else {
            return DEFAULT_METRICS;
        }
    }

    /**
     * Sets the value of the metrics property.
     *
     * @param newMetrics The value of metrics.
     */
    public final void setMetrics(final String newMetrics) {
        metrics = newMetrics;
    }

    /**
     * Gets the value of the startDate property.
     *
     * @return The value of startDate.
     */
    public final String getStartDate() {
        if (startDate != null && !startDate.equals("")) {
            return startDate;
        } else {
            return defaultStartDate;
        }
    }

    /**
     * Sets the value of the startDate property.
     *
     * @param newStartDate The value of startDate.
     */
    public final void setStartDate(final String newStartDate) {
        startDate = newStartDate;
    }

    /**
     * Gets the value of the endDate property.
     *
     * @return The value of endDate.
     */
    public final String getEndDate() {
        if (endDate != null && !endDate.equals("")) {
            return endDate;
        } else {
            return defaultEndDate;
        }
    }

    /**
     * Sets the value of the endDate property.
     *
     * @param newEndDate The value of endDate.
     */
    public final void setEndDate(final String newEndDate) {
        endDate = newEndDate;
    }

    /**
     * Gets the value of the dimensions property.
     *
     * @return The value of dimensions.
     */
    public final String getDimensions() {
        return dimensions;
    }

    /**
     * Sets the value of the dimensions property.
     *
     * @param newDimensions The value of dimensions.
     */
    public final void setDimensions(final String newDimensions) {
        dimensions = newDimensions;
    }

    /**
     * Gets the value of the segment property.
     *
     * @return The value of segment.
     */
    public final String getSegment() {
        return segment;
    }

    /**
     * Sets the value of the segment property.
     *
     * @param newSegment The value of segment.
     */
    public final void setSegment(final String newSegment) {
        segment = newSegment;
    }

    /**
     * Gets the value of the filters property.
     *
     * @return The value of filters.
     */
    public final String getFilters() {
        return filters;
    }

    /**
     * Sets the value of the filters property.
     *
     * @param newFilters The value of filters.
     */
    public final void setFilters(final String newFilters) {
        filters = newFilters;
    }

    /**
     * Gets the value of the sort property.
     *
     * @return The value of sort.
     */
    public final String getSort() {
        return sort;
    }

    /**
     * Sets the value of the sort property.
     *
     * @param newSort The value of sort.
     */
    public final void setSort(final String newSort) {
        sort = newSort;
    }

    /**
     * @deprecated Use {@link #getPageToken()} instead.
     * Gets the value of the startIndex property.
     *
     * @return The value of startIndex.
     */
    @Deprecated
    public final int getStartIndex() {
        return startIndex;
    }

    /**
     * @deprecated Use {@link #setPageToken()} instead.
     * Sets the value of the startIndex property.
     *
     * @param newStartIndex The value of startIndex.
     */
    @Deprecated
    public final void setStartIndex(final int newStartIndex) {
        startIndex = newStartIndex;
    }

    /**
     * Gets the value of the maxResults property.
     *
     * @return The value of maxResults.
     */
    public final int getMaxResults() {
        return maxResults;
    }

    /**
     * Sets the value of the maxResults property.
     *
     * @param newMaxResults The value of maxResults.
     */
    public final void setMaxResults(final int newMaxResults) {
        maxResults = newMaxResults;
    }

    public String getPageToken() {
        return pageToken;
    }

    public void setPageToken(String pageToken) {
        this.pageToken = pageToken;
    }
}
