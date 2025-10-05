package com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.generator;

import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Service
public class ViewGeneratorService {

    public void generateViews(Path projectPath, List<Map<String, String>> userTasks) throws IOException {
        if (userTasks == null || userTasks.isEmpty()) {
            System.out.println("⚠ No se encontraron user tasks para generar vistas.");
            return;
        }

        Path templatesPath = projectPath.resolve("src/main/resources/templates");
        Files.createDirectories(templatesPath);

        // === Vista principal del formulario ===
        String formularioHtml = """
        <!DOCTYPE html>
        <html xmlns:th="http://www.thymeleaf.org">
        <head>
            <meta charset="UTF-8">
            <title>Formulario</title>
        </head>
        <body>
            <h1>Formulario</h1>
            <form th:action="@{/formulario/guardar}" th:object="${formulario}" method="post">
                <label>Nombre: </label>
                <input type="text" th:field="*{nombre}" /><br/>
                <label>Edad: </label>
                <input type="number" th:field="*{edad}" /><br/>
                <label>Correo: </label>
                <input type="email" th:field="*{correo}" /><br/>
                <label>Activo: </label>
                <input type="checkbox" th:field="*{activo}" /><br/>
                <br/>
                <button type="submit">Guardar</button>
            </form>

            <form th:action="@{/formulario/enviar/id/{id}(id=${formulario.id})}" method="post" th:if="${formulario.id != null}">
                <button type="submit">Enviar</button>
            </form>
        </body>
        </html>
        """;

        Files.writeString(templatesPath.resolve("formulario.html"), formularioHtml);

        // === Vistas de aprobación ===
        for (int i = 1; i < userTasks.size(); i++) {
            Map<String, String> task = userTasks.get(i);
            String taskName = task.get("name"); // Nombre legible
            String fileName = "aprobar-task" + (i + 1) + ".html";
            String titulo = "Pendientes de Aprobación - " + taskName;

            String viewHtml = """
        <!DOCTYPE html>
        <html xmlns:th="http://www.thymeleaf.org">
        <head>
            <title>%s</title>
        </head>
        <body>
            <h1>%s</h1>
            <table border="1">
                <tr>
                    <th>ID</th>
                    <th>Nombre</th>
                    <th>Edad</th>
                    <th>Correo</th>
                    <th>Acción</th>
                </tr>
                <tr th:each="f : ${formularios}">
                    <td th:text="${f.id}"></td>
                    <td th:text="${f.nombre}"></td>
                    <td th:text="${f.edad}"></td>
                    <td th:text="${f.correo}"></td>
                    <td>
                        <form th:action="@{'/formulario/aprobar-task%d/id/' + ${f.taskId}}" method="post">
                            <button type="submit">Aprobar</button>
                        </form>
                    </td>
                </tr>
            </table>
            <a href="/formulario/lista">Volver a la lista general</a>
        </body>
        </html>
        """.formatted(titulo, titulo, i + 1);

            Files.writeString(templatesPath.resolve(fileName), viewHtml);
        }

        System.out.println("✅ Vistas generadas en: " + templatesPath);
    }
}
