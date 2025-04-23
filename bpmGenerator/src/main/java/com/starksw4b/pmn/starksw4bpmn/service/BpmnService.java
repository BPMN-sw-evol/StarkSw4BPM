package com.starksw4b.pmn.starksw4bpmn.service;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;
import com.starksw4b.pmn.starksw4bpmn.exception.InvalidBpmnException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;

import java.io.FileNotFoundException;

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

    public Map<String, List<String>> getTareasPorTipo(String pathArchivo) throws Exception {
        File archivo = new File(pathArchivo);

        if (!archivo.exists()) {
            throw new FileNotFoundException("El archivo no se encontró: " + pathArchivo);
        }

        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(archivo);

        Map<String, List<String>> tareasPorTipo = new HashMap<>();

        // Tipos de tareas a mapear
        Map<String, Class<? extends Task>> tipos = Map.of(
                "userTask", UserTask.class,
                "serviceTask", ServiceTask.class,
                "sendTask", SendTask.class,
                "receiveTask", ReceiveTask.class,
                "manualTask", ManualTask.class,
                "scriptTask", ScriptTask.class,
                "businessRuleTask", BusinessRuleTask.class
        );

        for (Map.Entry<String, Class<? extends Task>> entry : tipos.entrySet()) {
            String tipo = entry.getKey();
            Class<? extends Task> clase = entry.getValue();

            Collection<? extends Task> tareas = modelInstance.getModelElementsByType(clase);
            List<String> nombres = tareas.stream()
                    .map(Task::getName)
                    .filter(n -> n != null && !n.isBlank())
                    .toList();

            if (!nombres.isEmpty()) {
                tareasPorTipo.put(tipo, nombres);
            }
        }

        return tareasPorTipo;
    }

}