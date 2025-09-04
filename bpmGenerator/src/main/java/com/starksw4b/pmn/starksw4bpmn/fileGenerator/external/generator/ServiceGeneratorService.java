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

                    public void iniciarProcesoFormulario(Map<String, Object> variables) {
                        Map<String, Object> requestBody = new HashMap<>();
                        requestBody.put("variables", variables);

                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);

                        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

                        String url = "http://localhost:8080/engine-rest/process-definition/key/Process_19oqlmt/start";

                        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

                        System.out.println("Respuesta de Camunda: " + response.getStatusCode());
                        System.out.println("Body: " + response.getBody());
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
                        Formulario entidad = new Formulario();
                        entidad.setNombre(dto.getNombre());
                        entidad.setEdad(dto.getEdad());
                        entidad.setFechaNacimiento(dto.getFechaNacimiento());
                        entidad.setCorreo(dto.getCorreo());
                        entidad.setActivo(dto.getActivo());
                        formularioRepository.save(entidad);

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
                                    Map.of(), Void.class
                            );
                        } else {
                            throw new RuntimeException("No se encontró tarea activa para el formulario con ID: " + id);
                        }
                    }

                    public void aprobarPorId(Long id) {
                        Formulario formulario = formularioRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("No encontrado ID: " + id));
                        formulario.setAprobado(true);
                        formularioRepository.save(formulario);

                        String queryUrl = camundaRestUrl + "/task"
                                + "?variables=formularioId_eq_" + id
                                + "&active=true";

                        ResponseEntity<JsonNode> resp = restTemplate.getForEntity(queryUrl, JsonNode.class);
                        JsonNode tasks = resp.getBody();
                        if (tasks != null && tasks.isArray() && tasks.size() > 0) {
                            String taskId = tasks.get(0).get("id").asText();
                            restTemplate.postForEntity(
                                    camundaRestUrl + "/task/" + taskId + "/complete",
                                    Map.of(), Void.class
                            );
                        } else {
                            throw new RuntimeException("No se encontró tarea activa para el formulario con ID: " + id);
                        }
                    }

                    public FormularioDTO obtenerPorId(Long id) {
                        Formulario entidad = formularioRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Formulario no encontrado con ID: " + id));
                        return convertirAFormularioDTO(entidad);
                    }
                }
                """;

        Files.writeString(dir.resolve("FormularioService.java"), content);
        System.out.println("📩 FormularioService generado.");
    }
}
