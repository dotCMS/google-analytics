package com.dotcms.google.analytics.service;

import com.dotcms.google.analytics.model.AnalyticsRequest;
import com.dotmarketing.util.Logger;
import com.google.analytics.data.v1beta.BetaAnalyticsDataClient;
import com.google.analytics.data.v1beta.BetaAnalyticsDataSettings;
import com.google.analytics.data.v1beta.DateRange;
import com.google.analytics.data.v1beta.Dimension;
import com.google.analytics.data.v1beta.Metric;
import com.google.analytics.data.v1beta.OrderBy;
import com.google.analytics.data.v1beta.RunReportRequest;
import com.google.analytics.data.v1beta.RunReportResponse;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.liferay.util.StringPool;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * This class provides a service to interact with Google Analytics.
 * @author jsanca
 */
public class GoogleAnalyticsService {

    private final BetaAnalyticsDataSettings betaAnalyticsDataSettings ;
    public GoogleAnalyticsService(final char [] jsonKeyFile) throws Exception {

        try (final InputStream inputStream = new ByteArrayInputStream(new String(jsonKeyFile).getBytes())){

            this.betaAnalyticsDataSettings =
                    BetaAnalyticsDataSettings.newBuilder()
                            .setCredentialsProvider(
                                    FixedCredentialsProvider.create(
                                            GoogleCredentials.fromStream(inputStream)))
                            .build();
        }
    }

    /**
     * Runs a query against the Google Analytics API.
     * @param analyticsRequest
     * @return GoData
     */
    public RunReportResponse query(final AnalyticsRequest analyticsRequest) throws IOException {

        try (BetaAnalyticsDataClient analyticsData = BetaAnalyticsDataClient.create(betaAnalyticsDataSettings)) {

            final RunReportRequest.Builder requestBuilder =
                    RunReportRequest.newBuilder();

            requestBuilder.addDateRanges(DateRange.newBuilder()
                    .setStartDate(analyticsRequest.getStartDate())
                    .setEndDate(analyticsRequest.getEndDate())
                    .build());

            if (Objects.nonNull(analyticsRequest.getMetrics())) {

                final String [] metrics = analyticsRequest.getMetrics().split(StringPool.COMMA);
                Logger.info(this.getClass().getName(), "metrics: " + Arrays.asList(metrics));
                final List<Metric> metricList = new ArrayList<>();
                for (final String metric : metrics) {
                    Logger.info(this.getClass().getName(), "Adding metric: " + metric);
                    requestBuilder.addMetrics(Metric.newBuilder().setExpression(metric));
                }
            }

            if (analyticsRequest.getDimensions() != null && !analyticsRequest.getDimensions().equals("")) {

                final String [] dimensions = analyticsRequest.getDimensions().split(StringPool.COMMA);
                for (final String dimension : dimensions) {
                    requestBuilder.addDimensions(Dimension.newBuilder().setName(dimension).build());
                }
            }

            if (analyticsRequest.getSort() != null && !analyticsRequest.getSort().equals("")) {
                requestBuilder.addOrderBys(
                        OrderBy.newBuilder()
                                .setMetric(OrderBy.MetricOrderBy.newBuilder().setMetricName(analyticsRequest.getSort()))
                                .setDesc(true));
            }

            requestBuilder.setOffset(analyticsRequest.getStartIndex());
            requestBuilder.setLimit(analyticsRequest.getMaxResults());

            requestBuilder.setProperty("properties/" + analyticsRequest.getPropertyId());

            return analyticsData.runReport(requestBuilder.build());
        }
    }
}
