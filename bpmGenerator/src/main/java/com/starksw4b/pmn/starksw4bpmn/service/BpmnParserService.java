package com.starksw4b.pmn.starksw4bpmn.service;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class BpmnParserService {

    /**
     * 🧩 Lee un archivo BPMN y devuelve todos los elementos (tareas, compuertas y condiciones)
     * agrupados por tipo, incluyendo los flujos booleanos de gateways.
     */
    public Map<String, List<Map<String, String>>> parseElements(String pathArchivo) throws Exception {
        File archivo = new File(pathArchivo);

        if (!archivo.exists()) {
            throw new FileNotFoundException("El archivo no se encontró: " + pathArchivo);
        }

        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(archivo);
        Map<String, List<Map<String, String>>> elementosPorTipo = new LinkedHashMap<>();

        // 🔹 Tipos de elementos a buscar
        Map<String, Class<? extends BaseElement>> tipos = new LinkedHashMap<>();
        tipos.put("userTask", UserTask.class);
        tipos.put("serviceTask", ServiceTask.class);
        tipos.put("sendTask", SendTask.class);
        tipos.put("exclusiveGateway", ExclusiveGateway.class);
        tipos.put("parallelGateway", ParallelGateway.class);

        // 🔹 Extraer tareas y compuertas
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
                    info.put("type", tipo);
                    datos.add(info);
                }
            }
            if (!datos.isEmpty()) {
                elementosPorTipo.put(tipo, datos);
            }
        }

        // 🔹 Extraer condiciones y flujos de compuertas
        Collection<SequenceFlow> sequenceFlows = modelInstance.getModelElementsByType(SequenceFlow.class);
        List<Map<String, String>> condiciones = new ArrayList<>();

        // 🧠 Mapa temporal para agrupar las salidas de cada compuerta
        Map<String, Map<String, String>> gatewayMap = new HashMap<>();

        for (SequenceFlow flow : sequenceFlows) {
            String sourceRef = flow.getSource().getId();
            String targetRef = flow.getTarget().getId();
            String conditionExpression = null;
            String variableName = null;
            String expectedValue = null;

            if (flow.getConditionExpression() != null) {
                conditionExpression = flow.getConditionExpression().getTextContent().trim();

                // Ejemplo: ${decision == true} o ${true}
                if (conditionExpression.contains("==")) {
                    String clean = conditionExpression.replace("${", "").replace("}", "").trim();
                    String[] parts = clean.split("==");
                    if (parts.length == 2) {
                        variableName = parts[0].trim();
                        expectedValue = parts[1].trim();
                    }
                } else if (conditionExpression.contains("true") || conditionExpression.contains("false")) {
                    variableName = "decision"; // fallback genérico
                    expectedValue = conditionExpression.contains("true") ? "true" : "false";
                }
            }

            // Detectar tipo de gateway si el source es una compuerta
            String gatewayType = null;
            BaseElement sourceElement = modelInstance.getModelElementById(sourceRef);
            if (sourceElement instanceof ExclusiveGateway) {
                gatewayType = "exclusive";
            } else if (sourceElement instanceof ParallelGateway) {
                gatewayType = "parallel";
            }

            // 🧠 Guardar en gatewayMap los flujos true/false
            if ("exclusive".equalsIgnoreCase(gatewayType)) {
                Map<String, String> gwData = gatewayMap.computeIfAbsent(sourceRef, k -> new HashMap<>());
                gwData.put("variableName", variableName);
                if ("true".equalsIgnoreCase(expectedValue)) {
                    gwData.put("conditionTrue", targetRef);
                } else if ("false".equalsIgnoreCase(expectedValue)) {
                    gwData.put("conditionFalse", targetRef);
                }
            }

            Map<String, String> info = new HashMap<>();
            info.put("sourceRef", sourceRef);
            info.put("targetRef", targetRef);
            info.put("conditionExpression", conditionExpression);
            info.put("variableName", variableName);
            info.put("expectedValue", expectedValue);
            info.put("gatewayType", gatewayType);

            if (gatewayType != null) {
                condiciones.add(info);
            }
        }

        // 🔹 Agregar condiciones individuales
        if (!condiciones.isEmpty()) {
            elementosPorTipo.put("gatewayConditions", condiciones);
        }

        // 🔹 Agregar gateways con salidas true/false detectadas
        if (!gatewayMap.isEmpty()) {
            List<Map<String, String>> gatewaysDetectados = new ArrayList<>();
            for (Map.Entry<String, Map<String, String>> e : gatewayMap.entrySet()) {
                Map<String, String> data = new HashMap<>();
                data.put("gatewayId", e.getKey());
                data.putAll(e.getValue());
                gatewaysDetectados.add(data);
            }
            elementosPorTipo.put("exclusiveGateways", gatewaysDetectados);
        }

        return elementosPorTipo;
    }

    /**
     * 🔍 Extrae el ID del proceso (processDefinitionKey) del BPMN de forma segura.
     */
    public String extractProcessId(String pathArchivo) throws Exception {
        File archivo = new File(pathArchivo);
        if (!archivo.exists()) {
            throw new FileNotFoundException("El archivo no se encontró: " + pathArchivo);
        }

        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(archivo);
        Collection<Process> procesos = modelInstance.getModelElementsByType(Process.class);
        if (procesos.isEmpty()) {
            throw new RuntimeException("❌ No se encontró ningún proceso en el BPMN: " + pathArchivo);
        }

        Process procesoPrincipal = procesos.iterator().next();
        String processId = procesoPrincipal.getId();

        if (processId == null || processId.isBlank()) {
            throw new RuntimeException("❌ El proceso no tiene atributo 'id' definido en el BPMN.");
        }

        System.out.println("✅ ProcessDefinitionKey detectado automáticamente: " + processId);
        return processId;
    }

    /**
     * 🧰 Alternativa de respaldo: extracción con expresión regular directa (por si el parser de Camunda falla).
     */
    public String extractProcessIdFallback(Path bpmnFile) {
        try {
            String xml = Files.readString(bpmnFile);
            Pattern pattern = Pattern.compile("<process[^>]*id=\"([^\"]+)\"");
            Matcher matcher = pattern.matcher(xml);
            if (matcher.find()) {
                return matcher.group(1);
            }
            throw new IllegalArgumentException("No se encontró atributo id en el archivo BPMN.");
        } catch (Exception e) {
            throw new RuntimeException("Error leyendo el archivo BPMN: " + e.getMessage(), e);
        }
    }
}
