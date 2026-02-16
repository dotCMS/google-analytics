package com.dotcms.google.analytics.osgi;

import com.dotcms.google.analytics.app.AnalyticsAppService;
import com.dotcms.google.analytics.rest.GoogleAnalyticsResource;
import com.dotcms.google.analytics.view.AnalyticsToolInfo;
import com.dotmarketing.business.CacheLocator;
import com.dotmarketing.loggers.Log4jUtil;
import com.dotmarketing.osgi.GenericBundleActivator;
import com.dotmarketing.util.ConfigUtils;
import com.dotmarketing.util.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.osgi.framework.BundleContext;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * Activator subclass for the Google Analytics Reporting plugin.
 *
 * @author Matthew Montgomery
 */
public class Activator extends GenericBundleActivator {

    private LoggerContext pluginLoggerContext;

    private final static String APP_YAML_NAME = AnalyticsAppService.APP_KEY + ".yml";

    @Override
    public final void start(final BundleContext bundleContext) throws Exception {

        //Initializing log4j...
        final LoggerContext dotcmsLoggerContext = Log4jUtil.getLoggerContext();
        //Initialing the log4j context of this plugin based on the dotCMS logger context
        pluginLoggerContext = (LoggerContext) LogManager.getContext(this.getClass().getClassLoader(),
                false,
                dotcmsLoggerContext,
                dotcmsLoggerContext.getConfigLocation());

        // Initialize services
        initializeServices(bundleContext);

        // copy the yaml
        copyAppYml();

        // Register REST resources
        publishBundleServices(bundleContext);

        // Register all ViewTool services
        registerViewToolService(bundleContext, new AnalyticsToolInfo());

        Logger.info(this, "+++++++++++++++++++++++++++++++++++++++++++++++");
        Logger.info(this, "    Starting Google Analytics OSGI plugin      ");
        Logger.info(this, "+++++++++++++++++++++++++++++++++++++++++++++++");
    }


    private final File installedAppYaml = new File(ConfigUtils.getAbsoluteAssetsRootPath() + File.separator + "server"
            + File.separator + "apps" + File.separator + APP_YAML_NAME);

    /**
     * copies the App yaml to the apps directory and refreshes the apps
     *
     * @throws IOException
     */
    private void copyAppYml() throws IOException {

        Logger.info(this.getClass().getName(), "copying YAML File:" + installedAppYaml);
        try (final InputStream in = this.getClass().getResourceAsStream("/" + APP_YAML_NAME)) {
            IOUtils.copy(in, Files.newOutputStream(installedAppYaml.toPath()));
        }
        CacheLocator.getAppsCache().clearCache();
    }

    @Override
    public final void stop(final BundleContext bundleContext) throws Exception {
        unregisterViewToolServices();

        //Shutting down log4j in order to avoid memory leaks
        Log4jUtil.shutdown(pluginLoggerContext);
    }
}
