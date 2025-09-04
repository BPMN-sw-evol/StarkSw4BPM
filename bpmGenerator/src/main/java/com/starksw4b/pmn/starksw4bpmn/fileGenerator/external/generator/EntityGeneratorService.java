package com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.generator;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

@Service
public class EntityGeneratorService {

    private static final String PACKAGE_NAME = "com.form.client.model";

    public void generateEntities(Path externalProjectPath) throws IOException {
        Path modelDir = getModelDirectory(externalProjectPath);
        Files.createDirectories(modelDir);

        Path filePath = modelDir.resolve("Formulario.java");

        String classContent = """
                package com.form.client.model;

                import jakarta.persistence.*;
                import java.time.LocalDate;

                @Entity
                public class Formulario {

                    @Id
                    @GeneratedValue(strategy = GenerationType.IDENTITY)
                    private Long id;

                    private String nombre;
                    private Integer edad;
                    private LocalDate fechaNacimiento;
                    private String correo;
                    private Boolean activo;
                    private Boolean aprobado = false;

                    public Boolean getAprobado() {
                        return aprobado;
                    }

                    public void setAprobado(Boolean aprobado) {
                        this.aprobado = aprobado;
                    }

                    public Long getId() {
                        return id;
                    }

                    public void setId(Long id) {
                        this.id = id;
                    }

                    public String getNombre() {
                        return nombre;
                    }

                    public void setNombre(String nombre) {
                        this.nombre = nombre;
                    }

                    public Integer getEdad() {
                        return edad;
                    }

                    public void setEdad(Integer edad) {
                        this.edad = edad;
                    }

                    public LocalDate getFechaNacimiento() {
                        return fechaNacimiento;
                    }

                    public void setFechaNacimiento(LocalDate fechaNacimiento) {
                        this.fechaNacimiento = fechaNacimiento;
                    }

                    public String getCorreo() {
                        return correo;
                    }

                    public void setCorreo(String correo) {
                        this.correo = correo;
                    }

                    public Boolean getActivo() {
                        return activo;
                    }

                    public void setActivo(Boolean activo) {
                        this.activo = activo;
                    }
                }
                """;

        Files.writeString(filePath, classContent);
        System.out.println("🏗️ Entidad generada: " + filePath.getFileName());
    }

    private Path getModelDirectory(Path externalProjectPath) {
        return externalProjectPath.resolve("src/main/java/com/form/client/model".replace(".", File.separator));
    }
}
