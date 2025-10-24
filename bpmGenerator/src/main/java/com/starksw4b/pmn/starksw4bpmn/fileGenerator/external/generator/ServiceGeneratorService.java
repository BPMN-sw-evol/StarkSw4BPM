package com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.generator;

import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class ServiceGeneratorService {

    public void generateServices(Path projectPath, String processId) throws IOException {
        Path servicesDir = getServicesDirectory(projectPath);
        Files.createDirectories(servicesDir);

        generateCamundaService(servicesDir);
        generateFormularioService(servicesDir, processId);
    }

    private Path getServicesDirectory(Path projectPath) {
        return projectPath.resolve("src/main/java/com/form/client/services".replace(".", File.separator));
    }

    /**
     * ✅ Genera un CamundaService completamente funcional con soporte para processInstanceId y decisiones.
     */
    private void generateCamundaService(Path dir) throws IOException {
        String content = """
            package com.form.client.services;

            import org.springframework.http.*;
            import org.springframework.stereotype.Service;
            import org.springframework.web.client.RestTemplate;
            import java.util.*;

            @Service
            public class CamundaService {

                private final RestTemplate restTemplate;

                public CamundaService(RestTemplate restTemplate) {
                    this.restTemplate = restTemplate;
                }

                /**
                 * 🔹 Inicia una instancia de proceso con variables iniciales y devuelve el ID de la instancia.
                 */
                public String iniciarProceso(String processDefinitionKey, Map<String, Object> variables) {
                    Map<String, Object> body = new HashMap<>();
                    Map<String, Object> vars = new HashMap<>();

                    variables.forEach((key, value) -> {
                        Map<String, Object> var = new HashMap<>();
                        var.put("value", value);
                        vars.put(key, var);
                    });
                    body.put("variables", vars);

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);

                    HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
                    String url = "http://localhost:8080/engine-rest/process-definition/key/" + processDefinitionKey + "/start";

                    ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

                    String processInstanceId = (String) response.getBody().get("id");
                    System.out.println("✅ Proceso iniciado con instancia ID = " + processInstanceId);
                    return processInstanceId;
                }

                /**
                 * 🔹 Completa una tarea por su ID enviando variables.
                 */
                public void completarTarea(String taskId, Map<String, Object> variables) {
                    Map<String, Object> body = new HashMap<>();
                    Map<String, Object> vars = new HashMap<>();

                    variables.forEach((key, value) -> {
                        Map<String, Object> var = new HashMap<>();
                        var.put("value", value);
                        vars.put(key, var);
                    });
                    body.put("variables", vars);

                    String url = "http://localhost:8080/engine-rest/task/" + taskId + "/complete";
                    restTemplate.postForEntity(url, body, Void.class);
                    System.out.println("✅ Tarea completada en Camunda: " + taskId + " con vars=" + variables);
                }

                /**
                 * 🔹 Envía una variable de decisión (true/false) a la instancia de proceso.
                 */
                public void enviarVariableDeDecision(String processInstanceId, Map<String, Object> variables) {
                    try {
                        String taskUrl = "http://localhost:8080/engine-rest/task?processInstanceId=" + processInstanceId;
                        ResponseEntity<Map[]> response = restTemplate.getForEntity(taskUrl, Map[].class);
                        Map[] tareas = response.getBody();

                        if (tareas == null || tareas.length == 0) {
                            System.out.println("⚠ No se encontró tarea activa para la instancia con ID=" + processInstanceId);
                            return;
                        }

                        String taskId = (String) tareas[0].get("id");
                        completarTarea(taskId, variables);
                        System.out.println("📤 Variable enviada al proceso " + processInstanceId + ": " + variables);

                    } catch (Exception e) {
                        System.err.println("❌ Error enviando variable de decisión: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
            """;

        Files.writeString(dir.resolve("CamundaService.java"), content);
        System.out.println("🧩 CamundaService generado con soporte de processInstanceId y decisiones.");
    }

    /**
     * ✅ Genera un FormularioService con persistencia del processInstanceId y envío correcto de tareas.
     */
    private void generateFormularioService(Path dir, String processId) throws IOException {
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
            import java.util.*;
            import java.util.stream.Collectors;

            @Service
            public class FormularioService {

                private final FormularioRepository formularioRepository;
                private final RestTemplate restTemplate;
                private final CamundaService camundaService;
                private final String camundaRestUrl;
                private final String processDefinitionKey;

                public FormularioService(FormularioRepository formularioRepository,
                                         RestTemplate restTemplate,
                                         CamundaService camundaService,
                                         @Value("${camunda.rest.url:http://localhost:8080/engine-rest}") String camundaRestUrl) {
                    this.formularioRepository = formularioRepository;
                    this.restTemplate = restTemplate;
                    this.camundaService = camundaService;
                    this.camundaRestUrl = camundaRestUrl;
                    this.processDefinitionKey = "%s";
                }

                /**
                 * 🔹 Guarda el formulario y crea la instancia Camunda (guardando processInstanceId).
                 */
                public FormularioDTO guardarFormularioYIniciarProceso(FormularioDTO dto) {
                    Formulario entidad = new Formulario();
                    entidad.setNombre(dto.getNombre());
                    entidad.setEdad(dto.getEdad());
                    entidad.setCorreo(dto.getCorreo());
                    entidad.setActivo(dto.getActivo());
                    formularioRepository.save(entidad);

                    Map<String, Object> variables = new HashMap<>();
                    variables.put("formularioId", entidad.getId());
                    variables.put("nombre", dto.getNombre());
                    variables.put("edad", dto.getEdad());
                    variables.put("correo", dto.getCorreo());
                    variables.put("activo", dto.getActivo());

                    String processInstanceId = camundaService.iniciarProceso(processDefinitionKey, variables);
                    entidad.setProcessInstanceId(processInstanceId);
                    formularioRepository.save(entidad);

                    dto.setId(entidad.getId());
                    dto.setProcessInstanceId(processInstanceId);

                    System.out.println("✅ Formulario ID=" + entidad.getId() + " vinculado a instancia Camunda=" + processInstanceId);
                    return dto;
                }

                public List<FormularioDTO> obtenerTodos() {
                    return formularioRepository.findAll()
                            .stream()
                            .map(this::convertirAFormularioDTO)
                            .collect(Collectors.toList());
                }

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
                                    dto.setTaskId(taskId);
                                    pendientes.add(dto);
                                });
                            }
                        }
                    }
                    return pendientes;
                }

                public void aprobarPorTaskId(String taskId) {
                    restTemplate.postForEntity(
                            camundaRestUrl + "/task/" + taskId + "/complete",
                            Map.of(),
                            Void.class
                    );
                }

                public void enviarPorId(Long id) {
                    Formulario formulario = formularioRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Formulario no encontrado con ID: " + id));

                    if (formulario.getProcessInstanceId() == null) {
                        throw new RuntimeException("⚠ El formulario con ID " + id + " no tiene processInstanceId asociado.");
                    }

                    String queryUrl = camundaRestUrl + "/task?processInstanceId=" + formulario.getProcessInstanceId();
                    ResponseEntity<JsonNode> resp = restTemplate.getForEntity(queryUrl, JsonNode.class);
                    JsonNode tasks = resp.getBody();

                    if (tasks != null && tasks.isArray() && tasks.size() > 0) {
                        String taskId = tasks.get(0).get("id").asText();
                        restTemplate.postForEntity(
                                camundaRestUrl + "/task/" + taskId + "/complete",
                                Map.of(),
                                Void.class
                        );
                        System.out.println("✅ Tarea completada para formulario ID=" + id + ", instancia=" + formulario.getProcessInstanceId());
                    } else {
                        throw new RuntimeException("No se encontró tarea activa para la instancia del formulario con ID: " + id);
                    }
                }

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
                    dto.setActivo(f.isActivo());
                    dto.setAprobado(f.isAprobado());
                    dto.setProcessInstanceId(f.getProcessInstanceId());
                    return dto;
                }
            }
            """.formatted(processId);

        Files.writeString(dir.resolve("FormularioService.java"), content);
        System.out.println("📩 FormularioService generado (processKey: " + processId + ")");
    }
}
