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
        if (!parentDir.exists()) parentDir.mkdirs();

        StringBuilder sb = new StringBuilder();
        sb.append("package com.form.client.controller;\n\n")
                .append("import com.form.client.dto.FormularioDTO;\n")
                .append("import com.form.client.services.FormularioService;\n")
                .append("import com.form.client.services.CamundaService;\n")
                .append("import org.springframework.stereotype.Controller;\n")
                .append("import org.springframework.web.bind.annotation.*;\n")
                .append("import org.springframework.ui.Model;\n\n")
                .append("import java.util.HashMap;\n")
                .append("import java.util.Map;\n\n")
                .append("@Controller\n")
                .append("@RequestMapping(\"/formulario\")\n")
                .append("public class FormularioController {\n\n")
                .append("    private final FormularioService formularioService;\n")
                .append("    private final CamundaService camundaService;\n\n")
                .append("    public FormularioController(FormularioService formularioService, CamundaService camundaService) {\n")
                .append("        this.formularioService = formularioService;\n")
                .append("        this.camundaService = camundaService;\n")
                .append("    }\n\n")

                // === GET formulario principal ===
                .append("    @GetMapping\n")
                .append("    public String mostrarFormulario(@RequestParam(value=\"id\", required=false) Long id,\n")
                .append("                                   @RequestParam(value=\"exito\", required=false) Boolean exito,\n")
                .append("                                   Model model) {\n")
                .append("        if (id != null) {\n")
                .append("            FormularioDTO dto = formularioService.obtenerPorId(id);\n")
                .append("            model.addAttribute(\"formulario\", dto);\n")
                .append("        } else {\n")
                .append("            model.addAttribute(\"formulario\", new FormularioDTO());\n")
                .append("        }\n")
                .append("        if (Boolean.TRUE.equals(exito)) model.addAttribute(\"exito\", true);\n")
                .append("        return \"formulario\";\n")
                .append("    }\n\n")

                // === POST guardar ===
                .append("    @PostMapping(\"/guardar\")\n")
                .append("    public String guardarFormulario(@ModelAttribute(\"formulario\") FormularioDTO dto) {\n")
                .append("        FormularioDTO guardado = formularioService.guardarFormularioYIniciarProceso(dto);\n")
                .append("        System.out.println(\"✅ Formulario guardado e instancia iniciada: ID=\" + guardado.getId());\n")
                .append("        return \"redirect:/formulario?id=\" + guardado.getId() + \"&exito=true\";\n")
                .append("    }\n\n")

                // === POST enviar ===
                .append("    @PostMapping(\"/enviar/id/{id}\")\n")
                .append("    public String enviarPorId(@PathVariable Long id) {\n")
                .append("        formularioService.enviarPorId(id);\n")
                .append("        System.out.println(\"➡️ Tarea inicial completada para formulario ID=\" + id);\n")
                .append("        return \"redirect:/formulario\";\n")
                .append("    }\n\n")

                // === ✅ POST condition (aprobar / rechazar usando processInstanceId) ===
                .append("    @PostMapping(\"/condition/{variable}/{valor}/{id}\")\n")
                .append("    public String setCondition(@PathVariable String variable,\n")
                .append("                                @PathVariable String valor,\n")
                .append("                                @PathVariable Long id) {\n")
                .append("        boolean boolValue = Boolean.parseBoolean(valor);\n")
                .append("        Map<String, Object> vars = new HashMap<>();\n")
                .append("        vars.put(variable, boolValue);\n\n")
                .append("        FormularioDTO formulario = formularioService.obtenerPorId(id);\n")
                .append("        if (formulario.getProcessInstanceId() != null) {\n")
                .append("            camundaService.enviarVariableDeDecision(formulario.getProcessInstanceId(), vars);\n")
                .append("            System.out.println(\"📤 Enviada decisión='\" + boolValue + \"' para instancia=\" + formulario.getProcessInstanceId());\n")
                .append("        } else {\n")
                .append("            System.out.println(\"⚠ El formulario con ID=\" + id + \" no tiene processInstanceId asociado.\");\n")
                .append("        }\n")
                .append("        return \"redirect:/formulario\";\n")
                .append("    }\n\n");

        // === Generar controladores por cada userTask ===
        for (int i = 1; i < userTasks.size(); i++) {
            Map<String, String> task = userTasks.get(i);
            String taskKey = task.get("key");

            sb.append("    @GetMapping(\"/task").append(i + 1).append("\")\n")
                    .append("    public String listarPendientesTask").append(i + 1).append("(Model model) {\n")
                    .append("        model.addAttribute(\"formularios\", formularioService.obtenerPendientesPorTask(\"")
                    .append(taskKey).append("\"));\n")
                    .append("        return \"task").append(i + 1).append("\";\n")
                    .append("    }\n\n")
                    .append("    @PostMapping(\"/task").append(i + 1).append("/id/{taskId}\")\n")
                    .append("    public String completarTask").append(i + 1).append("(@PathVariable String taskId) {\n")
                    .append("        formularioService.aprobarPorTaskId(taskId);\n")
                    .append("        System.out.println(\"✅ Tarea completada manualmente: taskId=\" + taskId);\n")
                    .append("        return \"redirect:/formulario/task").append(i + 1).append("\";\n")
                    .append("    }\n\n");
        }

        sb.append("}\n");

        Files.writeString(controllerPath, sb.toString());
        System.out.println("✅ FormularioController generado correctamente en: " + controllerPath);
    }
}
