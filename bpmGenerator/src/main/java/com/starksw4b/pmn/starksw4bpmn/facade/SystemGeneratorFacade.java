package com.starksw4b.pmn.starksw4bpmn.facade;

import com.starksw4b.pmn.starksw4bpmn.fileGenerator.DelegateExpressionGeneratorService;
import com.starksw4b.pmn.starksw4bpmn.fileGenerator.JavaDelegateGeneratorService;
import com.starksw4b.pmn.starksw4bpmn.fileGenerator.SendTaskGeneratorService;
import com.starksw4b.pmn.starksw4bpmn.model.TaskModel;
import com.starksw4b.pmn.starksw4bpmn.service.FileCopyService;
import com.starksw4b.pmn.starksw4bpmn.service.BpmnEngineGeneratorService;
import com.starksw4b.pmn.starksw4bpmn.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class SystemGeneratorFacade {

    private final JavaDelegateGeneratorService javaDelegateGeneratorService;
    private final DelegateExpressionGeneratorService delegateExpressionGeneratorService;
    private final SendTaskGeneratorService sendTaskGeneratorService;
    private final BpmnEngineGeneratorService generatorService;
    private final TaskService taskService;
    private final FileCopyService fileCopyService;

    @Autowired
    public SystemGeneratorFacade(JavaDelegateGeneratorService javaDelegateGeneratorService,
                                 DelegateExpressionGeneratorService delegateExpressionGeneratorService,
                                 SendTaskGeneratorService sendTaskGeneratorService,
                                 BpmnEngineGeneratorService generatorService,
                                 TaskService taskService,
                                 FileCopyService fileCopyService) {
        this.javaDelegateGeneratorService = javaDelegateGeneratorService;
        this.delegateExpressionGeneratorService = delegateExpressionGeneratorService;
        this.sendTaskGeneratorService = sendTaskGeneratorService;
        this.generatorService = generatorService;
        this.taskService = taskService;
        this.fileCopyService = fileCopyService;
    }

    public void generarClaseJavaDelegate(String className) throws IOException {
        javaDelegateGeneratorService.generateJavaDelegateClass(className);
    }

    public void generarClaseDelegateExpression(String className) throws IOException {
        delegateExpressionGeneratorService.generateDelegateExpressionClass(className);
    }

    public void generarClaseSendTask(String className) throws IOException {
        sendTaskGeneratorService.generateSendTaskClass(className);
    }

    public void generarClasesDesdeConfiguracion() throws IOException {
        // Paso 1: generar el proyecto base
        Path proyectoGenerado = generatorService.generateProjectFromTemplate();

        // Paso 2: copiar el archivo BPMN al proyecto generado
        Path bpmnOriginal = Paths.get(System.getProperty("user.dir"), "uploads", "proyecto.bpmn");
        if (Files.exists(bpmnOriginal)) {
            fileCopyService.saveBpmnFileFromPath(bpmnOriginal, proyectoGenerado);
            System.out.println("✔ BPMN copiado dentro del proyecto generado.");
        } else {
            System.out.println("⚠ No se encontró el archivo BPMN en uploads/proyecto.bpmn");
        }

        // Paso 3: generar clases según configuración
        List<TaskModel> serviceTasks = taskService.getServiceTasks();
        for (TaskModel task : serviceTasks) {
            String className = task.getName().replaceAll("\\s+", "");
            switch (task.getTaskCategory()) {
                case "CRUD" -> javaDelegateGeneratorService.generateJavaDelegateClass(className);
                case "Solo consulta" -> delegateExpressionGeneratorService.generateDelegateExpressionClass(className);
            }
        }

        System.out.println("✔ Proyecto modificado sobre plantilla en: " + proyectoGenerado.toAbsolutePath());
    }
}
