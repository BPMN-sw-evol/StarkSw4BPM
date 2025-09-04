package com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.generator;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

@Service
public class DtoGeneratorService {

    private static final String PACKAGE_NAME = "com.form.client.dto";

    public void generateDtos(Path externalProjectPath) throws IOException {
        Path dtoDir = getDtoDirectory(externalProjectPath);
        Files.createDirectories(dtoDir);

        Path filePath = dtoDir.resolve("FormularioDTO.java");
        String classContent = """
                package com.form.client.dto;

                import java.time.LocalDate;

                public class FormularioDTO {

                    private Long id;
                    private String nombre;
                    private Integer edad;
                    private LocalDate fechaNacimiento;
                    private String correo;
                    private Boolean activo;
                    private Boolean aprobado = false;

                    public Long getId() {
                        return id;
                    }

                    public void setId(Long id) {
                        this.id = id;
                    }

                    public Boolean getAprobado() {
                        return aprobado;
                    }

                    public void setAprobado(Boolean aprobado) {
                        this.aprobado = aprobado;
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
        System.out.println("📦 DTO fijo generado: " + filePath.getFileName());
    }

    private Path getDtoDirectory(Path externalProjectPath) {
        return externalProjectPath.resolve("src/main/java/com/form/client/dto".replace(".", File.separator));
    }
}
