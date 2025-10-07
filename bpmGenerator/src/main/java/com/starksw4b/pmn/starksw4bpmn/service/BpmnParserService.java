package com.starksw4b.pmn.starksw4bpmn.service;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

@Service
public class BpmnParserService {

    /**
     * Lee un archivo BPMN y devuelve todos los elementos (tareas y compuertas)
     * agrupados por tipo.
     */
    public Map<String, List<Map<String, String>>> parseElements(String pathArchivo) throws Exception {
        File archivo = new File(pathArchivo);

        if (!archivo.exists()) {
            throw new FileNotFoundException("El archivo no se encontró: " + pathArchivo);
        }

        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(archivo);
        Map<String, List<Map<String, String>>> elementosPorTipo = new LinkedHashMap<>();

        Map<String, Class<? extends BaseElement>> tipos = new LinkedHashMap<>();
        tipos.put("userTask", UserTask.class);
        tipos.put("serviceTask", ServiceTask.class);
        tipos.put("sendTask", SendTask.class);
        tipos.put("exclusiveGateway", ExclusiveGateway.class);
        tipos.put("parallelGateway", ParallelGateway.class);

        for (Map.Entry<String, Class<? extends BaseElement>> entry : tipos.entrySet()) {
            String tipo = entry.getKey();
            Class<? extends BaseElement> clase = entry.getValue();
            Collection<? extends BaseElement> elementos = modelInstance.getModelElementsByType(clase);

            List<Map<String, String>> datos = new ArrayList<>();
            for (BaseElement e : elementos) {
                if (e instanceof FlowNode fn && fn.getName() != null && !fn.getName().isBlank()) {
                    Map<String, String> info = new HashMap<>();
                    info.put("name", fn.getName());
                    info.put("key", fn.getId());
                    datos.add(info);
                }
            }
            if (!datos.isEmpty()) {
                elementosPorTipo.put(tipo, datos);
            }
        }

        return elementosPorTipo;
    }
}
