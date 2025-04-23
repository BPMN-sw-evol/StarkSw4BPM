package com.form.client.services;

import com.form.client.dto.FormularioDTO;
import com.form.client.model.Formulario;
import com.form.client.repository.FormularioRepository;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FormularioService {

    private final FormularioRepository formularioRepository;
    private final CamundaService camundaService;
    private final TaskService taskService;

    public FormularioService(FormularioRepository formularioRepository,
                             CamundaService camundaService,
                             TaskService taskService) {
        this.formularioRepository = formularioRepository;
        this.camundaService = camundaService;
        this.taskService = taskService;
    }

    public void guardarFormularioYIniciarProceso(FormularioDTO dto) {
        // 1. Guardar en BD
        Formulario formulario = new Formulario();
        formulario.setNombre(dto.getNombre());
        formulario.setEdad(dto.getEdad());
        formulario.setFechaNacimiento(dto.getFechaNacimiento());
        formulario.setCorreo(dto.getCorreo());
        formulario.setActivo(dto.getActivo());

        formularioRepository.save(formulario);

        // 2. Variables para el proceso (¡AGREGA el ID del formulario aquí!)
        Map<String, Object> variables = new HashMap<>();
        variables.put("formularioId", Map.of("value", formulario.getId(), "type", "Long")); // 🔥 ESTE es el nuevo
        variables.put("nombre", Map.of("value", dto.getNombre(), "type", "String"));
        variables.put("edad", Map.of("value", dto.getEdad(), "type", "Integer"));
        variables.put("correo", Map.of("value", dto.getCorreo(), "type", "String"));
        variables.put("activo", Map.of("value", dto.getActivo(), "type", "Boolean"));

        camundaService.iniciarProcesoFormulario(variables);
    }


    public List<FormularioDTO> obtenerTodos() {
        return formularioRepository.findAll().stream()
                .map(this::convertirAFormularioDTO)
                .collect(Collectors.toList());
    }

    private FormularioDTO convertirAFormularioDTO(Formulario formulario) {
        FormularioDTO dto = new FormularioDTO();
        dto.setId(formulario.getId());
        dto.setNombre(formulario.getNombre());
        dto.setEdad(formulario.getEdad());
        dto.setFechaNacimiento(formulario.getFechaNacimiento());
        dto.setCorreo(formulario.getCorreo());
        dto.setActivo(formulario.getActivo());
        dto.setAprobado(formulario.getAprobado());
        return dto;
    }
    public void aprobarPorId(Long id) {
        // 1. Aprueba el formulario en la base de datos
        Formulario formulario = formularioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formulario no encontrado con ID: " + id));
        formulario.setAprobado(true);
        formularioRepository.save(formulario);

        // 2. Buscar tarea activa asociada a ese formulario (por variable de proceso)
        Task tarea = taskService.createTaskQuery()
                .processVariableValueEquals("formularioId", id)
                .active()
                .singleResult(); // ✅ solo una tarea por formulario

        // Log para verificar si se encontró una tarea
        if (tarea != null) {
            System.out.println("Completing task with ID: " + tarea.getId());
            taskService.complete(tarea.getId());
        } else {
            System.out.println("No active task found for form ID: " + id);
            throw new RuntimeException("No se encontró tarea activa para el formulario con ID: " + id);
        }
    }

}


