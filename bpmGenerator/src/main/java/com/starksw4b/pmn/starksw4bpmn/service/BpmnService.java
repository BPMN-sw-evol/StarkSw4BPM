package com.starksw4b.pmn.starksw4bpmn.service;

import com.starksw4b.pmn.starksw4bpmn.exception.InvalidBpmnException;
import com.starksw4b.pmn.starksw4bpmn.model.FormFieldData;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormData;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormField;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Service
public class BpmnService {

    private static final String UPLOAD_DIR = "uploads/";
    private final TaskService taskService;

    public BpmnService(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Guarda el archivo BPMN y automáticamente registra
     * las tareas y compuertas en la base de datos.
     */
    public String processBpmnFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new InvalidBpmnException("⚠ El archivo BPMN está vacío.");
        }

        String projectDir = System.getProperty("user.dir");
        String uploadPath = projectDir + File.separator + UPLOAD_DIR;
        Files.createDirectories(Paths.get(uploadPath));

        // ⚠ Eliminar todos los BPMN anteriores
        Files.walk(Paths.get(uploadPath))
                .filter(p -> !Files.isDirectory(p) && p.toString().endsWith(".bpmn"))
                .forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException e) {
                        throw new RuntimeException("Error eliminando BPMN anterior: " + p, e);
                    }
                });

        // 📥 Guardar el nuevo archivo BPMN
        String originalFileName = file.getOriginalFilename();
        String filePath = uploadPath + File.separator + originalFileName;

        File destino = new File(filePath);
        file.transferTo(destino);

        System.out.println("✔ Archivo BPMN guardado: " + destino.getAbsolutePath());
        return originalFileName;
    }

    /**
     * Extrae los campos de formularios tipo "Generated Task Forms" para cada UserTask.
     */
    public Map<String, List<FormFieldData>> getEnrichedUserTaskFormFields(String pathArchivo) throws Exception {
        File archivo = new File(pathArchivo);
        if (!archivo.exists()) {
            throw new FileNotFoundException("El archivo no se encontró: " + pathArchivo);
        }

        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(archivo);
        Collection<UserTask> userTasks = modelInstance.getModelElementsByType(UserTask.class);

        Map<String, List<FormFieldData>> formularioPorTarea = new LinkedHashMap<>();

        for (UserTask task : userTasks) {
            ExtensionElements extensionElements = task.getExtensionElements();
            if (extensionElements != null) {
                Collection<CamundaFormData> formularios =
                        extensionElements.getElementsQuery()
                                .filterByType(CamundaFormData.class)
                                .list();

                for (CamundaFormData form : formularios) {
                    List<FormFieldData> campos = new ArrayList<>();
                    for (CamundaFormField field : form.getCamundaFormFields()) {
                        campos.add(new FormFieldData(
                                field.getCamundaId(),
                                field.getCamundaType()
                        ));
                    }

                    if (!campos.isEmpty()) {
                        formularioPorTarea.put(task.getName(), campos);
                    }
                }
            }
        }

        return formularioPorTarea;
    }
}
