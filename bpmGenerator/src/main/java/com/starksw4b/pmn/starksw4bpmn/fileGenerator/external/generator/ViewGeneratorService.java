package com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.generator;

import com.starksw4b.pmn.starksw4bpmn.model.FormFieldData;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Map;

@Service
public class ViewGeneratorService {

    public void generateViews(Path projectPath, Map<String, List<FormFieldData>> formFields) throws IOException {
        Path templatesDir = projectPath.resolve("src/main/resources/templates");
        Files.createDirectories(templatesDir);

        for (Map.Entry<String, List<FormFieldData>> entry : formFields.entrySet()) {
            String taskName = entry.getKey().replaceAll("\\s+", "").toLowerCase();
            List<FormFieldData> fields = entry.getValue();

            Path formDir = templatesDir.resolve(taskName);
            Files.createDirectories(formDir);

            // Formulario
            String formHtml = generateFormHtml(taskName, fields);
            Files.writeString(formDir.resolve("form.html"), formHtml);

            // Listado
            String listHtml = generateListHtml(taskName, fields);
            Files.writeString(formDir.resolve("list.html"), listHtml);

            System.out.println("📄 Vistas generadas para: " + taskName);
        }
    }

    private String generateFormHtml(String entity, List<FormFieldData> fields) {
        StringBuilder sb = new StringBuilder();
        sb.append("""
        <!DOCTYPE html>
        <html xmlns:th="http://www.thymeleaf.org">
        <head>
            <meta charset="UTF-8">
            <title>Formulario de """ + entity + """
        </title>
        </head>
        <body>
            <h1>Formulario de """ + entity + """
            </h1>
            <form th:action="@{/""" + entity + """
                }" method="post" th:object="${""" + entity + """
    }">
    """);

        for (FormFieldData field : fields) {
            String id = field.getId();
            String type = field.getType();

            sb.append("    <label>").append(id).append(":</label>\n");

            switch (type.toLowerCase()) {
                case "boolean":
                    sb.append("    <select th:field=\"*{").append(id).append("}\">\n")
                            .append("        <option value=\"true\">Sí</option>\n")
                            .append("        <option value=\"false\">No</option>\n")
                            .append("    </select>\n");
                    break;
                case "long":
                case "integer":
                case "double":
                case "number":
                    sb.append("    <input type=\"number\" th:field=\"*{").append(id).append("}\" />\n");
                    break;
                case "date":
                    sb.append("    <input type=\"date\" th:field=\"*{").append(id).append("}\" />\n");
                    break;
                case "email":
                    sb.append("    <input type=\"email\" th:field=\"*{").append(id).append("}\" />\n");
                    break;
                case "string":
                default:
                    sb.append("    <input type=\"text\" th:field=\"*{").append(id).append("}\" />\n");
                    break;
            }

            sb.append("    <br/>\n");
        }

        sb.append("""
                <button type="submit">Guardar</button>
            </form>
        </body>
        </html>
    """);

        return sb.toString();
    }

    private String generateListHtml(String entity, List<FormFieldData> fields) {
        StringBuilder sb = new StringBuilder();
        sb.append("""
        <!DOCTYPE html>
        <html xmlns:th="http://www.thymeleaf.org">
        <head>
            <meta charset="UTF-8">
            <title>Lista de """ + entity + """
        </title>
        </head>
        <body>
            <h1>Lista de """ + entity + """
            </h1>
            <table border="1">
                <thead>
                    <tr>
    """);

        for (FormFieldData field : fields) {
            sb.append("            <th>").append(field.getId()).append("</th>\n");
        }

        sb.append("""
                    </tr>
                </thead>
                <tbody>
                    <tr th:each="item : ${items}">
    """);

        for (FormFieldData field : fields) {
            String id = field.getId();
            String type = field.getType();

            switch (type.toLowerCase()) {
                case "boolean":
                    sb.append("            <td th:text=\"${item.").append(id).append("} ? 'Sí' : 'No'\"></td>\n");
                    break;
                case "date":
                    sb.append("            <td th:text=\"${#temporals.format(item.").append(id).append(", 'dd/MM/yyyy')}\"></td>\n");
                    break;
                default:
                    sb.append("            <td th:text=\"${item.").append(id).append("}\"></td>\n");
                    break;
            }
        }

        sb.append("""
                    </tr>
                </tbody>
            </table>
        </body>
        </html>
    """);

        return sb.toString();
    }
}
