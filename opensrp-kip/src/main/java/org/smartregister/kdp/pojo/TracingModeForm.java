package org.smartregister.kdp.pojo;

public class TracingModeForm {

    private String id;
    private String baseEntityId;
    private String visitId;
    private String phoneTracing;
    private String physicalTracing;
    private String date;
    private String createdAt;


    public TracingModeForm() {
    }

    public TracingModeForm(String id, String baseEntityId, String visitId, String phoneTracing, String physicalTracing, String date, String createdAt) {
        this.id = id;
        this.baseEntityId = baseEntityId;
        this.visitId = visitId;
        this.phoneTracing = phoneTracing;
        this.physicalTracing = physicalTracing;
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

    public String getPhoneTracing() {
        return phoneTracing;
    }

    public void setPhoneTracing(String phoneTracing) {
        this.phoneTracing = phoneTracing;
    }

    public String getPhysicalTracing() {
        return physicalTracing;
    }

    public void setPhysicalTracing(String physicalTracing) {
        this.physicalTracing = physicalTracing;
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
