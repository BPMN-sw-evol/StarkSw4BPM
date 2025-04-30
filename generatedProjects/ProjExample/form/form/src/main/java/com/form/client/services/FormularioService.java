package com.form.client.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.form.client.dto.FormularioDTO;
import com.form.client.model.Formulario;
import com.form.client.repository.FormularioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FormularioService {

    private final FormularioRepository formularioRepository;
    private final RestTemplate restTemplate;
    private final String camundaRestUrl;
    private final String processDefinitionKey = "Process_19oqlmt"; // ajusta al ID de tu proceso

    public FormularioService(FormularioRepository formularioRepository,
                             RestTemplate restTemplate,
                             @Value("${camunda.rest.url}") String camundaRestUrl) {
        this.formularioRepository = formularioRepository;
        this.restTemplate = restTemplate;
        this.camundaRestUrl = camundaRestUrl;
    }

    public FormularioDTO guardarFormularioYIniciarProceso(FormularioDTO dto) {
        // 1. Guardar en BD
        Formulario entidad = new Formulario();
        entidad.setNombre(dto.getNombre());
        entidad.setEdad(dto.getEdad());
        entidad.setFechaNacimiento(dto.getFechaNacimiento());
        entidad.setCorreo(dto.getCorreo());
        entidad.setActivo(dto.getActivo());
        formularioRepository.save(entidad);

        // 2. Iniciar proceso en Camunda
        Map<String,Object> variables = Map.of(
                "formularioId", Map.of("value", entidad.getId(), "type", "Long"),
                "nombre",       Map.of("value", dto.getNombre(),   "type", "String"),
                "edad",         Map.of("value", dto.getEdad(),     "type", "Integer"),
                "correo",       Map.of("value", dto.getCorreo(),   "type", "String"),
                "activo",       Map.of("value", dto.getActivo(),   "type", "Boolean")
        );
        restTemplate.postForEntity(
                camundaRestUrl + "/process-definition/key/" + processDefinitionKey + "/start",
                Map.of("variables", variables),
                JsonNode.class
        );

        // 3. Rellenamos el ID en el DTO y lo devolvemos
        dto.setId(entidad.getId());
        return dto;
    }

    public List<FormularioDTO> obtenerTodos() {
        return formularioRepository.findAll()
                .stream()
                .map(this::convertirAFormularioDTO)
                .collect(Collectors.toList());
    }

    private FormularioDTO convertirAFormularioDTO(Formulario f) {
        FormularioDTO dto = new FormularioDTO();
        dto.setId(f.getId());
        dto.setNombre(f.getNombre());
        dto.setEdad(f.getEdad());
        dto.setFechaNacimiento(f.getFechaNacimiento());
        dto.setCorreo(f.getCorreo());
        dto.setActivo(f.getActivo());
        dto.setAprobado(f.getAprobado());
        return dto;
    }

    public void aprobarPorId(Long id) {
        // 1. Actualizar DB
        Formulario formulario = formularioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No encontrado ID: " + id));
        formulario.setAprobado(true);
        formularioRepository.save(formulario);

        // 2. Buscar tarea activa vía REST
        String queryUrl = camundaRestUrl + "/task"
                + "?variables=formularioId_eq_" + id
                + "&active=true";
        ResponseEntity<JsonNode> resp = restTemplate.getForEntity(queryUrl, JsonNode.class);
        JsonNode tasks = resp.getBody();
        if (tasks != null && tasks.isArray() && tasks.size() > 0) {
            String taskId = tasks.get(0).get("id").asText();
            // 3. Completar tarea
            restTemplate.postForEntity(
                    camundaRestUrl + "/task/" + taskId + "/complete",
                    Map.of(), Void.class
            );
        } else {
            throw new RuntimeException(
                    "No se encontró tarea activa para el formulario con ID: " + id
            );
        }
    }

    public FormularioDTO obtenerPorId(Long id) {
        Formulario entidad = formularioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formulario no encontrado con ID: " + id));
        return convertirAFormularioDTO(entidad);
    }
}
