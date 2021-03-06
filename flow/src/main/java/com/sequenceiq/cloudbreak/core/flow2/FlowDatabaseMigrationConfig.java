package com.sequenceiq.cloudbreak.core.flow2;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.apache.commons.io.Charsets;
import org.apache.ibatis.migration.ConnectionProvider;
import org.apache.ibatis.migration.DataSourceConnectionProvider;
import org.apache.ibatis.migration.FileMigrationLoader;
import org.apache.ibatis.migration.operations.PendingOperation;
import org.apache.ibatis.migration.operations.UpOperation;
import org.apache.ibatis.migration.options.DatabaseOperationOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class FlowDatabaseMigrationConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(FlowDatabaseMigrationConfig.class);

    private static final String DEFAULT_SCHEMA_LOCATION = "/schema";

    private static final String SCHEMA_IN_CONTAINER = "container";

    private static final String DEFAULT_SCHEMA_LOCATION_IN_SOURCE = "flow/src/main/resources/schema";

    private static final String PENDING_OPERATION_WARNING_MSG = "WARNING: Running pending migrations out of order can create unexpected results.";

    private static final String UP_OPERATION_SUBFOLDER = "/mybatis";

    private static final String PENDING_OPERATION_SUBFOLDER = "/app";

    @Value("${flow.schema.scripts.location:" + DEFAULT_SCHEMA_LOCATION_IN_SOURCE + '}')
    private String schemaLocation;

    @Value("${flow.schema.migration.auto:true}")
    private boolean schemaMigrationEnabled;

    @Inject
    private DataSource dataSource;

    @Bean
    @DependsOn("databaseUpMigration")
    public UpOperation flowDatabaseUpMigration() throws IOException {
        UpOperation upOperation = new UpOperation();
        PendingOperation pendingOperation = new PendingOperation();
        if (schemaMigrationEnabled) {
            ConnectionProvider dataSourceConnectionProvider = new DataSourceConnectionProvider(dataSource);
            DatabaseOperationOption operationOption = new DatabaseOperationOption();
            operationOption.setDelimiter(";");
            operationOption.setFullLineDelimiter(false);
            operationOption.setSendFullScript(true);
            operationOption.setAutoCommit(false);
            try (ByteArrayOutputStream upOutStream = new ByteArrayOutputStream()) {
                try (ByteArrayOutputStream pendingOutStream = new ByteArrayOutputStream()) {
                    FileMigrationLoader upMigrationLoader = flowUpMigrationLoader();
                    upOperation.operate(dataSourceConnectionProvider, upMigrationLoader, operationOption, new PrintStream(upOutStream));
                    FileMigrationLoader pendingMigrationLoader = flowPendingMigrationLoader();
                    pendingOperation.operate(dataSourceConnectionProvider, pendingMigrationLoader, operationOption, new PrintStream(pendingOutStream));
                    String upMigrationResult = upOutStream.toString().trim();
                    String pendingMigrationResult = pendingOutStream.toString().trim();
                    if (upMigrationResult.isEmpty() && pendingMigrationResult.equals(PENDING_OPERATION_WARNING_MSG)) {
                        LOGGER.info("Schema is up to date. No migration necessary.");
                    } else {
                        logMigrationResult(upMigrationResult, "up");
                        logMigrationResult(pendingMigrationResult, "pending");
                    }
                }
            }
        }
        return upOperation;
    }

    @Bean
    public FileMigrationLoader flowUpMigrationLoader() {
        String schemaLoc = schemaLocation;
        if (SCHEMA_IN_CONTAINER.equals(schemaLocation)) {
            schemaLoc = DEFAULT_SCHEMA_LOCATION;
        }
        schemaLoc += UP_OPERATION_SUBFOLDER;
        LOGGER.info("Creating up operation migration loader for location: '{}'.....", schemaLoc);
        File scriptsDir = new File(schemaLoc);
        Properties emptyProperties = new Properties();
        String charset = Charsets.UTF_8.displayName();
        return new FileMigrationLoader(scriptsDir, charset, emptyProperties);
    }

    @Bean
    public FileMigrationLoader flowPendingMigrationLoader() {
        String schemaLoc = schemaLocation;
        if (SCHEMA_IN_CONTAINER.equals(schemaLocation)) {
            schemaLoc = DEFAULT_SCHEMA_LOCATION;
        }
        schemaLoc += PENDING_OPERATION_SUBFOLDER;
        LOGGER.info("Creating pending operation migration loader for location: '{}'.....", schemaLoc);
        File scriptsDir = new File(schemaLoc);
        Properties emptyProperties = new Properties();
        String charset = Charsets.UTF_8.displayName();
        return new FileMigrationLoader(scriptsDir, charset, emptyProperties);
    }

    private void logMigrationResult(String migrationResult, String operation) {
        if (!migrationResult.isEmpty()) {
            LOGGER.debug("Migration result of '{}' operation:\n{}", operation, migrationResult);
        }
    }
}
