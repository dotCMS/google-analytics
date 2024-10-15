package com.dotcms.google.analytics.view;

import org.apache.velocity.tools.view.context.ViewContext;
import org.apache.velocity.tools.view.servlet.ServletToolInfo;

/**
 * Tool info for the AnalyticsViewTool class.
 *
 */
public class AnalyticsToolInfo extends ServletToolInfo {

    @Override
    public final String getKey() {
        return "analytics";
    }

    @Override
    public final String getScope() {
        return ViewContext.REQUEST;
    }

    @Override
    public final String getClassname() {
        return AnalyticsViewTool.class.getName();
    }

    @Override
    public final Object getInstance(final Object initData) {
        AnalyticsViewTool viewTool = new AnalyticsViewTool();

        viewTool.init(initData);

        setScope(ViewContext.APPLICATION);

        return viewTool;
    }


}
