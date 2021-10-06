package org.smartregister.kdp.pojo;

public class UpdateCovidDefaulterForm {

    private String id;
    private String baseEntityId;
    private String visitId;
    private String covidPhoneTracingOutcome;
    private String covidPhysicalTracingOutcome;
    private String covidPhoneTracing;
    private String covidPhysicalTracing;
    private String covidHomeAdministrationDate;
    private String covidOtherFacilityAdministrationDate;
    private String covidOtherFacilityName;
    private String covidDateToConfirmVaccination;
    private String covidModeOfTracing;
    private String age;
    private String date;
    private String createdAt;

    public UpdateCovidDefaulterForm() {
    }

    public UpdateCovidDefaulterForm(String id, String baseEntityId, String visitId, String covidPhoneTracingOutcome,
                                    String covidPhysicalTracingOutcome, String covidPhoneTracing, String covidPhysicalTracing,
                                    String covidHomeAdministrationDate, String covidOtherFacilityAdministrationDate, String covidOtherFacilityName,
                                    String covidDateToConfirmVaccination, String covidModeOfTracing, String age, String date, String createdAt) {
        this.id = id;
        this.baseEntityId = baseEntityId;
        this.visitId = visitId;
        this.covidPhoneTracingOutcome = covidPhoneTracingOutcome;
        this.covidPhysicalTracingOutcome = covidPhysicalTracingOutcome;
        this.covidPhoneTracing = covidPhoneTracing;
        this.covidPhysicalTracing = covidPhysicalTracing;
        this.covidHomeAdministrationDate = covidHomeAdministrationDate;
        this.covidOtherFacilityAdministrationDate = covidOtherFacilityAdministrationDate;
        this.covidOtherFacilityName = covidOtherFacilityName;
        this.covidDateToConfirmVaccination = covidDateToConfirmVaccination;
        this.covidModeOfTracing = covidModeOfTracing;
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

    public String getCovidPhoneTracingOutcome() {
        return covidPhoneTracingOutcome;
    }

    public void setCovidPhoneTracingOutcome(String covidPhoneTracingOutcome) {
        this.covidPhoneTracingOutcome = covidPhoneTracingOutcome;
    }

    public String getCovidPhysicalTracingOutcome() {
        return covidPhysicalTracingOutcome;
    }

    public void setCovidPhysicalTracingOutcome(String covidPhysicalTracingOutcome) {
        this.covidPhysicalTracingOutcome = covidPhysicalTracingOutcome;
    }

    public String getCovidPhoneTracing() {
        return covidPhoneTracing;
    }

    public void setCovidPhoneTracing(String covidPhoneTracing) {
        this.covidPhoneTracing = covidPhoneTracing;
    }

    public String getCovidPhysicalTracing() {
        return covidPhysicalTracing;
    }

    public void setCovidPhysicalTracing(String covidPhysicalTracing) {
        this.covidPhysicalTracing = covidPhysicalTracing;
    }

    public String getCovidHomeAdministrationDate() {
        return covidHomeAdministrationDate;
    }

    public void setCovidHomeAdministrationDate(String covidHomeAdministrationDate) {
        this.covidHomeAdministrationDate = covidHomeAdministrationDate;
    }

    public String getCovidOtherFacilityAdministrationDate() {
        return covidOtherFacilityAdministrationDate;
    }

    public void setCovidOtherFacilityAdministrationDate(String covidOtherFacilityAdministrationDate) {
        this.covidOtherFacilityAdministrationDate = covidOtherFacilityAdministrationDate;
    }

    public String getCovidOtherFacilityName() {
        return covidOtherFacilityName;
    }

    public void setCovidOtherFacilityName(String covidOtherFacilityName) {
        this.covidOtherFacilityName = covidOtherFacilityName;
    }

    public String getCovidDateToConfirmVaccination() {
        return covidDateToConfirmVaccination;
    }

    public void setCovidDateToConfirmVaccination(String covidDateToConfirmVaccination) {
        this.covidDateToConfirmVaccination = covidDateToConfirmVaccination;
    }

    public String getCovidModeOfTracing() {
        return covidModeOfTracing;
    }

    public void setCovidModeOfTracing(String covidModeOfTracing) {
        this.covidModeOfTracing = covidModeOfTracing;
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
