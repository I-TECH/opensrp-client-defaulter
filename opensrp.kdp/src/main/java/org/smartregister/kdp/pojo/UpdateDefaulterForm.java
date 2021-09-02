package org.smartregister.kdp.pojo;

public class UpdateDefaulterForm {

    private String id;
    private String baseEntityId;
    private String visitId;
    private String phoneTracingOutcome;
    private String physicalTracingOutcome;
    private String phoneTracing;
    private String physicalTracing;
    private String homeAdministrationDate;
    private String otherFacilityAdministrationDate;
    private String otherFacilityName;
    private String dateToConfirmVaccination;
    private String modeOfTracing;
    private String age;
    private String date;
    private String createdAt;

    public UpdateDefaulterForm() {
    }

    public UpdateDefaulterForm(String id, String baseEntityId, String visitId, String phoneTracingOutcome, String physicalTracingOutcome, String phoneTracing, String physicalTracing, String homeAdministrationDate, String otherFacilityAdministrationDate, String otherFacilityName, String dateToConfirmVaccination, String modeOfTracing, String age, String date, String createdAt) {
        this.id = id;
        this.baseEntityId = baseEntityId;
        this.visitId = visitId;
        this.phoneTracingOutcome = phoneTracingOutcome;
        this.physicalTracingOutcome = physicalTracingOutcome;
        this.phoneTracing = phoneTracing;
        this.physicalTracing = physicalTracing;
        this.homeAdministrationDate = homeAdministrationDate;
        this.otherFacilityAdministrationDate = otherFacilityAdministrationDate;
        this.otherFacilityName = otherFacilityName;
        this.dateToConfirmVaccination = dateToConfirmVaccination;
        this.modeOfTracing = modeOfTracing;
        this.age = age;
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

    public String getPhoneTracingOutcome() {
        return phoneTracingOutcome;
    }

    public void setPhoneTracingOutcome(String phoneTracingOutcome) {
        this.phoneTracingOutcome = phoneTracingOutcome;
    }

    public String getPhysicalTracingOutcome() {
        return physicalTracingOutcome;
    }

    public void setPhysicalTracingOutcome(String physicalTracingOutcome) {
        this.physicalTracingOutcome = physicalTracingOutcome;
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

    public String getHomeAdministrationDate() {
        return homeAdministrationDate;
    }

    public void setHomeAdministrationDate(String homeAdministrationDate) {
        this.homeAdministrationDate = homeAdministrationDate;
    }

    public String getOtherFacilityAdministrationDate() {
        return otherFacilityAdministrationDate;
    }

    public void setOtherFacilityAdministrationDate(String otherFacilityAdministrationDate) {
        this.otherFacilityAdministrationDate = otherFacilityAdministrationDate;
    }

    public String getOtherFacilityName() {
        return otherFacilityName;
    }

    public void setOtherFacilityName(String otherFacilityName) {
        this.otherFacilityName = otherFacilityName;
    }

    public String getDateToConfirmVaccination() {
        return dateToConfirmVaccination;
    }

    public void setDateToConfirmVaccination(String dateToConfirmVaccination) {
        this.dateToConfirmVaccination = dateToConfirmVaccination;
    }

    public String getModeOfTracing() {
        return modeOfTracing;
    }

    public void setModeOfTracing(String modeOfTracing) {
        this.modeOfTracing = modeOfTracing;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
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
