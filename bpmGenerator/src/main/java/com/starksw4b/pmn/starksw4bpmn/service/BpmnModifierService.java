package com.starksw4b.pmn.starksw4bpmn.service;

import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

@Service
public class BpmnModifierService {

    private static final String CAMUNDA_NS = "http://camunda.org/schema/1.0/bpmn";

    /** Agrega o reemplaza un atributo camunda:* en TODAS las tareas (serviceTask/sendTask) cuyo @name == taskName */
    public int upsertAttrByTaskName(File bpmnFile, String taskName, String qualifiedAttr, String value) {
        try {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setNamespaceAware(true);
            DocumentBuilder b = f.newDocumentBuilder();
            Document doc = b.parse(bpmnFile);

            ensureCamundaNamespaceOnRoot(doc);

            XPathFactory xpf = XPathFactory.newInstance();
            XPath xp = xpf.newXPath();

            // Busca serviceTask o sendTask que tengan atributo name
            String expr = "//*[local-name()='serviceTask' or local-name()='sendTask'][@name]";
            NodeList nodes = (NodeList) xp.evaluate(expr, doc, XPathConstants.NODESET);

            int changes = 0;
            for (int i = 0; i < nodes.getLength(); i++) {
                Element el = (Element) nodes.item(i);
                String name = el.getAttribute("name");
                if (name != null && name.equals(taskName)) {
                    // Quitar el atributo contrario para evitar doble configuración
                    if (qualifiedAttr.endsWith(":class")) {
                        el.removeAttributeNS(CAMUNDA_NS, "delegateExpression");
                    } else if (qualifiedAttr.endsWith(":delegateExpression")) {
                        el.removeAttributeNS(CAMUNDA_NS, "class");
                    }
                    // Escribir el atributo con namespace Camunda
                    el.setAttributeNS(CAMUNDA_NS, qualifiedAttr, value);
                    changes++;
                }
            }

            if (changes > 0) {
                Transformer tf = TransformerFactory.newInstance().newTransformer();
                tf.setOutputProperty(OutputKeys.INDENT, "yes");
                tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
                tf.transform(new DOMSource(doc), new StreamResult(bpmnFile));
            }

            System.out.println("BPMN actualizado: " + changes + " tarea(s) con nombre '" + taskName + "' → " + qualifiedAttr + "=" + value);
            return changes;

        } catch (Exception e) {
            throw new RuntimeException("Error modificando BPMN por nombre de tarea", e);
        }
    }

    /** Asegura que exista xmlns:camunda en el root (algunos modelos lo pierden al reescribir). */
    private void ensureCamundaNamespaceOnRoot(Document doc) {
        Element root = doc.getDocumentElement();
        if (root != null) {
            String existing = root.getAttribute("xmlns:camunda");
            if (existing == null || existing.isBlank()) {
                root.setAttribute("xmlns:camunda", CAMUNDA_NS);
            }
        }
    }
}
