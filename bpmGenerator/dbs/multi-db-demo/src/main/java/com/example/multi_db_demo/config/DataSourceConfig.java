package com.example.multi_db_demo.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DataSourceConfig {

    // 1) Propiedades para la conexión "master" (base postgres)
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.master")
    public DataSourceProperties masterProps() {
        return new DataSourceProperties();
    }

    // 2) DataSource que usa esas propiedades
    @Bean(name = "masterDataSource")
    @Primary
    public DataSource masterDataSource() {
        return masterProps().initializeDataSourceBuilder().build();
    }

    // Aquí podrías también definir beans para db1Props, db1DataSource, db2Props, db2DataSource, etc.
}
