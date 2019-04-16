package com.sequenceiq.cloudbreak.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class DatabaseCommonTest {
    private static final String ORACLE_URL = "jdbc:oracle:@test.eu-west-1.rds.amazonaws.com:1521/hivedb";

    private static final String MARIADB_URL = "jdbc:mariadb://test.eu-west-1.rds.amazonaws.com:3306/hivedb";

    private static final String MYSQL_URL = "jdbc:mysql://test.eu-west-1.rds.amazonaws.com:3306/hivedb?useSSL=true&requireSSL=false";

    private static final String MYSQL_URL_WITHOUT_PORT = "jdbc:mysql://test.eu-west-1.rds.amazonaws.com/hivedb?useSSL=true&requireSSL=false";

    private static final String POSTGRES_URL = "jdbc:postgresql://test.eu-west-1.rds.amazonaws.com:5432/hivedb";

    @Test
    public void testGetHostAndPortAndDatabaseName() {
        HostAndPortAndDatabaseName mysqlInfo = DatabaseCommon.getHostPortAndDatabaseName(MYSQL_URL).get();

        assertThat(mysqlInfo.getHost()).isEqualTo("test.eu-west-1.rds.amazonaws.com");
        assertThat(mysqlInfo.getPort()).isEqualTo(3306);
        assertThat(mysqlInfo.getDatabaseName()).isEqualTo("hivedb");

        HostAndPortAndDatabaseName oracleInfo = DatabaseCommon.getHostPortAndDatabaseName(ORACLE_URL).get();

        assertThat(oracleInfo.getHost()).isEqualTo("test.eu-west-1.rds.amazonaws.com");
        assertThat(oracleInfo.getPort()).isEqualTo(1521);
        assertThat(oracleInfo.getDatabaseName()).isEqualTo("hivedb");

        HostAndPortAndDatabaseName postgresInfo = DatabaseCommon.getHostPortAndDatabaseName(POSTGRES_URL).get();

        assertThat(postgresInfo.getHost()).isEqualTo("test.eu-west-1.rds.amazonaws.com");
        assertThat(postgresInfo.getPort()).isEqualTo(5432);
        assertThat(postgresInfo.getDatabaseName()).isEqualTo("hivedb");

        assertThat(DatabaseCommon.getHostPortAndDatabaseName(MYSQL_URL_WITHOUT_PORT).isEmpty()).isTrue();
    }

    @Test
    public void testGetType() {
        assertThat(DatabaseCommon.getDatabaseType(MYSQL_URL).get()).isEqualTo("mysql");
        assertThat(DatabaseCommon.getDatabaseType(ORACLE_URL).get()).isEqualTo("oracle");
        assertThat(DatabaseCommon.getDatabaseType(POSTGRES_URL).get()).isEqualTo("postgresql");
        assertThat(DatabaseCommon.getDatabaseType(MARIADB_URL).isEmpty()).isTrue();
    }
}
