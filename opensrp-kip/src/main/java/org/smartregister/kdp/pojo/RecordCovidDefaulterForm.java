package org.smartregister.kdp.pojo;

public class RecordCovidDefaulterForm {

    private String id;
    private String baseEntityId;
    private String visitId;
    private String covidAntigenAdministeredLast;
    private String covidAdministrationDate;
    private String covidMissedDoses;
    private String covidReturnDate;
    private String covidChvName;
    private String covidChvPhoneNumber;
    private String covidBirthDoseAntigen;
    private String age;
    private String date;
    private String createdAt;

    public RecordCovidDefaulterForm() {
    }

    public RecordCovidDefaulterForm(String id, String baseEntityId, String visitId, String covidAntigenAdministeredLast,
                                    String covidAdministrationDate, String covidMissedDoses, String covidReturnDate, String covidChvName,
                                    String covidChvPhoneNumber, String covidBirthDoseAntigen, String age, String date, String createdAt) {
        this.id = id;
        this.baseEntityId = baseEntityId;
        this.visitId = visitId;
        this.covidAntigenAdministeredLast = covidAntigenAdministeredLast;
        this.covidAdministrationDate = covidAdministrationDate;
        this.covidMissedDoses = covidMissedDoses;
        this.covidReturnDate = covidReturnDate;
        this.covidChvName = covidChvName;
        this.covidChvPhoneNumber = covidChvPhoneNumber;
        this.covidBirthDoseAntigen = covidBirthDoseAntigen;
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

    public String getCovidAntigenAdministeredLast() {
        return covidAntigenAdministeredLast;
    }

    public void setCovidAntigenAdministeredLast(String covidAntigenAdministeredLast) {
        this.covidAntigenAdministeredLast = covidAntigenAdministeredLast;
    }

    public String getCovidAdministrationDate() {
        return covidAdministrationDate;
    }

    public void setCovidAdministrationDate(String covidAdministrationDate) {
        this.covidAdministrationDate = covidAdministrationDate;
    }

    public String getCovidMissedDoses() {
        return covidMissedDoses;
    }

    public void setCovidMissedDoses(String covidMissedDoses) {
        this.covidMissedDoses = covidMissedDoses;
    }

    public String getCovidReturnDate() {
        return covidReturnDate;
    }

    public void setCovidReturnDate(String covidReturnDate) {
        this.covidReturnDate = covidReturnDate;
    }

    public String getCovidChvName() {
        return covidChvName;
    }

    public void setCovidChvName(String covidChvName) {
        this.covidChvName = covidChvName;
    }

    public String getCovidChvPhoneNumber() {
        return covidChvPhoneNumber;
    }

    public void setCovidChvPhoneNumber(String covidChvPhoneNumber) {
        this.covidChvPhoneNumber = covidChvPhoneNumber;
    }

    public String getCovidBirthDoseAntigen() {
        return covidBirthDoseAntigen;
    }

    public void setCovidBirthDoseAntigen(String covidBirthDoseAntigen) {
        this.covidBirthDoseAntigen = covidBirthDoseAntigen;
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
