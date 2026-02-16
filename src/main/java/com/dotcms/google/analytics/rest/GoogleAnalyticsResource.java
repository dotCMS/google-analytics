package com.dotcms.google.analytics.rest;

import com.dotcms.google.analytics.app.AnalyticsApp;
import com.dotcms.google.analytics.app.AnalyticsAppService;
import com.dotcms.google.analytics.model.AnalyticsRequest;
import com.dotcms.google.analytics.model.FilterRequest;
import com.dotcms.google.analytics.service.GoogleAnalyticsService;
import com.dotcms.rest.WebResource;
import com.dotmarketing.util.Logger;
import com.google.analytics.data.v1beta.DimensionValue;
import com.google.analytics.data.v1beta.MetricValue;
import com.google.analytics.data.v1beta.RunReportResponse;
import com.liferay.portal.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST endpoint for querying Google Analytics data.
 *
 * @author dotCMS
 */
@Path("/v1/googleanalytics")
public class GoogleAnalyticsResource {

    private final WebResource webResource = new WebResource();
    private final AnalyticsAppService analyticsAppService = new AnalyticsAppService();

    /**
     * Query Google Analytics 4 data via REST API.
     *
     * Example request:
     * POST /api/v1/googleanalytics/query
     * {
     *   "propertyId": "123456789",
     *   "startDate": "2026-02-09",
     *   "endDate": "2026-02-16",
     *   "metrics": ["sessions", "activeUsers"],
     *   "dimensions": ["date"],
     *   "maxResults": 100
     * }
     *
     * @param request HTTP request
     * @param response HTTP response
     * @param queryRequest Analytics query parameters
     * @return JSON response with analytics data
     */
    @POST
    @Path("/query")
    @Produces(MediaType.APPLICATION_JSON)
    public Response query(
            @Context final HttpServletRequest request,
            @Context final HttpServletResponse response,
            final GoogleAnalyticsQueryRequest queryRequest) {

        try {
            // Authenticate user
            final User user = new WebResource.InitBuilder(webResource)
                    .requiredBackendUser(true)
                    .requiredFrontendUser(false)
                    .requestAndResponse(request, response)
                    .rejectWhenNoUser(true)
                    .init()
                    .getUser();

            Logger.debug(this, () -> "User authenticated: " + user.getEmailAddress());

            // Validate request
            if (queryRequest.getPropertyId() == null || queryRequest.getPropertyId().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of("error", "propertyId is required"))
                        .build();
            }

            // Get analytics app configuration for current site
            final String siteId = request.getServerName(); // Or extract from request
            final AnalyticsApp analyticsApp = analyticsAppService.getAnalyticsApp(siteId);

            // Create Google Analytics service
            final GoogleAnalyticsService analyticsService =
                    new GoogleAnalyticsService(analyticsApp.getJsonKeyFile());

            // Build analytics request
            final AnalyticsRequest analyticsRequest =
                    new AnalyticsRequest(queryRequest.getPropertyId());

            // Set date range
            if (queryRequest.getStartDate() != null) {
                analyticsRequest.setStartDate(queryRequest.getStartDate());
            }
            if (queryRequest.getEndDate() != null) {
                analyticsRequest.setEndDate(queryRequest.getEndDate());
            }

            // Set metrics
            if (queryRequest.getMetrics() != null && !queryRequest.getMetrics().isEmpty()) {
                analyticsRequest.setMetrics(String.join(",", queryRequest.getMetrics()));
            }

            // Set dimensions
            if (queryRequest.getDimensions() != null && !queryRequest.getDimensions().isEmpty()) {
                analyticsRequest.setDimensions(String.join(",", queryRequest.getDimensions()));
            }

            // Set filters
            if (queryRequest.getFilters() != null) {
                if (queryRequest.getFilters().getDimension() != null) {
                    for (FilterRequestDTO filter : queryRequest.getFilters().getDimension()) {
                        final FilterRequest filterRequest = new FilterRequest(
                                filter.getField(),
                                filter.getOperator(),
                                filter.getValue()
                        );
                        analyticsRequest.getDimensionFilterList().add(filterRequest);
                    }
                }

                if (queryRequest.getFilters().getMetric() != null) {
                    for (FilterRequestDTO filter : queryRequest.getFilters().getMetric()) {
                        final FilterRequest filterRequest = new FilterRequest(
                                filter.getField(),
                                filter.getOperator(),
                                filter.getValue()
                        );
                        analyticsRequest.getMetricFilterList().add(filterRequest);
                    }
                }
            }

            // Set sort
            if (queryRequest.getSort() != null) {
                analyticsRequest.setSort(queryRequest.getSort());
            }

            // Set max results
            if (queryRequest.getMaxResults() != null && queryRequest.getMaxResults() > 0) {
                analyticsRequest.setMaxResults(queryRequest.getMaxResults());
            }

            // Execute query
            final RunReportResponse gaResponse = analyticsService.query(analyticsRequest);

            // Capture field names for flattened response
            final List<String> dimensionNames = queryRequest.getDimensions();
            final List<String> metricNames = queryRequest.getMetrics();

            // Convert to JSON-friendly format
            final Map<String, Object> responseData = new HashMap<>();
            responseData.put("rowCount", gaResponse.getRowCount());

            // Add metadata with dimension and metric names
            responseData.put("dimensions", dimensionNames != null ? dimensionNames : new ArrayList<>());
            responseData.put("metrics", metricNames != null ? metricNames : new ArrayList<>());

            // Convert rows to flattened structure with named fields
            final List<Map<String, String>> rows = gaResponse.getRowsList().stream()
                    .map(row -> {
                        final Map<String, String> rowData = new HashMap<>();

                        // Map dimension values to names
                        final List<DimensionValue> dimensionValues = row.getDimensionValuesList();
                        if (dimensionNames != null) {
                            for (int i = 0; i < dimensionNames.size() && i < dimensionValues.size(); i++) {
                                rowData.put(dimensionNames.get(i), dimensionValues.get(i).getValue());
                            }
                        }

                        // Map metric values to names
                        final List<MetricValue> metricValues = row.getMetricValuesList();
                        if (metricNames != null) {
                            for (int i = 0; i < metricNames.size() && i < metricValues.size(); i++) {
                                rowData.put(metricNames.get(i), metricValues.get(i).getValue());
                            }
                        }

                        return rowData;
                    })
                    .collect(Collectors.toList());

            responseData.put("rows", rows);

            // Add metadata
            final Map<String, String> metadata = new HashMap<>();
            if (gaResponse.getMetadata() != null) {
                metadata.put("currencyCode", gaResponse.getMetadata().getCurrencyCode());
                metadata.put("timeZone", gaResponse.getMetadata().getTimeZone());
            }
            responseData.put("metadata", metadata);

            return Response.ok(responseData).build();

        } catch (Exception e) {
            Logger.error(this, "Error querying Google Analytics", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }

    /**
     * Request DTO for Google Analytics query.
     */
    public static class GoogleAnalyticsQueryRequest {
        private String propertyId;
        private String startDate;
        private String endDate;
        private List<String> metrics;
        private List<String> dimensions;
        private FiltersDTO filters;
        private String sort;
        private Integer maxResults;

        // Getters and setters
        public String getPropertyId() { return propertyId; }
        public void setPropertyId(String propertyId) { this.propertyId = propertyId; }

        public String getStartDate() { return startDate; }
        public void setStartDate(String startDate) { this.startDate = startDate; }

        public String getEndDate() { return endDate; }
        public void setEndDate(String endDate) { this.endDate = endDate; }

        public List<String> getMetrics() { return metrics; }
        public void setMetrics(List<String> metrics) { this.metrics = metrics; }

        public List<String> getDimensions() { return dimensions; }
        public void setDimensions(List<String> dimensions) { this.dimensions = dimensions; }

        public FiltersDTO getFilters() { return filters; }
        public void setFilters(FiltersDTO filters) { this.filters = filters; }

        public String getSort() { return sort; }
        public void setSort(String sort) { this.sort = sort; }

        public Integer getMaxResults() { return maxResults; }
        public void setMaxResults(Integer maxResults) { this.maxResults = maxResults; }
    }

    /**
     * Filters container DTO.
     */
    public static class FiltersDTO {
        private List<FilterRequestDTO> dimension;
        private List<FilterRequestDTO> metric;

        public List<FilterRequestDTO> getDimension() { return dimension; }
        public void setDimension(List<FilterRequestDTO> dimension) { this.dimension = dimension; }

        public List<FilterRequestDTO> getMetric() { return metric; }
        public void setMetric(List<FilterRequestDTO> metric) { this.metric = metric; }
    }

    /**
     * Filter DTO.
     */
    public static class FilterRequestDTO {
        private String field;
        private String value;
        private String operator;

        public String getField() { return field; }
        public void setField(String field) { this.field = field; }

        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }

        public String getOperator() { return operator; }
        public void setOperator(String operator) { this.operator = operator; }
    }
}
