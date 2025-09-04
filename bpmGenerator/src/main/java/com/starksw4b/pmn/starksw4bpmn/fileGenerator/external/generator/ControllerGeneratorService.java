package com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.generator;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class ControllerGeneratorService {

    private static final String CONTROLLER_PACKAGE = "com.form.client.controller";

    public void generateControllers(Path projectPath) throws IOException {
        Path controllerDir = getControllerDirectory(projectPath);
        Files.createDirectories(controllerDir);

        String content = """
                package com.form.client.controller;

                import com.form.client.dto.FormularioDTO;
                import com.form.client.services.FormularioService;
                import org.springframework.stereotype.Controller;
                import org.springframework.web.bind.annotation.*;
                import org.springframework.ui.Model;

                @Controller
                @RequestMapping("/formulario")
                public class FormularioController {

                    private final FormularioService formularioService;

                    public FormularioController(FormularioService formularioService) {
                        this.formularioService = formularioService;
                    }

                    // Lector de GET: si viene id, recargamos ese DTO; si no, uno nuevo
                    @GetMapping
                    public String mostrarFormulario(
                            @RequestParam(value="id", required=false) Long id,
                            @RequestParam(value="exito", required=false) Boolean exito,
                            Model model) {
                        if (id != null) {
                            FormularioDTO dto = formularioService.obtenerPorId(id);
                            model.addAttribute("formulario", dto);
                        } else {
                            model.addAttribute("formulario", new FormularioDTO());
                        }
                        if (Boolean.TRUE.equals(exito)) {
                            model.addAttribute("exito", true);
                        }
                        return "formulario";
                    }

                    // POST guarda y redirige con id + exito=true
                    @PostMapping
                    public String guardarFormulario(@ModelAttribute("formulario") FormularioDTO formularioDTO) {
                        FormularioDTO guardado = formularioService.guardarFormularioYIniciarProceso(formularioDTO);
                        return "redirect:/formulario?id=" + guardado.getId() + "&exito=true";
                    }

                    @PostMapping("/enviar/id/{id}")
                    public String enviarPorId(@PathVariable Long id) {
                        formularioService.enviarPorId(id);
                        return "redirect:/formulario/lista";
                    }

                    // POST aprobar sigue igual
                    @PostMapping("/aprobar/id/{id}")
                    public String aprobarPorId(@PathVariable Long id) {
                        formularioService.aprobarPorId(id);
                        return "redirect:/formulario/lista";
                    }

                    @GetMapping("/lista")
                    public String listarFormularios(Model model) {
                        model.addAttribute("formularios", formularioService.obtenerTodos());
                        return "lista";
                    }
                }
                """;

        Files.writeString(controllerDir.resolve("FormularioController.java"), content);
        System.out.println("🌐 FormularioController generado.");
    }

    private Path getControllerDirectory(Path projectPath) {
        return projectPath.resolve("src/main/java/com/form/client/controller".replace(".", File.separator));
    }
}
