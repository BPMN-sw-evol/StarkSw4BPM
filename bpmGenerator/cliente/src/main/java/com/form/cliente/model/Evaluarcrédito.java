package com.form.cliente.model;

import jakarta.persistence.*;

@Entity
@Table(name = "evaluar_crédito")
public class Evaluarcrédito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long coupleSavings;
    private Long housePrices;
    private Long quotaValue;
    private Boolean isValid;
    private Boolean bothEmployees;
    private Long marriageYears;
    private String workSupport;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCoupleSavings() {
        return coupleSavings;
    }

    public void setCoupleSavings(Long coupleSavings) {
        this.coupleSavings = coupleSavings;
    }

    public Long getHousePrices() {
        return housePrices;
    }

    public void setHousePrices(Long housePrices) {
        this.housePrices = housePrices;
    }

    public Long getQuotaValue() {
        return quotaValue;
    }

    public void setQuotaValue(Long quotaValue) {
        this.quotaValue = quotaValue;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
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

    public String getWorkSupport() {
        return workSupport;
    }

    public void setWorkSupport(String workSupport) {
        this.workSupport = workSupport;
    }

}