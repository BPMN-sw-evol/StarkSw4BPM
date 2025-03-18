package com.starksw4b.pmn.starksw4bpmn.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.stereotype.Service;

@Service
public class FileCopyService {

    public void copyBpmnFile() {
        // Definir rutas
        Path sourcePath = Paths.get("uploads", "proyecto.bpmn"); // Ajusta el nombre del archivo si es necesario
        Path targetDir = Paths.get("generated-project", "src", "main", "resources", "static.bpmn"); // Carpeta destino
        Path targetPath = targetDir.resolve("proyecto.bpmn");

        try {
            // Crear directorio si no existe
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            // Copiar el archivo
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Archivo copiado exitosamente a: " + targetPath);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al copiar el archivo BPMN: " + e.getMessage());
        }
    }
}