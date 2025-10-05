package com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.generator;

import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class ServiceGeneratorService {

    public void generateServices(Path projectPath) throws IOException {
        Path servicesDir = getServicesDirectory(projectPath);
        Files.createDirectories(servicesDir);

        generateCamundaService(servicesDir);
        generateFormularioService(servicesDir);
    }

    private Path getServicesDirectory(Path projectPath) {
        return projectPath.resolve("src/main/java/com/form/client/services".replace(".", File.separator));
    }

    private void generateCamundaService(Path dir) throws IOException {
        String content = """
                package com.form.client.services;

                import org.springframework.http.*;
                import org.springframework.stereotype.Service;
                import org.springframework.web.client.RestTemplate;

                import java.util.HashMap;
                import java.util.Map;

                @Service
                public class CamundaService {

                    private final RestTemplate restTemplate;

                    public CamundaService(RestTemplate restTemplate) {
                        this.restTemplate = restTemplate;
                    }

                    public void iniciarProceso(String processDefinitionKey, Map<String, Object> variables) {
                        Map<String, Object> requestBody = new HashMap<>();
                        requestBody.put("variables", variables);

                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);

                        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

                        String url = "http://localhost:8080/engine-rest/process-definition/key/" 
                                     + processDefinitionKey + "/start";

                        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

                        System.out.println("Respuesta de Camunda: " + response.getStatusCode());
                        System.out.println("Body: " + response.getBody());
                    }

                    public void completarTarea(String taskId, Map<String, Object> variables) {
                        String url = "http://localhost:8080/engine-rest/task/" + taskId + "/complete";
                        restTemplate.postForEntity(url, Map.of("variables", variables), Void.class);
                        System.out.println("✅ Tarea completada en Camunda: " + taskId);
                    }
                }
                """;

        Files.writeString(dir.resolve("CamundaService.java"), content);
        System.out.println("🧩 CamundaService generado.");
    }

    private void generateFormularioService(Path dir) throws IOException {
        String content = """
            package com.form.client.services;

            import com.fasterxml.jackson.databind.JsonNode;
            import com.form.client.dto.FormularioDTO;
            import com.form.client.model.Formulario;
            import com.form.client.repository.FormularioRepository;
            import org.springframework.beans.factory.annotation.Value;
            import org.springframework.http.ResponseEntity;
            import org.springframework.stereotype.Service;
            import org.springframework.web.client.RestTemplate;

            import java.util.ArrayList;
            import java.util.List;
            import java.util.Map;
            import java.util.stream.Collectors;

            @Service
            public class FormularioService {

                private final FormularioRepository formularioRepository;
                private final RestTemplate restTemplate;
                private final String camundaRestUrl;
                private final String processDefinitionKey;

                public FormularioService(FormularioRepository formularioRepository,
                                         RestTemplate restTemplate,
                                         @Value("${camunda.rest.url:http://localhost:8080/engine-rest}") String camundaRestUrl,
                                         @Value("${camunda.process.key:Process_19oqlmt}") String processDefinitionKey) {
                    this.formularioRepository = formularioRepository;
                    this.restTemplate = restTemplate;
                    this.camundaRestUrl = camundaRestUrl;
                    this.processDefinitionKey = processDefinitionKey;
                }

                // === Primer userTask: guardar + iniciar proceso ===
                public FormularioDTO guardarFormularioYIniciarProceso(FormularioDTO dto) {
                    Formulario entidad = new Formulario();
                    entidad.setNombre(dto.getNombre());
                    entidad.setEdad(dto.getEdad());
                    entidad.setCorreo(dto.getCorreo());
                    entidad.setActivo(dto.getActivo()); // 👈 usamos getActivo
                    formularioRepository.save(entidad);

                    Map<String,Object> variables = Map.of(
                            "formularioId", Map.of("value", entidad.getId(), "type", "Long"),
                            "nombre",       Map.of("value", dto.getNombre(), "type", "String"),
                            "edad",         Map.of("value", dto.getEdad(),   "type", "Integer"),
                            "correo",       Map.of("value", dto.getCorreo(), "type", "String"),
                            "activo",       Map.of("value", dto.getActivo(), "type", "Boolean")
                    );

                    restTemplate.postForEntity(
                            camundaRestUrl + "/process-definition/key/" + processDefinitionKey + "/start",
                            Map.of("variables", variables),
                            JsonNode.class
                    );

                    dto.setId(entidad.getId());
                    return dto;
                }

                // === Listado completo desde BD ===
                public List<FormularioDTO> obtenerTodos() {
                    return formularioRepository.findAll()
                            .stream()
                            .map(this::convertirAFormularioDTO)
                            .collect(Collectors.toList());
                }

                // === Listar pendientes en Camunda por taskDefinitionKey ===
                public List<FormularioDTO> obtenerPendientesPorTask(String taskDefinitionKey) {
                    String queryUrl = camundaRestUrl + "/task"
                            + "?taskDefinitionKey=" + taskDefinitionKey
                            + "&active=true";

                    ResponseEntity<JsonNode> resp = restTemplate.getForEntity(queryUrl, JsonNode.class);
                    JsonNode tasks = resp.getBody();

                    List<FormularioDTO> pendientes = new ArrayList<>();

                    if (tasks != null && tasks.isArray()) {
                        for (JsonNode task : tasks) {
                            String taskId = task.get("id").asText();

                            ResponseEntity<JsonNode> varsResp = restTemplate.getForEntity(
                                    camundaRestUrl + "/task/" + taskId + "/variables",
                                    JsonNode.class
                            );

                            JsonNode vars = varsResp.getBody();
                            if (vars != null && vars.has("formularioId")) {
                                Long formularioId = vars.get("formularioId").get("value").asLong();

                                formularioRepository.findById(formularioId).ifPresent(f -> {
                                    FormularioDTO dto = convertirAFormularioDTO(f);
                                    dto.setTaskId(taskId); // vinculamos con Camunda
                                    pendientes.add(dto);
                                });
                            }
                        }
                    }
                    return pendientes;
                }

                // === Completar tarea por taskId (aprobar) ===
                public void aprobarPorTaskId(String taskId) {
                    restTemplate.postForEntity(
                        camundaRestUrl + "/task/" + taskId + "/complete",
                        Map.of(),
                        Void.class
                    );
                }

                // === Enviar: avanza la tarea activa por formularioId ===
                public void enviarPorId(Long id) {
                    String queryUrl = camundaRestUrl + "/task"
                            + "?variables=formularioId_eq_" + id
                            + "&active=true";

                    ResponseEntity<JsonNode> resp = restTemplate.getForEntity(queryUrl, JsonNode.class);
                    JsonNode tasks = resp.getBody();

                    if (tasks != null && tasks.isArray() && tasks.size() > 0) {
                        String taskId = tasks.get(0).get("id").asText();
                        restTemplate.postForEntity(
                                camundaRestUrl + "/task/" + taskId + "/complete",
                                Map.of(),
                                Void.class
                        );
                    } else {
                        throw new RuntimeException("No se encontró tarea activa para el formulario con ID: " + id);
                    }
                }

                // === Obtener un formulario por ID de la BD ===
                public FormularioDTO obtenerPorId(Long id) {
                    Formulario entidad = formularioRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Formulario no encontrado con ID: " + id));
                    return convertirAFormularioDTO(entidad);
                }

                private FormularioDTO convertirAFormularioDTO(Formulario f) {
                    FormularioDTO dto = new FormularioDTO();
                    dto.setId(f.getId());
                    dto.setNombre(f.getNombre());
                    dto.setEdad(f.getEdad());
                    dto.setCorreo(f.getCorreo());
                    dto.setActivo(f.isActivo()); // 👈 corregido
                    dto.setAprobado(f.isAprobado());
                    return dto;
                }
            }
            """;

        Files.writeString(dir.resolve("FormularioService.java"), content);
        System.out.println("📩 FormularioService generado.");
    }
}