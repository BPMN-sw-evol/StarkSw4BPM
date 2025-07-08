package com.starksw4b.pmn.starksw4bpmn.facade;

import com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.ExternalProjectGeneratorService;
import com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.FormClassGeneratorService;
import com.starksw4b.pmn.starksw4bpmn.fileGenerator.internal.DelegateExpressionGeneratorService;
import com.starksw4b.pmn.starksw4bpmn.fileGenerator.internal.JavaDelegateGeneratorService;
import com.starksw4b.pmn.starksw4bpmn.fileGenerator.internal.SendTaskGeneratorService;
import com.starksw4b.pmn.starksw4bpmn.model.TaskModel;
import com.starksw4b.pmn.starksw4bpmn.service.FileCopyService;
import com.starksw4b.pmn.starksw4bpmn.service.BpmnEngineGeneratorService;
import com.starksw4b.pmn.starksw4bpmn.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Service
public class SystemGeneratorFacade {

    private final JavaDelegateGeneratorService javaDelegateGeneratorService;
    private final DelegateExpressionGeneratorService delegateExpressionGeneratorService;
    private final SendTaskGeneratorService sendTaskGeneratorService;
    private final BpmnEngineGeneratorService generatorService;
    private final TaskService taskService;
    private final FileCopyService fileCopyService;
    private final ExternalProjectGeneratorService externalProjectGeneratorService;
    private final FormClassGeneratorService formClassGeneratorService;

    @Autowired
    public SystemGeneratorFacade(
            JavaDelegateGeneratorService javaDelegateGeneratorService,
            DelegateExpressionGeneratorService delegateExpressionGeneratorService,
            SendTaskGeneratorService sendTaskGeneratorService,
            BpmnEngineGeneratorService generatorService,
            TaskService taskService,
            FileCopyService fileCopyService,
            ExternalProjectGeneratorService externalProjectGeneratorService,
            FormClassGeneratorService formClassGeneratorService
    ) {
        this.javaDelegateGeneratorService = javaDelegateGeneratorService;
        this.delegateExpressionGeneratorService = delegateExpressionGeneratorService;
        this.sendTaskGeneratorService = sendTaskGeneratorService;
        this.generatorService = generatorService;
        this.taskService = taskService;
        this.fileCopyService = fileCopyService;
        this.externalProjectGeneratorService = externalProjectGeneratorService;
        this.formClassGeneratorService = formClassGeneratorService;
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
        // Usar el archivo más reciente en la carpeta uploads
        File folder = new File(System.getProperty("user.dir") + File.separator + "uploads");
        File[] archivos = folder.listFiles((dir, name) -> name.endsWith(".bpmn"));

        if (archivos != null && archivos.length > 0) {
            File archivoMasReciente = Arrays.stream(archivos)
                    .max( (f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()))
                    .orElseThrow();

            Path bpmnOriginal = archivoMasReciente.toPath();
            fileCopyService.saveBpmnFileFromPath(bpmnOriginal, proyectoGenerado);

            System.out.println("✔ BPMN copiado dentro del proyecto generado.");
        } else {
            System.out.println("⚠ No se encontró ningún archivo BPMN en uploads/");
        }

        // Paso 3: generar clases según configuración
        List<TaskModel> serviceTasks = taskService.getServiceTasks();
        System.out.println("Tareas de servicio recuperadas: " + serviceTasks);

        for (TaskModel task : serviceTasks) {
            String className = task.getName().replaceAll("\\s+", "");
            switch (task.getTaskCategory()) {
                case "Java class" -> javaDelegateGeneratorService.generateJavaDelegateClass(className);
                case "Delegate expression" -> delegateExpressionGeneratorService.generateDelegateExpressionClass(className);
            }
        }

        System.out.println("✔ Proyecto modificado sobre plantilla en: " + proyectoGenerado.toAbsolutePath());

        // Paso 4: generar sistema externo para formularios
        Path externalProjectPath = externalProjectGeneratorService.generateExternalFormProject(proyectoGenerado.getParent());

        // Paso 4.1: generar formularios de usuario (uno por tarea)
        List<TaskModel> userTasks = taskService.getUserTasks();
        System.out.println("Tareas de usuario recuperadas: " + userTasks);

        for (TaskModel task : userTasks) {
            String className = task.getName().replaceAll("\\s+", "");
            formClassGeneratorService.generateFormController(externalProjectPath, className);
        }

        System.out.println("✔ Sistema de formularios generado en: " + externalProjectPath.toAbsolutePath());
    }
}