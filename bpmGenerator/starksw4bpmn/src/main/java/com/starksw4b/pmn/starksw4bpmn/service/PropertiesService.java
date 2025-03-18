package com.starksw4b.pmn.starksw4bpmn.service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;

@Service
public class PropertiesService {

    // Ruta del archivo application.properties del proyecto generado
    private static final String PROPERTIES_FILE_PATH = "generated-project/src/main/resources/application.properties";

    public void modifyApplicationProperties() throws IOException {
        // Leer el contenido actual de application.properties
        Path propertiesFilePath = Paths.get(PROPERTIES_FILE_PATH);

        // Verificar si el archivo existe
        if (!Files.exists(propertiesFilePath)) {
            throw new FileNotFoundException("No se encuentra el archivo application.properties en la ruta: " + PROPERTIES_FILE_PATH);
        }

        // El contenido nuevo para application.properties
        String newPropertiesContent = getNewPropertiesContent();

        // Escribir el nuevo contenido en application.properties
        try (BufferedWriter writer = Files.newBufferedWriter(propertiesFilePath, StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write(newPropertiesContent);
            System.out.println("Archivo application.properties actualizado exitosamente.");
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Error al modificar el archivo application.properties: " + e.getMessage());
        }
    }

    // Método que devuelve el nuevo contenido para application.properties
    private String getNewPropertiesContent() {
        return "# Database configuration (General)\n" +
                "spring.datasource.url=jdbc:h2:mem:camunda-db;DB_CLOSE_DELAY=-1\n" +
                "spring.datasource.driverClassName=org.h2.Driver\n" +
                "spring.datasource.username=sa\n" +
                "spring.datasource.password=\n" +
                "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect\n\n" +

                "# Camunda configuration (General)\n" +
                "camunda.bpm.database.type=postgres\n" +
                "camunda.bpm.database.schema-update=true\n" +
                "camunda.bpm.admin-user.id=demo\n" +
                "camunda.bpm.admin-user.password=demo\n" +
                "camunda.bpm.deployment-resource-pattern=classpath*:*.bpmn\n\n" +

                "# Camunda logs (General)\n" +
                "logging.level.org.camunda.bpm.engine.impl.persistence.entity=DEBUG\n" +
                "logging.level.org.camunda.bpm.engine.impl.history.event=TRACE\n\n" +

                "# Server Port (Can be changed per project)\n" +
                "server.port=9000\n";
    }
}
