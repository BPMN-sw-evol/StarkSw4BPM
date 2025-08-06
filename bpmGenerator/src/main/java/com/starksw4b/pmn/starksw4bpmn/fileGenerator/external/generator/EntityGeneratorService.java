package com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.generator;

import com.starksw4b.pmn.starksw4bpmn.model.FormFieldData;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Map;

@Service
public class EntityGeneratorService {

    private static final String PACKAGE_NAME = "com.form.cliente.model";

    public void generateEntities(Path externalProjectPath, Map<String, List<FormFieldData>> formFieldsMap) throws IOException {
        Path modelDir = getModelDirectory(externalProjectPath);
        Files.createDirectories(modelDir);

        for (Map.Entry<String, List<FormFieldData>> entry : formFieldsMap.entrySet()) {
            String taskName = entry.getKey().replaceAll("\\s+", "");
            String tableName = entry.getKey().toLowerCase().replaceAll("\\s+", "_");
            List<FormFieldData> fields = entry.getValue();

            Path filePath = modelDir.resolve(taskName + ".java");
            StringBuilder classContent = new StringBuilder();

            // Encabezado
            classContent.append("package ").append(PACKAGE_NAME).append(";\n\n")
                    .append("import jakarta.persistence.*;\n\n")
                    .append("@Entity\n")
                    .append("@Table(name = \"").append(tableName).append("\")\n")
                    .append("public class ").append(taskName).append(" {\n\n");

            // Campo ID
            classContent.append("    @Id\n")
                    .append("    @GeneratedValue(strategy = GenerationType.IDENTITY)\n")
                    .append("    private Long id;\n\n");

            // Otros campos
            for (FormFieldData field : fields) {
                String javaType = mapCamundaTypeToJava(field.getType());
                classContent.append("    private ").append(javaType).append(" ").append(field.getId()).append(";\n");
            }

            classContent.append("\n");

            // Getters y setters
            classContent.append(generateGetterSetter("Long", "id"));
            for (FormFieldData field : fields) {
                String javaType = mapCamundaTypeToJava(field.getType());
                classContent.append(generateGetterSetter(javaType, field.getId()));
            }

            classContent.append("}");

            Files.writeString(filePath, classContent.toString());
            System.out.println("✅ Entidad generada: " + filePath.getFileName());
        }
    }

    private Path getModelDirectory(Path externalProjectPath) {
        return externalProjectPath.resolve("src/main/java/com/form/cliente/model".replace(".", File.separator));
    }

    private String mapCamundaTypeToJava(String camundaType) {
        return switch (camundaType) {
            case "boolean" -> "Boolean";
            case "long" -> "Long";
            case "double" -> "Double";
            case "int", "integer" -> "Integer";
            case "date" -> "String";
            default -> "String";
        };
    }

    private String capitalize(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    private String generateGetterSetter(String type, String field) {
        String capitalized = capitalize(field);
        return "    public " + type + " get" + capitalized + "() {\n" +
                "        return " + field + ";\n" +
                "    }\n\n" +
                "    public void set" + capitalized + "(" + type + " " + field + ") {\n" +
                "        this." + field + " = " + field + ";\n" +
                "    }\n\n";
    }
}
