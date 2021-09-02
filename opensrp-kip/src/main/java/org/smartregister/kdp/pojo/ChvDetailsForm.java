package org.smartregister.kdp.pojo;

public class ChvDetailsForm {

    private String id;
    private String baseEntityId;
    private String visitId;
    private String chvName;
    private String chvPhoneNumber;
    private String date;
    private String createdAt;

    public ChvDetailsForm() {
    }

    public ChvDetailsForm(String id, String baseEntityId, String visitId, String chvName, String chvPhoneNumber, String date, String createdAt) {
        this.id = id;
        this.baseEntityId = baseEntityId;
        this.visitId = visitId;
        this.chvName = chvName;
        this.chvPhoneNumber = chvPhoneNumber;
        this.date = date;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBaseEntityId() {
        return baseEntityId;
    }

    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }

    public String getVisitId() {
        return visitId;
    }

    public void setVisitId(String visitId) {
        this.visitId = visitId;
    }

    public String getChvName() {
        return chvName;
    }

    public void setChvName(String chvName) {
        this.chvName = chvName;
    }

    public String getChvPhoneNumber() {
        return chvPhoneNumber;
    }

    public void setChvPhoneNumber(String chvPhoneNumber) {
        this.chvPhoneNumber = chvPhoneNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
