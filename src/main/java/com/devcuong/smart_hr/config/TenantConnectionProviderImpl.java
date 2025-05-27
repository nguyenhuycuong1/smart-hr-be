package com.devcuong.smart_hr.config;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@Component
public class TenantConnectionProviderImpl implements MultiTenantConnectionProvider<String> {

    public static final String DEFAULT_SCHEMA = "public";
    private DataSource datasource;

    public TenantConnectionProviderImpl(DataSource datasource) {this.datasource = datasource;}

    @Override
    public Connection getAnyConnection() throws SQLException {
        return datasource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        Connection connection = getAnyConnection();
        // If tenant is null or empty, use the default schema
        String schema = (tenantIdentifier == null || tenantIdentifier.isEmpty()) ? DEFAULT_SCHEMA : tenantIdentifier;
        log.info("Using schema: {}", schema);
        connection.setSchema(schema);
        return connection;
    }

    @Override
    public void releaseConnection(String s, Connection connection) throws SQLException {
        connection.setSchema(DEFAULT_SCHEMA);
        connection.close();
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(Class aClass) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> aClass) {
        return null;
    }
}
