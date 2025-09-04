package com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.generator;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class RestConfigGeneratorService {

    public void generateRestConfig(Path projectPath) throws IOException {
        Path configDir = projectPath.resolve("src/main/java/com/form/client/config".replace(".", "/"));
        Files.createDirectories(configDir);

        generateRestTemplateConfig(configDir);
    }

    private void generateRestTemplateConfig(Path configDir) throws IOException {
        String restConfig = """
            package com.form.client.config;

            import org.springframework.boot.web.client.RestTemplateBuilder;
            import org.springframework.context.annotation.Bean;
            import org.springframework.context.annotation.Configuration;
            import org.springframework.web.client.RestTemplate;

            @Configuration
            public class RestConfig {

                @Bean
                public RestTemplate restTemplate(RestTemplateBuilder builder) {
                    return builder.build();
                }
            }
            """;

        Files.writeString(configDir.resolve("RestConfig.java"), restConfig);
        System.out.println("🔧 Clase RestConfig generada.");
    }
}
