package org.smartregister.kdp.domain;

public class InvalidClient {

    private String baseEntityId;
    private String jsonObject;

    public InvalidClient() {
    }

    public InvalidClient(String baseEntityId, String jsonObject) {
        this.baseEntityId = baseEntityId;
        this.jsonObject = jsonObject;
    }

    public String getBaseEntityId() {
        return baseEntityId;
    }

    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }

    public String getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(String jsonObject) {
        this.jsonObject = jsonObject;
    }

    @Override
    public String toString() {
        return "InvalidClient{" +
                "baseEntityId='" + baseEntityId + '\'' +
                ", jsonObject='" + jsonObject + '\'' +
                '}';
    }
}
