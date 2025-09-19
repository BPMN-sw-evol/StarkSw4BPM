package com.starksw4b.pmn.starksw4bpmn.fileGenerator.internal;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class JavaClassGeneratorService {

    private static final String BASE_PATH = "../generatedProjects/generatedproject/BPM-Engine/src/main/java/com/example/workflow";

    public void generateJavaClass(String className) throws IOException {
        String content = generateClassContent(className);
        Path outputPath = Paths.get(BASE_PATH, className + ".java");

        Files.createDirectories(outputPath.getParent());
        Files.writeString(outputPath, content);

        System.out.println("⚙ Clase JavaClass generada en: " + outputPath.toAbsolutePath());
    }

    private String generateClassContent(String className) {
        return "package com.example.workflow;\n\n" +
                "import org.camunda.bpm.engine.delegate.DelegateExecution;\n" +
                "import org.camunda.bpm.engine.delegate.JavaDelegate;\n" +
                "import org.springframework.beans.factory.annotation.Value;\n" +
                "import org.springframework.stereotype.Service;\n\n" +
                "@Service\n" +
                "public class " + className + " implements JavaDelegate {\n\n" +
                "    @Value(\"${CONNECTION_CREDIT_REQUEST}\")\n" +
                "    private String connectionDB;\n" +
                "    @Value(\"${USER_DB}\")\n" +
                "    private String userDB;\n" +
                "    @Value(\"${PASSWORD_DB}\")\n" +
                "    private String passwordDB;\n\n" +
                "    @Override\n" +
                "    public void execute(DelegateExecution execution) throws Exception {\n" +
                "        System.out.println(\"⚙ Ejecutando tarea de servicio Java class...\");\n" +
                "    }\n" +
                "}\n";
    }
}
