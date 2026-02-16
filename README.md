# Google Analytics Plugin for dotCMS

OSGi plugin that integrates Google Analytics 4 (GA4) Data API with dotCMS, enabling you to fetch analytics data programmatically via Velocity viewtools.

[![Build and Release](https://github.com/dotCMS/google-analytics/actions/workflows/release.yml/badge.svg)](https://github.com/dotCMS/google-analytics/releases)

## What It Does

This plugin provides a `$analytics` viewtool in Velocity templates for querying Google Analytics 4 data directly from your dotCMS pages. Retrieve metrics like sessions, active users, page views, and more—filtered by dimensions like date, page path, device category, etc.

**Note:** This plugin *fetches* analytics data from Google Analytics. It does NOT add tracking code to your site.

## Quick Start

### Prerequisites

- dotCMS 23.01.10 or higher
- Google Cloud Platform account with billing enabled
- Google Analytics 4 property with data to query
- Service account JSON credentials from Google Cloud

### Installation

1. **Download the latest release**
   - Go to [Releases](https://github.com/dotCMS/google-analytics/releases)
   - Download `google-analytics-X.X.X.jar`

2. **Upload to dotCMS**
   - Log into dotCMS as admin
   - Go to **System → Dynamic Plugins**
   - Click **Upload Plugin**
   - Select the JAR file

3. **Configure the App**
   - Go to **System → Apps → Google Analytics**
   - **Application Name**: Any name (e.g., "My GA4 Property")
   - **Json Key File**: Paste your Google Cloud service account JSON credentials
   - Click **Save**

### Basic Usage

```velocity
<h2>Last 7 Days Analytics</h2>

## Your GA4 property ID (just the number)
#set($propertyId = "123456789")

## Create and configure request
#set($gaRequest = $analytics.createAnalyticsRequest($propertyId))
$gaRequest.setStartDate("2026-02-09")
$gaRequest.setEndDate("2026-02-16")
$gaRequest.setMetrics("sessions,activeUsers")
$gaRequest.setDimensions("date")

## Execute query
#set($gaResponse = $analytics.query($gaRequest))

## Display results
<table>
  <thead>
    <tr><th>Date</th><th>Sessions</th><th>Active Users</th></tr>
  </thead>
  <tbody>
    #foreach($row in $gaResponse.getRowsList())
    <tr>
      <td>$row.getDimensionValues(0).getValue()</td>
      <td>$row.getMetricValues(0).getValue()</td>
      <td>$row.getMetricValues(1).getValue()</td>
    </tr>
    #end
  </tbody>
</table>
```

## Documentation

For complete setup instructions including Google Cloud configuration, Google Analytics permissions, advanced usage, and troubleshooting:

**📖 [Full Integration Guide](https://www.dotcms.com/integrations/google-analytics)**

### Key Topics Covered

- **Google Cloud Platform Setup** - Creating service accounts and enabling the Analytics Data API
- **Google Analytics Configuration** - Granting access and finding your property ID
- **Advanced Queries** - Filters, dimensions, metrics, and data processing
- **Troubleshooting** - OSGi issues, metric errors, variable name conflicts
- **Available Metrics & Dimensions** - GA4 API schema reference

## Available Metrics

Common GA4 metrics you can query:
- `sessions` - Number of sessions
- `activeUsers` - Number of distinct users
- `screenPageViews` - Total page and screen views
- `bounceRate` - Percentage of single-page sessions
- `averageSessionDuration` - Average session duration in seconds

See the [GA4 API Schema](https://developers.google.com/analytics/devguides/reporting/data/v1/api-schema) for the full list.

## Version History

### 0.4.1 (Current)
- ✅ GA4 compatibility - Changed default metric from `ga:visits` to `sessions`
- ✅ Fixed OSGi dependency resolution for local/Docker deployments
- ✅ Added proper Import-Package whitelist for dotCMS classes
- ✅ Bundle all Google Analytics Data API dependencies inside plugin JAR
- Compatible with dotCMS 23.01.10+

## Building from Source

```bash
git clone https://github.com/dotCMS/google-analytics.git
cd google-analytics
./gradlew jar
```

The JAR will be in `build/libs/google-analytics-0.4.1.jar`

## Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Make your changes
4. Commit with clear messages
5. Push to your branch
6. Open a Pull Request

## Support

- **Issues & Bugs:** [GitHub Issues](https://github.com/dotCMS/google-analytics/issues)
- **Documentation:** [dotCMS Integration Guide](https://www.dotcms.com/integrations/google-analytics)
- **dotCMS Docs:** [www.dotcms.com/docs](https://www.dotcms.com/docs)

## License

This plugin is provided as-is by dotCMS.
