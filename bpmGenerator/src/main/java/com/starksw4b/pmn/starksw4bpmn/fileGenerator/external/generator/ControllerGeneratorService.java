package com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.generator;

import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Service
public class ControllerGeneratorService {

    public void generateControllers(Path projectPath, List<Map<String, String>> userTasks) throws IOException {
        if (userTasks == null || userTasks.isEmpty()) {
            System.out.println("⚠ No se encontraron user tasks para generar controladores.");
            return;
        }

        Path controllerPath = projectPath.resolve(
                "src/main/java/com/form/client/controller/FormularioController.java"
        );

        File parentDir = controllerPath.toFile().getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("package com.form.client.controller;\n\n")
                .append("import com.form.client.dto.FormularioDTO;\n")
                .append("import com.form.client.services.FormularioService;\n")
                .append("import org.springframework.stereotype.Controller;\n")
                .append("import org.springframework.web.bind.annotation.*;\n")
                .append("import org.springframework.ui.Model;\n\n")
                .append("@Controller\n")
                .append("@RequestMapping(\"/formulario\")\n")
                .append("public class FormularioController {\n\n")
                .append("    private final FormularioService formularioService;\n\n")
                .append("    public FormularioController(FormularioService formularioService) {\n")
                .append("        this.formularioService = formularioService;\n")
                .append("    }\n\n")

                // === GET formulario ===
                .append("    @GetMapping\n")
                .append("    public String mostrarFormulario(\n")
                .append("            @RequestParam(value=\"id\", required=false) Long id,\n")
                .append("            @RequestParam(value=\"exito\", required=false) Boolean exito,\n")
                .append("            Model model) {\n")
                .append("        if (id != null) {\n")
                .append("            FormularioDTO dto = formularioService.obtenerPorId(id);\n")
                .append("            model.addAttribute(\"formulario\", dto);\n")
                .append("        } else {\n")
                .append("            model.addAttribute(\"formulario\", new FormularioDTO());\n")
                .append("        }\n")
                .append("        if (Boolean.TRUE.equals(exito)) {\n")
                .append("            model.addAttribute(\"exito\", true);\n")
                .append("        }\n")
                .append("        return \"formulario\";\n")
                .append("    }\n\n")

                // === POST guardar ===
                .append("    @PostMapping(\"/guardar\")\n")
                .append("    public String guardarFormulario(@ModelAttribute(\"formulario\") FormularioDTO dto) {\n")
                .append("        FormularioDTO guardado = formularioService.guardarFormularioYIniciarProceso(dto);\n")
                .append("        return \"redirect:/formulario?id=\" + guardado.getId() + \"&exito=true\";\n")
                .append("    }\n\n")

                // === POST enviar ===
                .append("    @PostMapping(\"/enviar/id/{id}\")\n")
                .append("    public String enviarPorId(@PathVariable Long id) {\n")
                .append("        formularioService.enviarPorId(id);\n")
                .append("        return \"redirect:/formulario/lista\";\n")
                .append("    }\n\n");

        // Resto de userTasks → aprobar-taskX
        for (int i = 1; i < userTasks.size(); i++) {
            Map<String, String> task = userTasks.get(i);
            String taskName = task.get("name"); // ej: "Acknowledge as received"
            String taskKey  = task.get("key");  // ej: "Activity_0viydpl"

            sb.append("    @GetMapping(\"/aprobar-task").append(i + 1).append("\")\n")
                    .append("    public String listarPendientesTask").append(i + 1).append("(Model model) {\n")
                    .append("        model.addAttribute(\"formularios\", formularioService.obtenerPendientesPorTask(\"")
                    .append(taskKey).append("\"));\n") // 👈 aquí ahora va el taskDefinitionKey real
                    .append("        return \"aprobar-task").append(i + 1).append("\";\n")
                    .append("    }\n\n")

                    .append("    @PostMapping(\"/aprobar-task").append(i + 1).append("/id/{taskId}\")\n")
                    .append("    public String aprobarTask").append(i + 1).append("(@PathVariable String taskId) {\n")
                    .append("        formularioService.aprobarPorTaskId(taskId);\n")
                    .append("        return \"redirect:/formulario/aprobar-task").append(i + 1).append("\";\n")
                    .append("    }\n\n");
        }


        sb.append("    @GetMapping(\"/lista\")\n")
                .append("    public String listarFormularios(Model model) {\n")
                .append("        model.addAttribute(\"formularios\", formularioService.obtenerTodos());\n")
                .append("        return \"lista\";\n")
                .append("    }\n\n");

        sb.append("}\n");

        Files.writeString(controllerPath, sb.toString());
        System.out.println("✅ FormularioController generado en: " + controllerPath);
    }
}
