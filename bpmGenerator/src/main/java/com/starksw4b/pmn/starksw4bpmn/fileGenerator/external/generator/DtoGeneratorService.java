package com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.generator;

import com.starksw4b.pmn.starksw4bpmn.model.FormFieldData;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Map;

@Service
public class DtoGeneratorService {

    private static final String PACKAGE_NAME = "com.form.cliente.dto";

    public void generateDtos(Path externalProjectPath, Map<String, List<FormFieldData>> formFieldsMap) throws IOException {
        Path dtoDir = getDtoDirectory(externalProjectPath);
        Files.createDirectories(dtoDir);

        for (Map.Entry<String, List<FormFieldData>> entry : formFieldsMap.entrySet()) {
            String taskName = entry.getKey().replaceAll("\\s+", "");
            List<FormFieldData> fields = entry.getValue();

            Path filePath = dtoDir.resolve(taskName + "DTO.java");
            StringBuilder classContent = new StringBuilder();

            classContent.append("package ").append(PACKAGE_NAME).append(";\n\n");
            classContent.append("public class ").append(taskName).append("DTO {\n\n");

            // Atributos
            for (FormFieldData field : fields) {
                String javaType = mapCamundaTypeToJava(field.getType());
                classContent.append("    private ").append(javaType).append(" ").append(field.getId()).append(";\n");
            }

            classContent.append("\n");

            // Getters y setters
            for (FormFieldData field : fields) {
                String javaType = mapCamundaTypeToJava(field.getType());
                String fieldName = field.getId();
                String capitalized = capitalize(fieldName);

                classContent.append("    public ").append(javaType).append(" get").append(capitalized).append("() {\n")
                        .append("        return ").append(fieldName).append(";\n")
                        .append("    }\n\n");

                classContent.append("    public void set").append(capitalized).append("(").append(javaType).append(" ").append(fieldName).append(") {\n")
                        .append("        this.").append(fieldName).append(" = ").append(fieldName).append(";\n")
                        .append("    }\n\n");
            }

            classContent.append("}");

            Files.writeString(filePath, classContent.toString());
            System.out.println("✅ DTO generado: " + filePath.getFileName());
        }
    }

    private Path getDtoDirectory(Path externalProjectPath) {
        return externalProjectPath.resolve("src/main/java/com/form/cliente/dto".replace(".", File.separator));
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
}
