package com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.generator;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class EntityGeneratorService {

    public void generateEntities(Path projectPath) throws IOException {
        // Ruta destino
        Path entityPath = projectPath.resolve(
                "src/main/java/com/form/client/model/Formulario.java"
        );

        File parentDir = entityPath.toFile().getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("package com.form.client.model;\n\n")
                .append("import jakarta.persistence.*;\n")
                .append("\n")
                .append("@Entity\n")
                .append("public class Formulario {\n\n")
                .append("    @Id\n")
                .append("    @GeneratedValue(strategy = GenerationType.IDENTITY)\n")
                .append("    private Long id;\n\n")
                .append("    private String nombre;\n")
                .append("    private Integer edad;\n")
                .append("    private String correo;\n")
                .append("    private boolean activo;\n")
                .append("    private boolean aprobado;\n")
                .append("\n")
                .append("    private String estado;\n")
                .append("\n")
                // Getters & Setters
                .append("    public Long getId() { return id; }\n")
                .append("    public void setId(Long id) { this.id = id; }\n\n")

                .append("    public String getNombre() { return nombre; }\n")
                .append("    public void setNombre(String nombre) { this.nombre = nombre; }\n\n")

                .append("    public Integer getEdad() { return edad; }\n")
                .append("    public void setEdad(Integer edad) { this.edad = edad; }\n\n")

                .append("    public String getCorreo() { return correo; }\n")
                .append("    public void setCorreo(String correo) { this.correo = correo; }\n\n")

                .append("    public boolean isActivo() { return activo; }\n")
                .append("    public void setActivo(boolean activo) { this.activo = activo; }\n\n")

                .append("    public boolean isAprobado() { return aprobado; }\n")
                .append("    public void setAprobado(boolean aprobado) { this.aprobado = aprobado; }\n\n")

                .append("    public String getEstado() { return estado; }\n")
                .append("    public void setEstado(String estado) { this.estado = estado; }\n\n")

                .append("}\n");

        Files.writeString(entityPath, sb.toString());

        System.out.println("✅ Entidad generada en: " + entityPath);
    }
}