package com.starksw4b.pmn.starksw4bpmn.model;

public class FormFieldData {
    private String id;
    private String type;

    public FormFieldData(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}
