package com.form.cliente.model;

import jakarta.persistence.*;

@Entity
@Table(name = "revisar_soportes_de_solicitud")
public class Revisarsoportesdesolicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pdfSupport;
    private String workSupport;
    private Boolean validSupports;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPdfSupport() {
        return pdfSupport;
    }

    public void setPdfSupport(String pdfSupport) {
        this.pdfSupport = pdfSupport;
    }

    public String getWorkSupport() {
        return workSupport;
    }

    public void setWorkSupport(String workSupport) {
        this.workSupport = workSupport;
    }

    public Boolean getValidSupports() {
        return validSupports;
    }

    public void setValidSupports(Boolean validSupports) {
        this.validSupports = validSupports;
    }

}