package com.example.multi_db_demo.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
public class DatabaseCreator implements CommandLineRunner {

    private final DataSource masterDataSource;

    public DatabaseCreator(@Qualifier("masterDataSource") DataSource masterDataSource) {
        this.masterDataSource = masterDataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        try (Connection conn = masterDataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            createDbIfNotExists(stmt, "db_camunda");
            createDbIfNotExists(stmt, "db_credit");
        }
    }

    private void createDbIfNotExists(Statement stmt, String dbName) throws SQLException {
        // 1) Comprueba si existe en el catálogo pg_database
        ResultSet rs = stmt.executeQuery(
                "SELECT 1 FROM pg_database WHERE datname = '" + dbName + "'"
        );
        // 2) Si no hay filas, la creas
        if (!rs.next()) {
            stmt.executeUpdate("CREATE DATABASE " + dbName);
            System.out.println("Base creada: " + dbName);
        } else {
            System.out.println("Ya existe la base: " + dbName);
        }
        rs.close();
    }
}
