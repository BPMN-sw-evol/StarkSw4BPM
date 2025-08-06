package com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.generator;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class RestConfigGeneratorService {

    public void generateRestConfig(Path projectPath) throws IOException {
        Path configDir = projectPath.resolve("src/main/java/com/form/cliente/config");
        Files.createDirectories(configDir);

        String configClass = """
            package com.form.cliente.config;

            import org.springframework.context.annotation.Configuration;
            import org.springframework.web.servlet.config.annotation.CorsRegistry;
            import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
            import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

            @Configuration
            public class WebConfig implements WebMvcConfigurer {

                @Override
                public void addCorsMappings(CorsRegistry registry) {
                    registry.addMapping("/**")
                            .allowedOrigins("*")
                            .allowedMethods("GET", "POST", "PUT", "DELETE")
                            .allowedHeaders("*");
                }

                @Override
                public void addViewControllers(ViewControllerRegistry registry) {
                    registry.addViewController("/").setViewName("redirect:/index");
                }
            }
        """;

        Files.writeString(configDir.resolve("WebConfig.java"), configClass);
        System.out.println("⚙ Clase de configuración WebConfig generada.");
    }
}
