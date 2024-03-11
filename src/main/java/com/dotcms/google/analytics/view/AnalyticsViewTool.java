package com.dotcms.google.analytics.view;

import com.dotcms.google.analytics.app.AnalyticsApp;
import com.dotcms.google.analytics.app.AnalyticsAppService;
import com.dotcms.google.analytics.model.AnalyticsRequest;
import com.dotcms.google.analytics.service.GoogleAnalyticsService;
import com.dotmarketing.beans.Host;
import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.util.Logger;
import com.google.analytics.data.v1beta.RunReportResponse;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.context.ViewContext;
import org.apache.velocity.tools.view.tools.ViewTool;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A ViewTool implementation that provides functionality for querying Google Analytics data.
 */
public class AnalyticsViewTool implements ViewTool {

    private final AnalyticsAppService analyticsAppService = new AnalyticsAppService();
    private final Map<String, GoogleAnalyticsService> googleAnalyticsServiceMap = new ConcurrentHashMap<>();

    private HttpServletRequest request;
    private Context ctx;

    public void init(final Object obj) {
        Logger.info(this, "Initializing the AnalyticsViewTool");
        ViewContext context = (ViewContext) obj;
        this.request = context.getRequest();
        this.ctx = context.getVelocityContext();
    }

    public AnalyticsViewTool() {

    }

    /**
     * Creates a default AnalyticsRequest instance for the provided property ID.
     *
     * @param propertyId The property ID to query against. Example: ga:12345678
     * @return A default AnalyticsRequest instance that can be customized as needed.
     */
    public final AnalyticsRequest createAnalyticsRequest(final String propertyId) {

        return new AnalyticsRequest(propertyId);
    }

    /**
     * Executes an analytics query using the provided request.
     *
     * @param analyticsRequest The AnalyticsRequest instance representing the desired query.
     * @return A RunReportResponse instances containing the results of the query.
     */
    public final RunReportResponse query(final AnalyticsRequest analyticsRequest) throws IOException {

        final Host currentHost = WebAPILocator.getHostWebAPI().getHost(this.request);
        final String siteId = currentHost.getIdentifier();

        final GoogleAnalyticsService googleAnalyticsService =
                this.googleAnalyticsServiceMap.computeIfAbsent(siteId,
                        key -> getGoogleAnalyticsService(siteId));

        return googleAnalyticsService.query(analyticsRequest);
    }


    private  GoogleAnalyticsService getGoogleAnalyticsService(final String siteIdentifier) {
        try {

            final AnalyticsApp analyticsApp = this.analyticsAppService.getAnalyticsApp(siteIdentifier);
            return new GoogleAnalyticsService(analyticsApp.getJsonKeyFile());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
