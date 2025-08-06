package com.form.cliente.model;

import jakarta.persistence.*;

@Entity
@Table(name = "diligenciar_formulario_de_solicitud")
public class Diligenciarformulariodesolicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String coupleName1;
    private String coupleName2;
    private String coupleEmail1;
    private String coupleEmail2;
    private String creationDate;
    private Long codRequest;
    private Long applicantCouple;
    private Boolean bothEmployees;
    private Long marriageYears;
    private Long countReviewsBpm;
    private String pdfSupport;
    private String workSupport;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCoupleName1() {
        return coupleName1;
    }

    public void setCoupleName1(String coupleName1) {
        this.coupleName1 = coupleName1;
    }

    public String getCoupleName2() {
        return coupleName2;
    }

    public void setCoupleName2(String coupleName2) {
        this.coupleName2 = coupleName2;
    }

    public String getCoupleEmail1() {
        return coupleEmail1;
    }

    public void setCoupleEmail1(String coupleEmail1) {
        this.coupleEmail1 = coupleEmail1;
    }

    public String getCoupleEmail2() {
        return coupleEmail2;
    }

    public void setCoupleEmail2(String coupleEmail2) {
        this.coupleEmail2 = coupleEmail2;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public Long getCodRequest() {
        return codRequest;
    }

    public void setCodRequest(Long codRequest) {
        this.codRequest = codRequest;
    }

    public Long getApplicantCouple() {
        return applicantCouple;
    }

    public void setApplicantCouple(Long applicantCouple) {
        this.applicantCouple = applicantCouple;
    }

    public Boolean getBothEmployees() {
        return bothEmployees;
    }

    public void setBothEmployees(Boolean bothEmployees) {
        this.bothEmployees = bothEmployees;
    }

    public Long getMarriageYears() {
        return marriageYears;
    }

    public void setMarriageYears(Long marriageYears) {
        this.marriageYears = marriageYears;
    }

    public Long getCountReviewsBpm() {
        return countReviewsBpm;
    }

    public void setCountReviewsBpm(Long countReviewsBpm) {
        this.countReviewsBpm = countReviewsBpm;
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

}