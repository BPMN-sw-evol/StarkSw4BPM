package com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.generator;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class PropertiesFileGeneratorService {

    public void generateApplicationProperties(Path projectPath) throws IOException {
        Path propertiesFile = projectPath.resolve("src/main/resources/application.properties");

        String propertiesContent = """
            spring.application.name=form

            spring.thymeleaf.prefix=classpath:/templates/
            spring.thymeleaf.suffix=.html

            spring.datasource.url=jdbc:postgresql://localhost:5432/db_credit
            spring.datasource.username=postgres
            spring.datasource.password=root
            spring.datasource.driver-class-name=org.postgresql.Driver

            spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
            spring.jpa.hibernate.ddl-auto=update

            # URL del REST API de Camunda
            camunda.rest.url=http://localhost:8080/engine-rest

            server.port=9001
            """;

        Files.writeString(propertiesFile, propertiesContent);
        System.out.println("✅ application.properties generado con configuración fija.");
    }
}
