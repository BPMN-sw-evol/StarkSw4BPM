package com.starksw4b.pmn.starksw4bpmn.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileCopyService {

    // Ruta donde se encuentra el proyecto copiado (este sería un ejemplo, ajustalo a tu caso)
    private static final String GENERATED_PROJECTS_DIR = "C:/devs/StarkSw4BPM/generatedProjects/generatedproject/src/main/resources/";

    public void saveBpmnFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("El archivo BPMN está vacío.");
        }

        // Ruta donde se guardará el archivo .bpmn
        Path resourcesPath = Paths.get(GENERATED_PROJECTS_DIR);

        // Verificar si la carpeta 'resources' existe, si no existe, crearla
        if (!Files.exists(resourcesPath)) {
            Files.createDirectories(resourcesPath);  // Crear la carpeta 'resources' si no existe
            System.out.println("Carpeta 'resources' creada: " + resourcesPath.toAbsolutePath());
        }

        // Eliminar cualquier archivo .bpmn existente
        Path existingBpmnPath = resourcesPath.resolve("proyecto.bpmn");
        if (Files.exists(existingBpmnPath)) {
            Files.delete(existingBpmnPath);  // Eliminar el archivo .bpmn existente
            System.out.println("Archivo BPMN existente eliminado: " + existingBpmnPath.toAbsolutePath());
        }

        // Guardar el nuevo archivo .bpmn
        File destinationFile = new File(existingBpmnPath.toString());
        file.transferTo(destinationFile);  // Guardar el archivo en la carpeta 'resources'

        System.out.println("Archivo BPMN guardado en: " + destinationFile.getAbsolutePath());
    }
}
