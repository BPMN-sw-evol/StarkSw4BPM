package com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.generator;

import com.starksw4b.pmn.starksw4bpmn.model.TaskModel;
import com.starksw4b.pmn.starksw4bpmn.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ViewGeneratorService {

    private final TaskRepository taskRepository;

    public ViewGeneratorService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void generateViews(Path projectPath, List<Map<String, String>> userTasks) throws IOException {
        Path templatesPath = projectPath.resolve("src/main/resources/templates");
        Files.createDirectories(templatesPath);

        // === 1️⃣ Obtener gateways únicos por variable ===
        List<TaskModel> gateways = taskRepository.findAll().stream()
                .filter(t -> "gateway".equalsIgnoreCase(t.getTaskCategory()))
                .filter(t -> t.getVariableName() != null && !t.getVariableName().isBlank())
                .collect(Collectors.toMap(TaskModel::getVariableName, t -> t, (a, b) -> a)) // evita duplicados
                .values().stream().toList();

        // === 2️⃣ Construir botones únicos ===
        StringBuilder decisionButtons = new StringBuilder();
        if (!gateways.isEmpty()) {
            decisionButtons.append("<hr/>\n<h3>Decisiones del proceso</h3>\n");
            for (TaskModel gw : gateways) {
                String variable = gw.getVariableName();
                decisionButtons.append("""
                    <div class='button-group'>
                        <form th:action="@{'/formulario/condition/%s/true/' + ${f.id}}" method="post" style="display:inline;">
                            <button type="submit" class="btn-success">Aprobar (%s=true)</button>
                        </form>
                        <form th:action="@{'/formulario/condition/%s/false/' + ${f.id}}" method="post" style="display:inline;">
                            <button type="submit" class="btn-danger">Rechazar (%s=false)</button>
                        </form>
                    </div>
                    """.formatted(variable, variable, variable, variable));
            }
        }

        // === 3️⃣ Generar vista principal ===
        String formularioHtml = """
        <!DOCTYPE html>
        <html xmlns:th="http://www.thymeleaf.org">
        <head>
            <meta charset="UTF-8">
            <title>Formulario de Solicitud</title>
            <style>
                body { font-family: Arial, sans-serif; margin: 40px; }
                input, button { margin-top: 5px; padding: 6px; }
                .btn-primary { background-color: #4CAF50; color: white; border: none; padding: 8px 16px; margin-right: 5px; }
                .btn-secondary { background-color: #2196F3; color: white; border: none; padding: 8px 16px; }
            </style>
        </head>
        <body>
            <h1>Formulario de Solicitud</h1>

            <form th:action="@{/formulario/guardar}" th:object="${formulario}" method="post">
                <label>Nombre: </label>
                <input type="text" th:field="*{nombre}" /><br/>
                <label>Edad: </label>
                <input type="number" th:field="*{edad}" /><br/>
                <label>Correo: </label>
                <input type="email" th:field="*{correo}" /><br/>
                <label>Activo: </label>
                <input type="checkbox" th:field="*{activo}" /><br/><br/>
                <button type="submit" class="btn-primary">Guardar</button>
            </form>

            <div th:if="${formulario.id != null}">
                <hr/>
                <h3>Acción del Proceso</h3>
                <form th:action="@{/formulario/enviar/id/{id}(id=${formulario.id})}" method="post">
                    <button type="submit" class="btn-secondary">Enviar</button>
                </form>
            </div>
        </body>
        </html>
        """;
        Files.writeString(templatesPath.resolve("formulario.html"), formularioHtml);

        // === 4️⃣ Vistas secundarias ===
        if (userTasks == null || userTasks.isEmpty()) {
            System.out.println("⚠ No se encontraron user tasks para generar vistas adicionales.");
            return;
        }

        for (int i = 0; i < userTasks.size(); i++) {
            Map<String, String> task = userTasks.get(i);
            String taskName = task.get("name");
            String titulo = "Tarea: " + taskName;
            String fileName = "task" + (i + 1) + ".html";

            String viewHtml = """
            <!DOCTYPE html>
            <html xmlns:th="http://www.thymeleaf.org">
            <head>
                <meta charset="UTF-8">
                <title>%s</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 40px; }
                    table { border-collapse: collapse; width: 100%%; }
                    th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
                    button { padding: 6px 10px; margin-right: 5px; }
                    .btn-success { background-color: #4CAF50; color: white; border: none; }
                    .btn-danger { background-color: #f44336; color: white; border: none; }
                </style>
            </head>
            <body>
                <h1>%s</h1>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Nombre</th>
                            <th>Edad</th>
                            <th>Correo</th>
                            <th>Acción</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr th:each="f : ${formularios}">
                            <td th:text="${f.id}"></td>
                            <td th:text="${f.nombre}"></td>
                            <td th:text="${f.edad}"></td>
                            <td th:text="${f.correo}"></td>
                            <td>
                                %s
                            </td>
                        </tr>
                    </tbody>
                </table>
            </body>
            </html>
            """.formatted(titulo, titulo,
                    gateways.isEmpty()
                            ? """
                                <form th:action="@{'/formulario/enviar/id/' + ${f.id}}" method="post">
                                    <button type="submit">Enviar</button>
                                </form>
                              """
                            : decisionButtons.toString());

            Files.writeString(templatesPath.resolve(fileName), viewHtml);
        }

        System.out.println("✅ Vistas generadas correctamente en: " + templatesPath);
    }
}
