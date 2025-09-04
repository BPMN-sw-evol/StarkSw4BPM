package com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.generator;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class ViewGeneratorService {

    public void generateViews(Path projectPath) throws IOException {
        // Ruta sin subcarpeta "formulario"
        Path templatesDir = projectPath.resolve("src/main/resources/templates");
        Files.createDirectories(templatesDir);

        // Cambiar nombres a formulario.html y lista.html
        Files.writeString(templatesDir.resolve("formulario.html"), getFormularioHtml());
        Files.writeString(templatesDir.resolve("lista.html"), getListaHtml());

        System.out.println("📄 Templates Thymeleaf generados: formulario.html y lista.html");
    }

    private String getFormularioHtml() {
        return """
                <!DOCTYPE html>
                <html xmlns:th="http://www.thymeleaf.org">
                <head>
                    <title>Formulario</title>
                </head>
                <body>
                <h1>Formulario</h1>

                <form th:object="${formulario}"
                      th:action="@{/formulario}"
                      method="post">
                    <label>Nombre:</label>
                    <input type="text" th:field="*{nombre}" /><br/>
                    <label>Edad:</label>
                    <input type="number" th:field="*{edad}" /><br/>
                    <label>Fecha de nacimiento:</label>
                    <input type="date" th:field="*{fechaNacimiento}" /><br/>
                    <label>Correo:</label>
                    <input type="email" th:field="*{correo}" /><br/>
                    <label>Activo:</label>
                    <input type="checkbox" th:field="*{activo}" /><br/>

                    <!-- Botón “Guardar” siempre -->
                    <button type="submit">Guardar</button>

                    <!-- Botón “Enviar” sólo cuando haya ID -->
                    <button type="submit"
                            th:if="${formulario.id != null}"
                            th:formaction="@{'/formulario/enviar/id/' + ${formulario.id}}">
                        Enviar
                    </button>
                </form>

                <!-- Mensajes -->
                <div th:if="${exito}">
                    <p style="color:green;">Formulario guardado con éxito.</p>
                </div>
                </body>
                </html>
                """;
    }

    private String getListaHtml() {
        return """
                <!DOCTYPE html>
                <html xmlns:th="http://www.thymeleaf.org">
                <head>
                    <title>Lista de Formularios</title>
                </head>
                <body>
                <h1>Lista de Formularios</h1>

                <table border="1">
                    <tr>
                        <th>ID</th>
                        <th>Nombre</th>
                        <th>Edad</th>
                        <th>Fecha de Nacimiento</th>
                        <th>Correo</th>
                        <th>Activo</th>
                        <th>Aprobado</th>
                        <th>Acción</th>
                    </tr>
                    <tr th:each="formulario : ${formularios}">
                        <td th:text="${formulario.id}"></td>
                        <td th:text="${formulario.nombre}"></td>
                        <td th:text="${formulario.edad}"></td>
                        <td th:text="${formulario.fechaNacimiento}"></td>
                        <td th:text="${formulario.correo}"></td>
                        <td th:text="${formulario.activo}"></td>
                        <td th:text="${formulario.aprobado}"></td>
                        <td>
                            <form th:if="${!formulario.aprobado}"
                                  th:action="@{'/formulario/aprobar/id/' + ${formulario.id}}"
                                  method="post">
                                <button type="submit">Aprobar</button>
                            </form>
                            <span th:if="${formulario.aprobado}">Ya aprobado</span>
                        </td>
                    </tr>
                </table>

                </body>
                </html>
                """;
    }
}
