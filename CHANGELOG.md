# Change Log
All notable changes to this project will be documented in this file, which follows the guidelines
on [Keep a CHANGELOG](http://keepachangelog.com/). This project adheres to
[Semantic Versioning](http://semver.org/).

## [Unreleased]

# [17.104.0-M1] - 2025-09-18
### Changed
- Update event-store to 17.104.0-M2 for the new event publishing mechanism
### Added
- New REST endpoint that will serve json showing the various framework project versions on the path `/internal/framework/versions`
### Security
- Updated to latest common-bom for latest third party security fixes:
  - Update commons.beanutils version to **1.11.0** to fix **security vulnerability CVE-2025-48734**
    Detail: https://cwe.mitre.org/data/definitions/284.html
  - Update resteasy version to **3.15.5.Final** to fix **security vulnerability CVE-2023-0482**
    Detail: https://cwe.mitre.org/data/definitions/378.html
  - Update classgraph version to **4.8.112** to fix **security vulnerability CVE-2021-47621**
    Detail: https://cwe.mitre.org/data/definitions/611.html
  - Update commons-lang version to **3.18.0** to fix **security vulnerability CVE-2025-48924**
    Detail: https://cwe.mitre.org/data/definitions/674.html
  - Update Commons Fileupload version to **1.6.0** to fix **security vulnerability CVE-2025-48976**
    Detail: https://cwe.mitre.org/data/definitions/770.html
  - Update activemq-client version to **5.16.7** to fix **security vulnerability CVE-2023-46604**
    Detail: https://cwe.mitre.org/data/definitions/502.html
  - Update org.hsqldb version to **2.7.1** to fix **security vulnerability CVE-2022-41853**
    Detail: https://cwe.mitre.org/data/definitions/470.html
  - Update Quartz-Scheduler version to **2.3.2** to fix **security vulnerability CVE-2019-13990**
    Detail: https://cwe.mitre.org/data/definitions/611.html
  - Update ActiveMQ-Client version to **5.16.8** to fix **security vulnerability CVE-2025-27533**
    Detail: https://cwe.mitre.org/data/definitions/789.html
  - Update elasticsearch version to **7.17.23** to fix **security vulnerability CVE-2024-23444**
    Detail: https://cwe.mitre.org/data/definitions/311.html
  - Update elasticsearch version to **7.17.21** to fix **security vulnerability CVE-2024-43709**
    Detail: https://cwe.mitre.org/data/definitions/770.html
  - Update json-smart version to **2.4.9** to fix **security vulnerability CVE-2023-1370**
    Detail: https://cwe.mitre.org/data/definitions/674.html
  - Update gson to **2.8.9** to fix **security vulnerability CVE-2022-25647**
    Detail: https://cwe.mitre.org/data/definitions/502.html
  - Update jberet-core to **2.2.1.Final** to fix **security vulnerability CVE-2024-1102**
    Detail: https://cwe.mitre.org/data/definitions/532
  - Update commons.compress to **1.26.0** to fix **security vulnerability CVE-2024-26308**
    Detail: https://cwe.mitre.org/data/definitions/770.html
  - Update drools-core to **7.69.0.Final** to fix **security vulnerability CVE-2022-1415**
    Detail: https://cwe.mitre.org/data/definitions/502.html
  - Update commons-net to **3.9.0** to fix **security vulnerability CVE-2021-37533**
    Detail: https://cwe.mitre.org/data/definitions/20.html
  - Update jetbrains.kotlin to **1.6.0** to fix **security vulnerability CVE-2022-24329**
    Detail: https://cwe.mitre.org/data/definitions/667.html
  - Update xmlunit to **2.10.0** to fix **security vulnerability CVE-2024-31573**
    Detail: https://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2024-31573

# [17.103.3] - 2025-09-11
### Changed
- Update event-store to 17.103.2

# [17.103.2] - 2025-08-28
### Changed
- CCT-1530 changes

# [17.103.1] - 2025-08-18
- Update event-store 17.103.1 for:
  - Refactoring of error handling to make database access more efficient. These include:
    - Delete of stream errors sql no longer cascades to delete any orphaned hashes. Instead, orphaned hashes are found and deleted if necessary
    - Locking of stream_status table when publishing events, no longer calls error tables updates on locking errors
    - Streams no longer marked as fixed by default and will only mark as fixed if stream previously broken
    - We now check that any new error not a repeat of a previous error before updating stream_error tables.
      - If the error is different, we now lock stream_status and updates error details.
      - If the error is same, `markSameErrorHappened(...)` only updates stream_status.updated_at.
    - We now execute the lock of steam_status table before calculating stream statistics

# [17.103.0] - 2025-07-16
### Added
- Add micrometer metrics
- Micrometer metrics are now bootstrapped in this project at application startup using `MicrometerMetricsWildflyExtension`
### Changed
- Update event-store for:
  - New REST endpoint to fetch list of active errors 
  - New REST endpoint to fetch stream errors by streamId and errorId
  - Fetch streams by streamId and hasError
  - Refactor NewSubscriptionManager to use EventSourceNameCalculator while calculating source
  - Fixed MeterNotFoundException thrown when looking up metrics meter with unknown tags
  - Insert into stream_buffer table during event publishing is now idempotent
  - Run each event sent to the event listeners in its own transaction
  - Update the `stream_status` table with `latest_known_position`
  - Mark stream as 'up_to_date' when all events from event-buffer successfully processed
  - New column `latest_known_position` in `stream_status table`
  - New column `is_up_to_date` in `stream_status table`
  - New liquibase scripts to update stream_status table
  - New SubscriptionManager class `NewSubscriptionManager`to handle the new way of processing events
  - New replacement StreamStatusRepository class for data access of stream_status table
  - Release file-service extraction changes (via framework-libraries)
- Plugged all gauges and counters into the metrics registries
- Make framework REST endpoints available to contexts 
- Azure metrics registered with global tags and factories
- Add framework rest endpoint to fetch streams by errorHash
- Azure metrics registry is no longer bootstrapped if metrics disabled
- Release JMX command execution to work with selfhealing feature (via event-store)

# [17.102.9] - 2025-04-28
### Changed
- Update cpp.common-bom to Include netty-bom instead of individual netty library versions.

# [17.102.8] - 2025-04-17
### Changed
- Update framework to 17.102.2 for:
  - Extended RestPoller to include custom PollInterval implementation and introduced FibonacciPollWithStartAndMax class

# [17.102.7] - 2025-04-14
### Changed
- DD-38618 DLRM changes related to unified search

# [17.102.6] - 2025-03-20
### Changed
- Update event-store to 17.102.2 for:
  - Removal of error handling from BackwardsCompatibleSubscriptionManager
  - Oversized messages are now logged as `WARN` rather than `ERROR`

# [17.102.5] - 2025-03-18
### Changed
- Update event-store to 17.102.1
- Update cpp-platform-maven-common-bom to 17.102.1

# [17.102.4] - 2025-03-10
### Changed
- Update event-store to 17.102.0-M7 for:
  - Stream error handling now uses same database connection for all error handling database updates
  - Failing retries of a previously stored error no longer update the error tables and only the first failure of that event is recorded
  - Renamed `component_name` column in `stream_error` to `component`
  - Streams no longer marked as fixed if events remain stuck in the stream-buffer
  - Added new `updated_at` column to `stream_status` table

# [17.102.3] - 2025-02-28
### Changed
- Update event-store to 17.102.0-M5 for:
  - The columns `stream_id`, `component_name` and `source` on the `stream_error` table are now unique when combined
  - Inserts into `stream_error` now `DO NOTHING` if a row with the same `stream_id`, `component_name` and `source` on the `stream`error` already exists
  - Inserts into `stream_error` are therefore idempotent
  - No longer removing stream_errors before inserting a new error, as the insert is now idempotent

# [17.102.1] - 2025-02-27
### Changed
- Bump version to 17.102.x for the 'C' release
  - Update framework to 17.102.x:
- Error handling for event streams:
  - New table `stream_error` and `stream_error_hash` in viewstore
  - Exceptions thrown during event processing now stored in stream_error table
  - Exception stacktraces are parsed to find entries into our code and stored in stream_error table
  - New JNDI value `event.error.handling.enabled` with default value of `false` to enable/disable error handling for events
  - New nullable column `stream_error_id` in stream status table with constraint on stream_error table
  - New nullable column `stream_error_position` in stream status table
  - New Interceptor `EntityManagerFlushInterceptor` for EVENT_LISTENER component that will always flush the Hibernate EntityManager to commit viewstore changes to the database

# [17.101.4] - 2025-04-14
### Changed
- Updated to include sourceSystemReference into unified search part of DLRM

# [17.101.3] - 2025-01-17
### Changed
- Bump microservice-framework to 17.101.6
- Bump event-store to 17.101.5
### Removed
- Removed OWASP cross-site scripting check on html rest parameters introduced in microservice-framework release 17.6.1

# [17.101.1] - 2025-01-10
### Added
- Add dependency for org.ow2.asm version 9.3 (through maven-common-bom)
### Changed
- Bump framework-libraries to 17.101.2
- Bump microservice-framework to 17.101.5
- Bump event-store to 17.101.4
- Update postgresql.driver.version to 42.3.2 (through maven-parent-pom)
- Update common-bom to 17.101.0
### Security
- Update com.jayway.json-path to version 2.9.0 to fix **security vulnerability CWE-787**
  Detail: https://cwe.mitre.org/data/definitions/787.html (through maven-common-bom)
- Update commons.io to 2.18.0 to fix security vulnerability CVE-2024-47554
  Detail: https://nvd.nist.gov/vuln/detail/CVE-2024-47554 and https://cwe.mitre.org/data/definitions/400.html

# [17.101.0] - 2025-01-06
### Changed
- Bump framework-libraries to 17.101.1
- Bump microservice-framework to 17.101.4
- Bump event-store to 17.101.3
- Bump progression version to 17.0.52
- Optimised SnapshotJdbcRepository queries to fetch only required data
### Added
- Expose prometheus metrics through /internal/metrics/prometheus endpoint
- Provide timerRegistrar bean to register timer with metricsRegistry
- Save aggregate snapshots asynchronously in the background when we have a large amount of event on a single stream. Default it 50000. This is configurable via JNDI var snapshot.background.saving.threshold
- Add 'liquibase.analytics.enabled: false' to all liquibase.properties files to stop liquibase collecting anonymous analytics if we should ever upgrade to liquibase 4.30.0 or greater. Details can be found here: https://www.liquibase.com/blog/product-update-liquibase-now-collects-anonymous-usage-analytics

# [17.100.0] - 2024-12-16
### Changed
- Post Java 17 release of the framework
- Improved the fetching of jobs by priority from the jobstore by retrying with a different priority if the first select returns no jobs
- Refactor of File Store
- Re-introduce 'soft' delete of files in file store. 'deleted' files now have a 'deleted_at' column.
- Adding maxSession for all MessageDrivenBeans
- All events pulled from the event queue by the message driven bean now
  check the size of the message, and will log an error if the number of bytes
  is greater than a new jndi value `messaging.jms.oversize.message.threshold.bytes`
- All rest http parameters in the generated rest endpoints are now encoded using owasp to
  protect against cross site scripting
- Jmx MBean `SystemCommanderMBean` now only takes basic Java Objects to keep the JMX handling interoperable
- Jmx commands can now have and extra optional String `command-runtime-string` that can ba
  passed to JmxCommandHandlers via the JmxCommandHandling framework
- Improved error messages printed whilst running framework-command-client.jar
- Integrate metrics with prometheus meter registry
- Provide REST endpoint for prometheus to scrape metrics (/internal/metrics/prometheus)
- Split filestore `content` tables back into two tables of `metadata` and `content` to allow for backwards compatibility with liquibase
- All JmxCommandHandlers must now have `commandName` String, `commandId` UUID and `JmxCommandRuntimeParameters` in their method signatures
- Sync up audit entry content format uploaded by hybrid client with artemis entry content
- Capture metrics for storage async interface
- Expose metrics through custom REST endpoint
- Remove configuration around sending event to artemis
- Provides capability to upload audit events to datalake in async and sync mode controlled through configuration
### Added
- New method `payloadIsNull()` on DefaultJsonEnvelope, to check if the payload is `JsonValue.NULL` or `null`
- New column `buffered_at` on the stream_buffer tables to allow for monitoring of stuck stream_buffer events
- New Jmx command `RebuildSnapshotCommand` and handler that can force hydration and generation of an Aggregate snapshot
- Remove debug logging of request payload in HybridAuditClient
### Removed
- Removed @MXBean annotation from Jmx interface class to change from MXBean to MBean
### Fixed
- Fixed error in RegenerateAggregateSnapshotBean where a closed java Stream was reused
- Fix where null payloads of JsonEnvelopes get converted to `JsonValue.NULL` and cause a ClassCastException
- All JsonEnvelopes that have null payloads will now:
  - return `JsonValue.NULL` if `getPayload()` is called
  - throw `IncompatibleJsonPayloadTypeException` if `getPayloadAsJsonObject()` is called
  - throw `IncompatibleJsonPayloadTypeException` if `getPayloadAsJsonArray()` is called
  - throw `IncompatibleJsonPayloadTypeException` if `getPayloadAsJsonString()` is called
  - throw `IncompatibleJsonPayloadTypeException` if `getPayloadAsJsonNumber()` is called
- JdbcResultSetStreamer now correctly streams data using statement.setFetchSize(). The Default fetch size is 200. This can be overridden with JNDI prop jdbc.statement.fetchSize

## [17.21.31] - 2024-10-22
### Changed
- service parent pom enforcer rule now checks for latest major and minor versions and ignores patch version (via cpp.platform.maven.parent-pom)

## [17.21.23] - 2024-08-06
### Changed
- For EVENT_PROCESSORs, derive the destinationType (queue|topic) from event_sources.location.jms_uri in event-sources.yaml

## [17.21.22] - 2024-08-02
### Changed
- Provide capability to configure jgitflow develop branch name through 'jgitflow.maven.developBranchName' property (via cpp.platform.maven.parent-pom)

## [17.21.21] - 2024-07-23
### Fixed PEG-365: Jacksons-SingleArgumentConstructor-issue

## [17.21.20] - 2024-07-17
### Fixed
- Downgrade azure-storage-file-datalake to 12.10.1 to stop incompatible netty jars getting included in wars

## [17.21.19] - 2024-07-16
### Added
- Add enforcer rules to verify core domain and service parent pom are on latest release version (via cpp.platform.maven.parent-pom)

## [17.21.25] - 2024-09-1605
### Fixed
- Fixed problem of guava-testlib on compile scope:
  - Removed dependency on guava-testlib from cpp-platform-data-utils-rest  

## [17.21.18] - 2024-07-16
### Changed
- Updated common-bom to 17.21.8 in order to:
  - Update all azure libraries to latest versions 

## [17.21.17] - 2024-06-20
### Removed
- Remove duplicate jgitflow maven plugin (through cpp.platform.maven.parent-pom)

## [17.21.15] - 2024-06-20
### Added
- Add sonar-maven-plugin plugin (through cpp.platform.maven.parent-pom)

## [17.21.14] - 2024-06-17
### Added
- Add jgitflow maven plugin (through cpp.platform.maven.parent-pom)
- Enhance JmsMessageConsumerClient to retrieve message as JsonObject (through microservice-framework)
- Add docmosis dependency to dependency management (through cpp.platform.common-bom)

## [17.21.13] - 2024-06-12
### Added
- Add maven-sonar-plugin to pluginManagement (through maven-parent-pom)

## [17.21.12] - 2024-06-06
- Update framework to 17.5.0 for:
  - JmsMessageConsumerClientProvider now returns JmsMessageConsumerClient interface rather than the implemening class

## [17.21.11] - 2024-06-04
### Changed
- Add method to SystemCommanderMBean interface to invoke system command without supplying CommandRunMode (through micro-service-framework changes)

## [17.21.10] - 2024-06-03
### Changed
- Break dependency on framework-command-client in test-utils-jmx library (by micro-service-framework changes)

## [17.21.9] - 2024-05-29
### Added
- Add REPLAY_EVENT_TO_EVENT_LISTENER and REPLAY_EVENT_TO_EVENT_INDEXER system command handlers to replay single event (through micro-service-framework/event-store)
- Add LOG_RUNTIME_ID system command and it's handler to log command runtime id (through micro-service-framework)
### Changed
- Renamed JmsMessageProducerClientBuilder to JmsMessageProducerClientProvider (through micro-service-framework)
- Renamed JmsMessageConsumerClientBuilder to JmsMessageConsumerClientProvider (through micro-service-framework)

## [17.21.8] - 2024-05-15
### Changed
- Release microservice-framework changes for adding junit CloseableResource that manages closing jms resources

## [17.21.7] - 2024-05-13
### Added
- Release micro-service-framework changes for adding jms message clients for effective management of jms resources in integration tests

## [17.21.6] - 2024-02-08
### Changed
- Release jobstore retries migration liquibase script via framework-libraries

## [17.21.5] - 2023-12-13
### Changed
- Release jobstore retry capability (through framework-libraries)

## [17.21.4] - 2023-11-29
### Changed
 - Update common-bom to 17.21.3

## [17.21.3] - 2023-11-27
### Changed
 - Centralise dependencies and release maven common bom

## [17.21.1] - 2023-11-10
### Changed
 - Release cpp common bom 17.21.1 (Remove transformation related dependencies)

## [17.21.0] - 2023-11-09
### Changed
- Moved all generic library dependencies and versions into GitHub common-bom
- This common bom now depends on and imports the GitHub common-bom
- Centralise more dependencies in platform-libraries-bom after consolidating them from all contexts
- Update framework common-bom to 17.2.0
- Moved hard coded dependency on drools-mvel to common-bom
### Security
- Update org.json to version 20231013 to fix **security vulnerability CVE-2023-5072**
  Detail: https://nvd.nist.gov/vuln/detail/CVE-2023-5072
- Update plexus-codehaus to version 3.0.24 to fix **security vulnerability CVE-2022-4244**
  Detail: https://nvd.nist.gov/vuln/detail/CVE-2022-4244
- Update apache-tika to version 1.28.3 to fix **security vulnerability CVE-2022-30973**
  Detail: https://nvd.nist.gov/vuln/detail/CVE-2022-30973
- Update google-guava to version 32.0.0-jre to fix **security vulnerability CVE-2023-2976**
  Detail: https://nvd.nist.gov/vuln/detail/CVE-2023-2976

## [17.10.5] - 2023-09-06
### Removed
- Update framework to 17.1.1 in order to 
  - Remove `clientId` from the header of all generated Message Driven Beans
- Update event-store to 17.1.2 in order to 
  - Fixed IndexOutOfBoundsException in ProcessedEventStreamSpliterator during catchup

## [17.10.4] - 2023-08-02
### Changed
- Merged 8.3.19, 8.3.20, 8.3.21 Changes from release/framework-8.x.x branch

## [8.3.21] - 2023-06-08
### Changed
- Updated event-store to 8.3.3 in order to
  - Limit logging of MissingEventRanges logged to sensible maximum number.
  - Add new JNDI value `catchup.max.number.of.missing.event.ranges.to.log`

## [8.3.20] - 2023-06-06
### Fixed
- Updated event-store to 8.3.2 in order to
  - Fix Logging of missing event ranges to only log on debug

## [8.3.19] - 2023-06-01
### Fixed
- Updated event-store to 8.3.1 in order to
  - Fix batch fetch of processed events during catchup to load batches of 'n' events
    into a Java List in memory
### Changed
- Updated framework-libraries to 8.0.5 in order to
  - Remove unnecessary logging of 'skipping generation' message in pojo generator

## [17.10.3] - 2023-08-02
### Changed
- Fix failing Integration Tests
- Update to junit5
- Update surefire and failsafe plugin version to 3.1.2 (through parent-pom)
- Update wiremock-test-utils version (through common-bom)

## [17.0.0] - 2021-05-19
### Changed
- Updated to Java 17 and JEE 8.0
- Update jackson.databind version to 2.12.7.1
- Updated jackson version to 2.12.7
- Update to log4j 2
- Update artemis-jms-client to 2.10.1
- Updated to wildfly 26.1.2
- Update rest-client libraries for wildfly 26.1.2
- Updated jboss-logging version to 3.5.0.Final
- Updated slf4j/log4j bridge jar from slf4j-log4j12 to slf4j-reload4j
- Update hibernate version to 5.4.24.Final
- Update plugins.jaxb2.version to 0.15.2
- Upgrade mockito to 4.11.0
- Remove obsolete authorisation service as this is now replaced by the feature flags work
- Removed dependencies on framework-generators from the service-component modules
- Fixed dependencies of service-component modules
- Remove trigger from event_log table
- Bring in the version downgrade of liquibase to 3.5.3
- Includes fix for JMX Command Client for Wildfly 20
- take filestore changes that now does hard delete in the database
- add new healthcheck apis
- Added new healthcheck for elastic search
- DateTimeUtils and DateUtils in the elastic search test utils module no longer have static methods
- changes made for document generator client to point to system doc generator as part of CCT-1118
- Update update to liquibase to 4.10.0
- CCT-1213: Application Request API merge to master
- Remove strict checking of liquibase.properties files
- Added DELETE method with payload to RestClientService (useful for specifying for instance reason of deletion)
- Added common healthcheck for camumda
- A default name of `jms.queue.DLQ` rather than the original name of `DLQ`
- A new constructor to pass the name in if you don't want the default name
- New builder `MessageConsumerClientBuilder` that allows ActiveMQ connection parameters to be specified
- MessageProducerClient and MessageConsumerClient are now idempotent when start is called
- Extract unifiedsearch and workmanagement healthcheck modules to healthcheck-parent module
- Removed log4j-over-slf4j as it is now replaced by slf4j-reload4j
- Updated 'workmanagement-healthchecks' group name
- Remove illegal-access flag from surefire plugin (through framework maven-parent-pom)
- Fix pojo generators by making additionalProperties assignment null safe (through framework-libraries)
- Downgrade h2 to 1.4.196 as 2.x.x is too strict for our tests
- Don't close restclient in azure utils resteasyclient service, as response is read in calling code
- Fix commons.text.version maven property in bom
- Added a method for Word Document Generation for DD-25099
- make originatingHearingId optional in the unified search crime-case-index-schema for SNI-2020
- Close client in DefaultSystemIdMapperClient, RestEasyClientService
- Update unifiedsearch-healthchecks dependency group id
- Close the client in DefaultRestClientProcessor
- Add common dependencies used by Deltaspike tests
- Remove unnecessary logging of 'skipping generation' message in pojo generator

## [17.10.5] - 2023-07-06
### Changed
Fix jacoco coverage issue (by releasing 17.0.1 parent-pom)

## [17.0.4] - 2023-06-05
### Changed
- Changed root pom artifactId to `platform-libraries-parent-pom`
- Added framework plugin dependencies to fix maven warnings/errors
- Moved various third party library version numbers to common-bom
### Security
- Update org.json to version 20230227 to fix **security vulnerability CVE-2022-45688**
  Detail: https://nvd.nist.gov/vuln/detail/CVE-2022-45688

## [17.0.2] - 2023-06-05
### Fixed
- Fix failing Integration Tests
- Fix wiremock url matching regex to make it work with latest wiremock version

## [17.0.0] - 2023-05-19
### Changed
- Updated to Java 17 and JEE 8.0
- Update jackson.databind version to 2.12.7.1
- Updated jackson version to 2.12.7
- Update to log4j 2
- Update artemis-jms-client to 2.10.1
- Updated to wildfly 26.1.2
- Update rest-client libraries for wildfly 26.1.2
- Updated jboss-logging version to 3.5.0.Final
- Updated slf4j/log4j bridge jar from slf4j-log4j12 to slf4j-reload4j
- Update hibernate version to 5.4.24.Final
- Update plugins.jaxb2.version to 0.15.2
- Upgrade mockito to 4.11.0
- Remove obsolete authorisation service as this is now replaced by the feature flags work
- Removed dependencies on framework-generators from the service-component modules
- Fixed dependencies of service-component modules
- Remove trigger from event_log table
- Bring in the version downgrade of liquibase to 3.5.3
- Includes fix for JMX Command Client for Wildfly 20
- take filestore changes that now does hard delete in the database
- add new healthcheck apis
- Added new healthcheck for elastic search
- DateTimeUtils and DateUtils in the elastic search test utils module no longer have static methods
- changes made for document generator client to point to system doc generator as part of CCT-1118
- Update update to liquibase to 4.10.0
- CCT-1213: Application Request API merge to master
- Remove strict checking of liquibase.properties files
- Added DELETE method with payload to RestClientService (useful for specifying for instance reason of deletion)
- Added common healthcheck for camumda
- A default name of `jms.queue.DLQ` rather than the original name of `DLQ`
- A new constructor to pass the name in if you don't want the default name
- New builder `MessageConsumerClientBuilder` that allows ActiveMQ connection parameters to be specified
- MessageProducerClient and MessageConsumerClient are now idempotent when start is called
- Extract unifiedsearch and workmanagement healthcheck modules to healthcheck-parent module
- Removed log4j-over-slf4j as it is now replaced by slf4j-reload4j
- Updated 'workmanagement-healthchecks' group name
- Remove illegal-access flag from surefire plugin (through framework maven-parent-pom)
- Fix pojo generators by making additionalProperties assignment null safe (through framework-libraries)
- Downgrade h2 to 1.4.196 as 2.x.x is too strict for our tests
- Don't close restclient in azure utils resteasyclient service, as response is read in calling code
- Fix commons.text.version maven property in bom
- Added a method for Word Document Generation for DD-25099
- make originatingHearingId optional in the unified search crime-case-index-schema for SNI-2020
- Close client in DefaultSystemIdMapperClient, RestEasyClientService
- Update unifiedsearch-healthchecks dependency group id
- Close the client in DefaultRestClientProcessor
- Add common dependencies used by Deltaspike tests
- Remove unnecessary logging of 'skipping generation' message in pojo generator

## [8.3.0] - 2023-05-19
### Changed
- Update event-store to 8.3.0 in order to:
  - Fetch of processed events from viewstore during catchup is now batched, to stop
    transaction timeout when there are millions of events
  - Add new JNDI value `catchup.fetch.processed.event.batch.size` that configures the
    size of the batch. Default value is 100,000

## [8.1.0] - 2023-01-31
### Changed
- Updated event-store version to 8.2.2 in order to:
  - Remove unnecessary indexes from event_log table

## [8.0.16] - 2023-01-10
### Added
- Added a method for Word Document Generation for DD-25099

## [8.0.15] - 2022-12-28
- make originatingHearingId optional in the unified search crime-case-index-schema for SNI-2020

## [8.0.14] - 2022-07-25
### Changed
- Updated framework-libraries to 8.0.5 in order to
  - Update API path for alfresco read material endpoint
### Added
- hasResultsShared to HearingDay index and schema

## [8.0.12] - 2022-05-28
### Changed
- Added DELETE method with payload to RestClientService (useful for specifying for instance reason of deletion)

## [8.0.11] - 2022-05-06

## [7.3.3] - 2021-12-14
### Changed
- Updated ElasticSearch to 7.16.2

## [7.3.0] - 2021-12-14
### Changed
- Updated log4j2 to 2.15.0 to fix security vulnerability https://www.randori.com/blog/cve-2021-44228/


## [8.0.9] - 2022-03-23
### Reverted
- Reverted the changes made for document generator client to point to system doc generator as part of CCT-1118

## [8.0.7] - 2022-03-18
### Changed
- changes made for document generator client to point to system doc generator as part of CCT-1118

## [8.0.6] - 2022-03-11
### Changed
- Updated Framework Libraries to 8.0.4:
- Updated Framework to 8.0.4:
- Updated Event Store to 8.2.0:
  - Filestore no longer supports in memory H2 databases. Postgres only

## [8.0.5] - 2022-03-08
### Changed
- Updated Framework Libraries to 8.0.3:
- Updated Framework to 8.0.3:
- Updated Event Store to 8.1.2:
  - Filestore now does hard delete when deleting files rather than just marking as deleted

## [8.0.4] - 2021-12-14
### Changed
- Updated Framework to 8.x.x:
  - Removed trigger from event_log table
  - Fixed out of memory error in catchup when catching up over 4,000,000 events
  - Bumped version to 8.0.4 to match framework 8.x.x and to avoid version clashes with previous failed releases

## [7.3.3] - 2021-12-14
### Changed
- Updated ElasticSearch to 7.16.2

## [7.3.0] - 2021-12-14
### Changed
- Updated log4j2 to 2.15.0 to fix security vulnerability https://www.randori.com/blog/cve-2021-44228/


## [8.0.3] - 2021-10-15
### Changed
- Added new method to DocumentGeneratorClient to generate document using thymeleaf template

## [8.0.2] - 2021-10-13
### Changed
- Downgrade liquibase to 3.5.3 to match the version in the framework

## [8.0.01] - 2021-10-08
### Changed
- DD-12921 - Added bulk system ID mapper client api

## [8.0.0] - 2021-10-05
### Changed
- Update to framework 8.0.0 as the final Java 8 version of the framework
- Bump version to 8 to match framework 8 version
- Update common-bom to 8.0.0
- Update framework-libraries to 8.0.1
- Update framework to 8.0.1
- Update event-store to 8.0.1


## [7.2.11] - 2021-08-20
### Changed
- Fixing DD-15055 fixed query audit provider for large message

## [Unreleased]
## [7.2.10] - 2021-07-08
### Changed
- Fixing DD-12772 Feature Toggle - Get features from Azure throws error when fails

## [7.2.9] - 2021-06-18
### Changed
- Added SSLContextBuild in a new common azure-utils module

## [7.2.8] - 2021-04-30
### Changed
- Support for multiple ElasticSearch index with CPSSearch

## [7.2.7] - 2021-04-21
### Changed
- Update framework to 7.2.23
- Update event-store to 7.2.3

## [7.2.6] - 2021-04-01
### Added
- CCT-1026: Unified Application Process

## [7.2.5] - 2021-02-18
### Fixed
- Fixed spelling of parameter in SystemIdMapperClient
- Javadoc for SystemIdMapperClient

## [7.2.4] - 2021-02-18
### Added
- Added remap function to system-id-mapper

## [7.2.3] - 2021-02-15
### Changed
- The enabling/disabling of auditing of endpoints is now an app specific JNDI variable,
  rather than global. The following variables were changed:
  - configuration.jndi.command-api.audit.enabled
  - configuration.jndi.event-api.audit.enabled
  - configuration.jndi.query-api.audit.enabled
  - configuration.jndi.query-controller.audit.enabled
  - configuration.jndi.query-view.audit.enabled
  - configuration.jndi.command-controller.audit.enabled
  - configuration.jndi.command-handler.audit.enabled


## [7.2.0] - 2020-12-04
### Added
- Added support for feature toggling
- Interceptor in command-api to forbid requests to feature disabled handlers
- Added feature data retrieval mechanism from Azure AppConfig using JNDI properties:
  - <code>featureManagerConnectionString</code>: connection details for the Azure instance to connect to
  - <code>featureLabel</code>: the label to use for the Azure SDK filtering
- Interceptor to all the layers to forbid requests to feature disabled handlers
### Changed
- Updated framework-libraries to 7.2.2
- Updated microservices-framework to 7.2.2
- Updated event-store to 7.2.2
- Updated cpp.platform.maven.common-bom to 7.2.0

## [7.1.2] - 2020-10-16
### Changed
- Updated framework-libraries to 7.1.5
- Updated cpp.platform.maven-common-bom to 7.1.1

## [7.1.1] - 2020-10-15
### Changed
- Updated various library dependencies to fix security vulnerabilities:
  - junit:                4.12    ->   4.13.1      https://github.com/advisories/GHSA-269g-pwp5-87pp
  - commons.beanutils:    1.9.2   ->   1.9.4       https://github.com/advisories/GHSA-6phf-73q6-gh87
  - commons.guava:        19.0    ->   29.0-jre    https://github.com/advisories/GHSA-mvr2-9pj6-7w5j
- Updated cpp.platform.maven-common-bom to 7.1.0 to pull in the above security fixes

## [7.1.0] - 2020-10-05
### Changed
- Updated common-bom to 7.0.5 to pull in the correct version of h2 (version 1.4.196)

## [7.0.23] - 2020-09-28
### Changed
- Now using framework artifacts stored in Cloudsmith.io rather than bintray
- Updated framework-libraries to 7.1.1
- Updated framework to 7.1.1
- Updated event-store to 7.1.1

## [7.0.23] - 2020-09-28
### Added
- Support for DELETE method in RestClientService
### Changed
- reference-data to version 7.0.15

## [7.0.22] - 2020-09-11
### Changed
- Moved in libraray-parent-pom to be the parent pom of this project thus removing the
  need for the library-parent-pom project
- Updated to framework-api 7.0.11

## [7.0.20] - 2020-09-01
### Changed
- Update library-parent-pom to 7.0.10

## [7.0.19] - 2020-08-24
### Changed
- Added MeridianUtil from listing here as it's used in other contexts

## [7.0.18] - 2020-08-14
### Changed
- Update library-parent-pom to version 7.0.9

## [7.0.17] - 2020-08-13
### Changed
- Update library-parent-pom to version 7.0.8

## [7.0.16] - 2020-07-30
### Added
- Migrated cpp.platform.library.activiti to this repo

## [7.0.12] - 2020-07-26
### Changed
- Corrected platform-libraries.version in service-components bom entries to be cpp.platform-libraries.version

## [7.0.11] - 2020-07-25
### Added
- service-component dependencies added to the platform-libraries-bom
### Changed
- Promoted wiremock removed from properties, now comes from common-bom
- Promoted elastic search versions removed, now comes from common-bom
- Updated the version of library-parent

## [7.0.5] - 2020-07-08
### Changed
- Update:
  - library-parent-pom to version 7.0.2

## [7.0.4] - 2020-07-08
### Changed
- Update:
  - usersgroups to version 7.0.2
  - progression to version 7.0.6
  - sjp to version 7.0.5
  - reference-data to version 7.0.2

## [7.0.3] - 2020-06-22
### Added
- Moved in 'service-components' module from service-parent-pom

## [7.0.2] - 2020-06-16
### Added
- Moved in 'service-components' module from service-parent-pom

## [7.0.1] - 2020-06-15
### Added
- Added .ci-hooks/fixup-versions for release management
### Changed
- Moved framework versions to library-parent-pom

## [7.0.1] - 2020-06-15
### Added
- Added .ci-hooks/fixup-versions for release management
### Changed
- Moved framework versions to library-parent-pom

## [7.0.0] - 2020-06-05
### Added
- Added access-control-parent as a sub project
- Added audit-library-parent as a sub project
- Added authorisation-parent as a sub project
- Added cpp-platform-library-utils as a sub project
- Added id-mapper-client as a sub project
- Added system-documentgenerator-client as a sub project
- Added system-users-library as a sub project
- Added unifiedsearch-library-parent as a sub project

### Changed
- Bumped version of all sub projects to 7.0.0 to match the framework


