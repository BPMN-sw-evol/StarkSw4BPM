package com.starksw4b.pmn.starksw4bpmn.service;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;

@Service
public class MainClassService {

    // Ruta del archivo DemoApplication.java en el proyecto generado
    private static final String MAIN_CLASS_PATH = "generated-project/src/main/java/com/example/demo/DemoApplication.java";

    public void modifyMainClass() throws IOException {
        // Verificar si el archivo DemoApplication.java existe
        Path mainClassPath = Paths.get(MAIN_CLASS_PATH);
        if (!Files.exists(mainClassPath)) {
            throw new FileNotFoundException("No se encuentra el archivo DemoApplication.java en la ruta: " + MAIN_CLASS_PATH);
        }

        // El contenido nuevo para DemoApplication.java
        String newMainClassContent = getNewMainClassContent();

        // Escribir el nuevo contenido en DemoApplication.java
        try (BufferedWriter writer = Files.newBufferedWriter(mainClassPath, StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write(newMainClassContent);
            System.out.println("Archivo DemoApplication.java actualizado exitosamente.");
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Error al modificar el archivo DemoApplication.java: " + e.getMessage());
        }
    }

    // Método que devuelve el nuevo contenido para DemoApplication.java
    private String getNewMainClassContent() {
        return "package com.example.demo;\n\n" +
                "import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;\n" +
                "import org.springframework.boot.SpringApplication;\n" +
                "import org.springframework.boot.autoconfigure.SpringBootApplication;\n\n" +
                "@SpringBootApplication  // Indica que es una aplicación Spring Boot\n" +
                "@EnableProcessApplication  // Activa Camunda BPM en el proyecto\n" +
                "public class DemoApplication {\n" +
                "    public static void main(String[] args) {\n" +
                "        SpringApplication.run(DemoApplication.class, args);\n" +
                "    }\n" +
                "}\n";
    }
}
