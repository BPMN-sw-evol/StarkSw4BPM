package com.form.cliente.dto;

public class RevisarsoportesdesolicitudDTO {

    private String pdfSupport;
    private String workSupport;
    private Boolean validSupports;

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