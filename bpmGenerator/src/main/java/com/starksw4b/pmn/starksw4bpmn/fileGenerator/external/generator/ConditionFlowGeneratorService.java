package com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.generator;

import com.starksw4b.pmn.starksw4bpmn.model.TaskModel;
import com.starksw4b.pmn.starksw4bpmn.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class ConditionFlowGeneratorService {

    private final TaskRepository taskRepository;

    public ConditionFlowGeneratorService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Genera controladores para cada condición de gateway detectada.
     * Cada variable genera un controlador con métodos que envían su valor al motor Camunda.
     */
    public void generateConditionalControllers(Path projectPath) throws IOException {
        List<TaskModel> conditions = taskRepository.findAll().stream()
                .filter(t -> "gateway".equalsIgnoreCase(t.getTaskCategory()))
                .filter(t -> t.getConditionExpression() != null && !t.getConditionExpression().isBlank())
                .toList();

        if (conditions.isEmpty()) {
            System.out.println("⚠️ No hay condiciones de compuertas para generar controladores.");
            return;
        }

        Path controllerDir = projectPath.resolve("src/main/java/com/form/client/controller");
        Files.createDirectories(controllerDir);

        for (TaskModel cond : conditions) {
            String variable = cond.getVariableName();
            String expected = cond.getExpectedValue();
            String className = capitalize(variable) + "Controller";

            Path filePath = controllerDir.resolve(className + ".java");
            String content = buildControllerContent(className, variable);

            Files.writeString(filePath, content);
            System.out.println("✅ Generado controlador de condición: " + className);
        }
    }

    /**
     * Construye el contenido del controlador a partir de la variable.
     */
    private String buildControllerContent(String className, String variable) {
        return """
                package com.form.client.controller;

                import org.springframework.web.bind.annotation.*;
                import org.springframework.http.*;
                import org.springframework.web.client.RestTemplate;
                import java.util.*;

                @RestController
                @RequestMapping("/condition/%s")
                public class %s {

                    private final RestTemplate restTemplate = new RestTemplate();
                    private static final String CAMUNDA_API_URL = "http://localhost:8080/engine-rest/";

                    @PostMapping("/true/{processId}")
                    public ResponseEntity<String> approve(@PathVariable String processId) {
                        return sendVariable(processId, true);
                    }

                    @PostMapping("/false/{processId}")
                    public ResponseEntity<String> reject(@PathVariable String processId) {
                        return sendVariable(processId, false);
                    }

                    private ResponseEntity<String> sendVariable(String processId, boolean value) {
                        try {
                            // Construir cuerpo JSON
                            Map<String, Object> requestBody = new HashMap<>();
                            Map<String, Object> variables = new HashMap<>();
                            Map<String, Object> varMap = new HashMap<>();
                            varMap.put("value", value);
                            varMap.put("type", "Boolean");
                            variables.put("%s", varMap);
                            requestBody.put("variables", variables);

                            HttpHeaders headers = new HttpHeaders();
                            headers.setContentType(MediaType.APPLICATION_JSON);
                            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

                            // Ejemplo de llamada (puedes ajustar para buscar taskId real)
                            String url = CAMUNDA_API_URL + "task/" + processId + "/complete";
                            restTemplate.postForEntity(url, requestEntity, Map.class);

                            return ResponseEntity.ok("Variable enviada: %s=" + value);
                        } catch (Exception e) {
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body("Error enviando variable: " + e.getMessage());
                        }
                    }
                }
                """.formatted(variable, className, variable, variable);
    }

    private String capitalize(String text) {
        if (text == null || text.isBlank()) return "Undefined";
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }
}
