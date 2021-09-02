package org.smartregister.kdp.pojo;

public class RecordDefaulterForm {

    private String id;
    private String baseEntityId;
    private String visitId;
    private String antigenAdministeredLast;
    private String administrationDate;
    private String missedDoses;
    private String returnDate;
    private String chvName;
    private String chvPhoneNumber;
    private String birthDoseAntigen;
    private String sixWksAntigen;
    private String tenWksAntigen;
    private String fourteenWksAntigen;
    private String nineMonthsAntigen;
    private String eighteenMonthsAntigen;
    private String age;
    private String date;
    private String createdAt;

    public RecordDefaulterForm() {
    }

    public RecordDefaulterForm(String id, String baseEntityId, String visitId, String antigenAdministeredLast, String administrationDate, String missedDoses, String returnDate, String chvName, String chvPhoneNumber, String birthDoseAntigen, String sixWksAntigen, String tenWksAntigen, String fourteenWksAntigen, String nineMonthsAntigen, String eighteenMonthsAntigen, String age, String date, String createdAt) {
        this.id = id;
        this.baseEntityId = baseEntityId;
        this.visitId = visitId;
        this.antigenAdministeredLast = antigenAdministeredLast;
        this.administrationDate = administrationDate;
        this.missedDoses = missedDoses;
        this.returnDate = returnDate;
        this.chvName = chvName;
        this.chvPhoneNumber = chvPhoneNumber;
        this.birthDoseAntigen = birthDoseAntigen;
        this.sixWksAntigen = sixWksAntigen;
        this.tenWksAntigen = tenWksAntigen;
        this.fourteenWksAntigen = fourteenWksAntigen;
        this.nineMonthsAntigen = nineMonthsAntigen;
        this.eighteenMonthsAntigen = eighteenMonthsAntigen;
        this.age = age;
        this.date = date;
        this.createdAt = createdAt;
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

    public String getMissedDoses() {
        return missedDoses;
    }

    public void setMissedDoses(String missedDoses) {
        this.missedDoses = missedDoses;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
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

    public String getAntigenAdministeredLast() {
        return antigenAdministeredLast;
    }

    public void setAntigenAdministeredLast(String antigenAdministeredLast) {
        this.antigenAdministeredLast = antigenAdministeredLast;
    }

    public String getAdministrationDate() {
        return administrationDate;
    }

    public void setAdministrationDate(String administrationDate) {
        this.administrationDate = administrationDate;
    }

    public String getBirthDoseAntigen() {
        return birthDoseAntigen;
    }

    public void setBirthDoseAntigen(String birthDoseAntigen) {
        this.birthDoseAntigen = birthDoseAntigen;
    }

    public String getSixWksAntigen() {
        return sixWksAntigen;
    }

    public void setSixWksAntigen(String sixWksAntigen) {
        this.sixWksAntigen = sixWksAntigen;
    }

    public String getTenWksAntigen() {
        return tenWksAntigen;
    }

    public void setTenWksAntigen(String tenWksAntigen) {
        this.tenWksAntigen = tenWksAntigen;
    }

    public String getFourteenWksAntigen() {
        return fourteenWksAntigen;
    }

    public void setFourteenWksAntigen(String fourteenWksAntigen) {
        this.fourteenWksAntigen = fourteenWksAntigen;
    }

    public String getNineMonthsAntigen() {
        return nineMonthsAntigen;
    }

    public void setNineMonthsAntigen(String nineMonthsAntigen) {
        this.nineMonthsAntigen = nineMonthsAntigen;
    }

    public String getEighteenMonthsAntigen() {
        return eighteenMonthsAntigen;
    }

    public void setEighteenMonthsAntigen(String eighteenMonthsAntigen) {
        this.eighteenMonthsAntigen = eighteenMonthsAntigen;
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
