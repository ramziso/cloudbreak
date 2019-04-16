package com.sequenceiq.cloudbreak.util;

import static java.util.Objects.requireNonNull;

/**
 * A host, port, and database name.
 */
public class HostAndPortAndDatabaseName {

    private final String host;

    private final int port;

    private final String databaseName;

    public HostAndPortAndDatabaseName(String host, int port, String databaseName) {
        this.host = requireNonNull(host, "host is null");
        this.port = requireNonNull(port, "port is null");
        this.databaseName = requireNonNull(databaseName, "databaseName is null");
    }

    /**
     * Returns the host.
     *
     * @return the host.
     */
    public String getHost() {
        return host;
    }

    /**
     * Returns the port.
     *
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * Returns the database name.
     *
     * @return the database name
     */
    public String getDatabaseName() {
        return databaseName;
    }
}
