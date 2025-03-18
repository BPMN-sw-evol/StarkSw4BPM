package com.starksw4b.pmn.starksw4bpmn.service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;

@Service
public class POMService {

    // Ruta del archivo pom.xml del proyecto generado
    private static final String POM_FILE_PATH = "generated-project/pom.xml";

    public void modifyPomXml() throws IOException {
        // Leer el contenido actual del pom.xml
        Path pomFilePath = Paths.get(POM_FILE_PATH);

        // Verificar si el archivo existe
        if (!Files.exists(pomFilePath)) {
            throw new FileNotFoundException("No se encuentra el archivo pom.xml en la ruta: " + POM_FILE_PATH);
        }

        // El contenido nuevo para el pom.xml
        String newPomContent = getNewPomContent();

        // Escribir el nuevo contenido en el pom.xml
        try (BufferedWriter writer = Files.newBufferedWriter(pomFilePath, StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write(newPomContent);
            System.out.println("Archivo pom.xml actualizado exitosamente.");
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Error al modificar el archivo pom.xml: " + e.getMessage());
        }
    }

    // Método que devuelve el nuevo contenido para el pom.xml
    private String getNewPomContent() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
                "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                "    <modelVersion>4.0.0</modelVersion>\n" +
                "    <groupId>com.example</groupId>\n" +
                "    <artifactId>BPM-Engine</artifactId>\n" +
                "    <version>1.0-SNAPSHOT</version>\n" +
                "    <properties>\n" +
                "        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>\n" +
                "        <maven.compiler.source>17</maven.compiler.source>\n" +
                "        <maven.compiler.target>17</maven.compiler.target>\n" +
                "    </properties>\n" +
                "    <dependencyManagement>\n" +
                "        <dependencies>\n" +
                "            <dependency>\n" +
                "                <groupId>org.springframework.boot</groupId>\n" +
                "                <artifactId>spring-boot-dependencies</artifactId>\n" +
                "                <version>2.7.9</version>\n" +
                "                <type>pom</type>\n" +
                "                <scope>import</scope>\n" +
                "            </dependency>\n" +
                "            <dependency>\n" +
                "                <groupId>org.camunda.bpm</groupId>\n" +
                "                <artifactId>camunda-bom</artifactId>\n" +
                "                <version>7.19.0</version>\n" +
                "                <scope>import</scope>\n" +
                "                <type>pom</type>\n" +
                "            </dependency>\n" +
                "        </dependencies>\n" +
                "    </dependencyManagement>\n" +
                "    <dependencies>\n" +
                "        <dependency>\n" +
                "            <groupId>org.camunda.bpm.springboot</groupId>\n" +
                "            <artifactId>camunda-bpm-spring-boot-starter-rest</artifactId>\n" +
                "        </dependency>\n" +
                "        <dependency>\n" +
                "            <groupId>org.springframework</groupId>\n" +
                "            <artifactId>spring-context</artifactId>\n" +
                "        </dependency>\n" +
                "        <dependency>\n" +
                "            <groupId>org.camunda.bpm.springboot</groupId>\n" +
                "            <artifactId>camunda-bpm-spring-boot-starter-webapp</artifactId>\n" +
                "        </dependency>\n" +
                "        <dependency>\n" +
                "            <groupId>org.camunda.bpm</groupId>\n" +
                "            <artifactId>camunda-engine-plugin-spin</artifactId>\n" +
                "        </dependency>\n" +
                "        <dependency>\n" +
                "            <groupId>org.camunda.spin</groupId>\n" +
                "            <artifactId>camunda-spin-dataformat-all</artifactId>\n" +
                "        </dependency>\n" +
                "        <dependency>\n" +
                "            <groupId>org.postgresql</groupId>\n" +
                "            <artifactId>postgresql</artifactId>\n" +
                "            <scope>runtime</scope>\n" +
                "        </dependency>\n" +
                "        <!-- H2 Database Dependency -->\n" +
                "        <dependency>\n" +
                "            <groupId>com.h2database</groupId>\n" +
                "            <artifactId>h2</artifactId>\n" +
                "            <scope>runtime</scope>\n" +
                "        </dependency>\n" +
                "        <dependency>\n" +
                "            <groupId>org.springframework.boot</groupId>\n" +
                "            <artifactId>spring-boot-starter-jdbc</artifactId>\n" +
                "        </dependency>\n" +
                "        <!-- Swagger Dependencies -->\n" +
                "        <dependency>\n" +
                "            <groupId>org.camunda.bpm.extension.swagger</groupId>\n" +
                "            <artifactId>camunda-bpm-swagger-json</artifactId>\n" +
                "            <version>7.8.0</version>\n" +
                "        </dependency>\n" +
                "        <dependency>\n" +
                "            <groupId>org.webjars</groupId>\n" +
                "            <artifactId>swagger-ui</artifactId>\n" +
                "            <version>3.1.4</version>\n" +
                "        </dependency>\n" +
                "        <!-- Email Sender Dependency -->\n" +
                "        <dependency>\n" +
                "            <groupId>org.springframework.boot</groupId>\n" +
                "            <artifactId>spring-boot-starter-mail</artifactId>\n" +
                "        </dependency>\n" +
                "        <dependency>\n" +
                "            <groupId>org.springframework.boot</groupId>\n" +
                "            <artifactId>spring-boot-starter-amqp</artifactId>\n" +
                "        </dependency>\n" +
                "        <!-- Camunda Connect Dependencies -->\n" +
                "        <dependency>\n" +
                "            <groupId>org.camunda.connect</groupId>\n" +
                "            <artifactId>camunda-connect-core</artifactId>\n" +
                "        </dependency>\n" +
                "        <dependency>\n" +
                "            <groupId>org.camunda.connect</groupId>\n" +
                "            <artifactId>camunda-connect-http-client</artifactId>\n" +
                "        </dependency>\n" +
                "        <dependency>\n" +
                "            <groupId>org.camunda.bpm</groupId>\n" +
                "            <artifactId>camunda-engine-plugin-connect</artifactId>\n" +
                "        </dependency>\n" +
                "        <dependency>\n" +
                "            <groupId>org.springframework.boot</groupId>\n" +
                "            <artifactId>spring-boot-starter-thymeleaf</artifactId>\n" +
                "        </dependency>\n" +
                "    </dependencies>\n" +
                "    <build>\n" +
                "        <plugins>\n" +
                "            <plugin>\n" +
                "                <groupId>org.springframework.boot</groupId>\n" +
                "                <artifactId>spring-boot-maven-plugin</artifactId>\n" +
                "                <version>2.7.9</version>\n" +
                "                <executions>\n" +
                "                    <execution>\n" +
                "                        <goals>\n" +
                "                            <goal>repackage</goal>\n" +
                "                        </goals>\n" +
                "                    </execution>\n" +
                "                </executions>\n" +
                "            </plugin>\n" +
                "        </plugins>\n" +
                "    </build>\n" +
                "</project>";
    }
}
