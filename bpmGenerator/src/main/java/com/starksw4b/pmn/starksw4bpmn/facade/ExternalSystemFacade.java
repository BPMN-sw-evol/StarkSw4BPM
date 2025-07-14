package com.starksw4b.pmn.starksw4bpmn.facade;

import com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.*;
import com.starksw4b.pmn.starksw4bpmn.model.TaskModel;
import com.starksw4b.pmn.starksw4bpmn.service.TaskService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Service
public class ExternalSystemFacade {

    private final ExternalProjectGeneratorService externalProjectGeneratorService;
    private final FormClassGeneratorService formClassGeneratorService;
    private final TaskService taskService;

    public ExternalSystemFacade(ExternalProjectGeneratorService externalProjectGeneratorService,
                                FormClassGeneratorService formClassGeneratorService,
                                TaskService taskService) {
        this.externalProjectGeneratorService = externalProjectGeneratorService;
        this.formClassGeneratorService = formClassGeneratorService;
        this.taskService = taskService;
    }

    public void generarSistemaExterno(Path proyectoBase) throws IOException {
        Path externalPath = externalProjectGeneratorService.generateExternalFormProject(proyectoBase.getParent());
        List<TaskModel> userTasks = taskService.getUserTasks();
        for (TaskModel task : userTasks) {
            String className = task.getName().replaceAll("\\s+", "");
            formClassGeneratorService.generateFormController(externalPath, className);
        }
    }
}