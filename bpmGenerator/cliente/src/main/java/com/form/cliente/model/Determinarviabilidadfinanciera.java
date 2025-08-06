package com.form.cliente.model;

import jakarta.persistence.*;

@Entity
@Table(name = "determinar_viabilidad_financiera")
public class Determinarviabilidadfinanciera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean legalConcept;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getLegalConcept() {
        return legalConcept;
    }

    public void setLegalConcept(Boolean legalConcept) {
        this.legalConcept = legalConcept;
    }

}