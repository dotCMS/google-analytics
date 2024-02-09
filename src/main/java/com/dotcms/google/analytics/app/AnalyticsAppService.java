package com.dotcms.google.analytics.app;

import com.dotcms.security.apps.AppSecrets;
import com.dotcms.security.apps.Secret;
import com.dotmarketing.beans.Host;
import com.dotmarketing.business.APILocator;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class AnalyticsAppService {

    public static final String APP_KEY = "google-analytics";
    public final Map <String, AnalyticsApp> analyticsAppPerSiteMap = new ConcurrentHashMap<>();

    public AnalyticsApp getAnalyticsApp(final String siteKey) {
        return analyticsAppPerSiteMap.computeIfAbsent(siteKey, k -> loadSiteApp(siteKey));
    }

    private AnalyticsApp loadSiteApp(final String siteKey) {

        try {

            final Host host = APILocator.getHostAPI().find(siteKey, APILocator.systemUser(), false);
            final Optional<AppSecrets> appSecrets = APILocator.getAppsAPI().getSecrets(APP_KEY, true, host, APILocator.systemUser());

            if (!appSecrets.isPresent()) {
                 throw new AppNotPresentException("Google Analytics app not present for site: " + siteKey);
            }

            final Map<String, Secret> secrets = appSecrets.get().getSecrets();
            final String applicationName = secrets.get("applicationName").getString();
            final char[] jsonKeyFile = secrets.get("jsonKeyFile").getValue();

            return new AnalyticsApp(jsonKeyFile, applicationName);
        } catch (AppNotPresentException e) {
            throw e;
        } catch(Exception e) {
            throw new RuntimeException("Unable to load Google Analytics app per site: "
                    + siteKey, e);
        }
    }
}
