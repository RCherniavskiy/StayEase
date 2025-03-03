package org.example.bookingapplication.config;

import org.testcontainers.containers.PostgreSQLContainer;

public class CustomPostgreSqlContainer extends PostgreSQLContainer<CustomPostgreSqlContainer> {
    private static final String DB_IMAGE = "postgres:15";
    private static CustomPostgreSqlContainer postgresContainer;

    private CustomPostgreSqlContainer() {
        super(DB_IMAGE);
    }

    public static synchronized CustomPostgreSqlContainer getInstance() {
        if (postgresContainer == null) {
            postgresContainer = new CustomPostgreSqlContainer();
        }
        return postgresContainer;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("TEST_DB_URL", postgresContainer.getJdbcUrl());
        System.setProperty("TEST_DB_USERNAME", postgresContainer.getUsername());
        System.setProperty("TEST_DB_PASSWORD", postgresContainer.getPassword());
    }

    @Override
    public void stop() {
    }
}
