package com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.generator;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class EntityGeneratorService {

    public void generateEntities(Path projectPath) throws IOException {
        // 📂 Ruta destino
        Path entityPath = projectPath.resolve(
                "src/main/java/com/form/client/model/Formulario.java"
        );

        File parentDir = entityPath.toFile().getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }

        String content = """
            package com.form.client.model;

            import jakarta.persistence.*;

            @Entity
            public class Formulario {

                @Id
                @GeneratedValue(strategy = GenerationType.IDENTITY)
                private Long id;

                private String nombre;
                private Integer edad;
                private String correo;
                private boolean activo;
                private boolean aprobado;
                private String estado;
                private String processInstanceId; // ✅ ID de instancia de proceso Camunda

                public Long getId() { return id; }
                public void setId(Long id) { this.id = id; }

                public String getNombre() { return nombre; }
                public void setNombre(String nombre) { this.nombre = nombre; }

                public Integer getEdad() { return edad; }
                public void setEdad(Integer edad) { this.edad = edad; }

                public String getCorreo() { return correo; }
                public void setCorreo(String correo) { this.correo = correo; }

                public boolean isActivo() { return activo; }
                public void setActivo(boolean activo) { this.activo = activo; }

                public boolean isAprobado() { return aprobado; }
                public void setAprobado(boolean aprobado) { this.aprobado = aprobado; }

                public String getEstado() { return estado; }
                public void setEstado(String estado) { this.estado = estado; }

                public String getProcessInstanceId() { return processInstanceId; }
                public void setProcessInstanceId(String processInstanceId) { this.processInstanceId = processInstanceId; }
            }
            """;

        Files.writeString(entityPath, content);
        System.out.println("✅ Entidad generada con soporte para Camunda (processInstanceId) en: " + entityPath);
    }
}
