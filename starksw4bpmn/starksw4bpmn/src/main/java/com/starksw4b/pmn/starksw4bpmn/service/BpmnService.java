package com.starksw4b.pmn.starksw4bpmn.service;

import com.starksw4b.pmn.starksw4bpmn.exception.InvalidBpmnException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class BpmnService {

    // Ruta dentro del proyecto
    private static final String UPLOAD_DIR = "uploads/";

    public void processBpmnFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new InvalidBpmnException("⚠ El archivo BPMN está vacío.");
        }

        // Obtener la ruta absoluta del proyecto y agregar "uploads/"
        String projectDir = System.getProperty("user.dir"); // Directorio raíz del proyecto
        String uploadPath = projectDir + File.separator + UPLOAD_DIR;

        // Crear la carpeta si no existe
        Files.createDirectories(Paths.get(uploadPath));

        // Guardar el archivo en la carpeta "uploads/" dentro del proyecto
        String filePath = uploadPath + File.separator + "proyecto.bpmn";  // Nombre fijo
        File destino = new File(filePath);
        file.transferTo(destino);

        System.out.println("Archivo BPMN guardado en: " + destino.getAbsolutePath());
    }
}