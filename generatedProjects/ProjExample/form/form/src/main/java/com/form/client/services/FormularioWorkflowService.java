package com.form.client.services;

import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FormularioWorkflowService {

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    public void completarTareaFormulario(Long formularioId) {
        try {
            // 1. Buscar el proceso por variable formularioId
            ProcessInstance proceso = runtimeService.createProcessInstanceQuery()
                    .variableValueEquals("formularioId", formularioId)
                    .singleResult();

            if (proceso == null) {
                System.out.println("No se encontró proceso con formularioId: " + formularioId);
                return;
            }

            System.out.println("Proceso encontrado: " + proceso.getId());

            // 2. Buscar la tarea activa asociada a ese proceso
            Task tarea = taskService.createTaskQuery()
                    .processInstanceId(proceso.getId())
                    .active()
                    .singleResult();

            if (tarea == null) {
                System.out.println("No se encontró tarea activa para el proceso.");
                return;
            }

            System.out.println("Tarea encontrada: " + tarea.getId() + " - " + tarea.getName());

            // 3. Completar la tarea
            taskService.complete(tarea.getId());
            System.out.println("Tarea completada correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al completar la tarea.");
        }
    }
}

