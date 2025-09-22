package com.starksw4b.pmn.starksw4bpmn.facade;

import com.starksw4b.pmn.starksw4bpmn.fileGenerator.internal.DelegateExpressionGeneratorService;
import com.starksw4b.pmn.starksw4bpmn.fileGenerator.internal.JavaClassGeneratorService;
import com.starksw4b.pmn.starksw4bpmn.fileGenerator.internal.JavaDelegateGeneratorService;
import com.starksw4b.pmn.starksw4bpmn.fileGenerator.internal.SendTaskGeneratorService;
import com.starksw4b.pmn.starksw4bpmn.model.TaskModel;
import com.starksw4b.pmn.starksw4bpmn.service.BpmnEngineGeneratorService;
import com.starksw4b.pmn.starksw4bpmn.service.BpmnModifierService;
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
    private final JavaClassGeneratorService javaClassGeneratorService;
    private final BpmnEngineGeneratorService generatorService;
    private final TaskService taskService;
    private final FileCopyService fileCopyService;
    private final BpmnModifierService bpmnModifierService;

    public CamundaEngineFacade(
            JavaDelegateGeneratorService javaDelegateGeneratorService,
            DelegateExpressionGeneratorService delegateExpressionGeneratorService,
            SendTaskGeneratorService sendTaskGeneratorService,
            BpmnEngineGeneratorService generatorService,
            TaskService taskService,
            FileCopyService fileCopyService,
            JavaClassGeneratorService javaClassGeneratorService,
            BpmnModifierService bpmnModifierService
    ) {
        this.javaDelegateGeneratorService = javaDelegateGeneratorService;
        this.delegateExpressionGeneratorService = delegateExpressionGeneratorService;
        this.sendTaskGeneratorService = sendTaskGeneratorService;
        this.generatorService = generatorService;
        this.taskService = taskService;
        this.fileCopyService = fileCopyService;
        this.javaClassGeneratorService = javaClassGeneratorService;
        this.bpmnModifierService = bpmnModifierService;
    }

    public Path generarProyectoCamunda() throws IOException {
        Path proyectoGenerado = generatorService.generateProjectFromTemplate();

        // 1) Ubicar el BPMN más reciente en uploads/
        File folder = new File(System.getProperty("user.dir") + File.separator + "uploads");
        File[] archivos = folder.listFiles((dir, name) -> name.endsWith(".bpmn"));
        if (archivos == null || archivos.length == 0) {
            System.out.println("⚠ No se encontró ningún archivo BPMN en uploads/");
            return proyectoGenerado;
        }
        File archivoMasReciente = Arrays.stream(archivos)
                .max((f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()))
                .orElseThrow();

        // 2) Recorrer TODAS las tareas (el nombre de la tarea en el BPMN es task.getName())
        List<TaskModel> tasks = taskService.getAllTasks();
        for (TaskModel task : tasks) {
            if (task.getTaskCategory() == null) continue;

            String rawName   = task.getName();                  // nombre exacto en el BPMN (para buscar)
            String className = formatClassName(rawName);        // nombre clase a generar (sin espacios, válido para Java)

            switch (task.getTaskCategory()) {
                case "Java class" -> {
                    // Generar clase
                    javaDelegateGeneratorService.generateJavaDelegateClass(className);
                    // Modificar BPMN por NOMBRE de la tarea (serviceTask o sendTask)
                    int changed = bpmnModifierService.upsertAttrByTaskName(
                            archivoMasReciente,
                            rawName,
                            "camunda:class",
                            "com.example.workflow." + className
                    );
                    if (changed == 0) {
                        System.out.println("⚠ No se encontró tarea con name='" + rawName + "' para camunda:class");
                    }
                }
                case "Delegate expression" -> {
                    // Generar clase (siempre quieres que exista)
                    delegateExpressionGeneratorService.generateDelegateExpressionClass(className);

                    // Convertir el nombre de la clase a camelCase (primera minúscula)
                    String beanName = Character.toLowerCase(className.charAt(0)) + className.substring(1);

                    // Modificar BPMN
                    int changed = bpmnModifierService.upsertAttrByTaskName(
                            archivoMasReciente,
                            rawName,
                            "camunda:delegateExpression",
                            "${" + beanName + "}"
                    );
                    if (changed == 0) {
                        System.out.println("⚠ No se encontró tarea con name='" + rawName + "' para camunda:delegateExpression");
                    }
                }

                default -> { /* otras categorías se ignoran */ }
            }
        }

        // 3) Copiar el BPMN ya modificado al proyecto generado
        fileCopyService.saveBpmnFileFromPath(archivoMasReciente.toPath(), proyectoGenerado);
        System.out.println("✔ BPMN copiado dentro del proyecto generado (ya modificado).");

        return proyectoGenerado;
    }




    public void generarClaseJavaDelegate(String className) throws IOException {
        javaDelegateGeneratorService.generateJavaDelegateClass(className);
    }

    public void generarClaseJavaClass(String className) throws IOException {
        javaClassGeneratorService.generateJavaClass(className);
    }

    public void generarClaseDelegateExpression(String className) throws IOException {
        delegateExpressionGeneratorService.generateDelegateExpressionClass(className);
    }

    public void generarClaseSendTask(String className) throws IOException {
        sendTaskGeneratorService.generateSendTaskClass(className);
    }

    private String formatClassName(String rawName) {
        String[] words = rawName.trim().split("\\s+");
        StringBuilder formatted = new StringBuilder();

        for (String word : words) {
            if (word.length() >= 3) {
                formatted.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.charAt(1))
                        .append(word.charAt(2));
            } else {
                formatted.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() >= 2) {
                    formatted.append(word.charAt(1));
                }
            }
        }

        return formatted.toString();
    }

}
