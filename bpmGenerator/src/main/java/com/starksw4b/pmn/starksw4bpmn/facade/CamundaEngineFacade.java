package com.starksw4b.pmn.starksw4bpmn.facade;

import com.starksw4b.pmn.starksw4bpmn.fileGenerator.internal.DelegateExpressionGeneratorService;
import com.starksw4b.pmn.starksw4bpmn.fileGenerator.internal.JavaDelegateGeneratorService;
import com.starksw4b.pmn.starksw4bpmn.fileGenerator.internal.SendTaskGeneratorService;
import com.starksw4b.pmn.starksw4bpmn.model.TaskModel;
import com.starksw4b.pmn.starksw4bpmn.service.BpmnEngineGeneratorService;
import com.starksw4b.pmn.starksw4bpmn.service.FileCopyService;
import com.starksw4b.pmn.starksw4bpmn.service.TaskService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Service
public class CamundaEngineFacade {

    private final JavaDelegateGeneratorService javaDelegateGeneratorService;
    private final DelegateExpressionGeneratorService delegateExpressionGeneratorService;
    private final SendTaskGeneratorService sendTaskGeneratorService;
    private final BpmnEngineGeneratorService generatorService;
    private final TaskService taskService;
    private final FileCopyService fileCopyService;

    public CamundaEngineFacade(
            JavaDelegateGeneratorService javaDelegateGeneratorService,
            DelegateExpressionGeneratorService delegateExpressionGeneratorService,
            SendTaskGeneratorService sendTaskGeneratorService,
            BpmnEngineGeneratorService generatorService,
            TaskService taskService,
            FileCopyService fileCopyService
    ) {
        this.javaDelegateGeneratorService = javaDelegateGeneratorService;
        this.delegateExpressionGeneratorService = delegateExpressionGeneratorService;
        this.sendTaskGeneratorService = sendTaskGeneratorService;
        this.generatorService = generatorService;
        this.taskService = taskService;
        this.fileCopyService = fileCopyService;
    }

    public Path generarProyectoCamunda() throws IOException {
        Path proyectoGenerado = generatorService.generateProjectFromTemplate();

        // Copiar BPMN
        File folder = new File(System.getProperty("user.dir") + File.separator + "uploads");
        File[] archivos = folder.listFiles((dir, name) -> name.endsWith(".bpmn"));
        if (archivos != null && archivos.length > 0) {
            File archivoMasReciente = Arrays.stream(archivos)
                    .max((f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()))
                    .orElseThrow();

            Path bpmnOriginal = archivoMasReciente.toPath();
            fileCopyService.saveBpmnFileFromPath(bpmnOriginal, proyectoGenerado);
            System.out.println("✔ BPMN copiado dentro del proyecto generado.");
        } else {
            System.out.println("⚠ No se encontró ningún archivo BPMN en uploads/");
        }

        // Generar clases según configuración
        List<TaskModel> serviceTasks = taskService.getServiceTasks();
        for (TaskModel task : serviceTasks) {
            String className = task.getName().replaceAll("\\s+", "");
            switch (task.getTaskCategory()) {
                case "Java class" -> javaDelegateGeneratorService.generateJavaDelegateClass(className);
                case "Delegate expression" -> delegateExpressionGeneratorService.generateDelegateExpressionClass(className);
            }
        }

        return proyectoGenerado;
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
}
