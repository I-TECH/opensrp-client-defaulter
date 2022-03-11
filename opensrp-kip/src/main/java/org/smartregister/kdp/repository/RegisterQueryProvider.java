package org.smartregister.kdp.repository;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.commonregistry.CommonFtsObject;

public class RegisterQueryProvider {
    public RegisterQueryProvider() {
    }

    public String getObjectIdsQuery(String mainCondition, String filters) {
        String strMainCondition = this.getMainCondition(mainCondition);
        String strFilters = this.getFilter(filters);
        if (StringUtils.isNotBlank(strFilters) && StringUtils.isBlank(strMainCondition)) {
            strFilters = String.format(" where " + this.getDemographicTable() + ".phrase MATCH '*%s*'", filters);
        }

        return "select " + this.getDemographicTable() + ".object_id from " + CommonFtsObject.searchTableName(this.getDemographicTable()) + " " + this.getDemographicTable() + "  join " + this.getChildDetailsTable() + " on " + this.getDemographicTable() + ".object_id =  " + this.getChildDetailsTable() + ".id left join " + CommonFtsObject.searchTableName(this.getChildDetailsTable()) + " on " + this.getDemographicTable() + ".object_id =  " + CommonFtsObject.searchTableName(this.getChildDetailsTable()) + ".object_id " + strMainCondition + strFilters;
    }

    private String getFilter(String filters) {
        return StringUtils.isNotBlank(filters) ? String.format(" AND " + this.getDemographicTable() + ".phrase MATCH '*%s*'", filters) : "";
    }

    private String getMainCondition(String mainCondition) {
        return !StringUtils.isBlank(mainCondition) ? " where " + mainCondition : "";
    }

    public String getCountExecuteQuery(String mainCondition, String filters) {
        String strMainCondition = this.getMainCondition(mainCondition);
        String strFilters = this.getFilter(filters);
        if (StringUtils.isNotBlank(strFilters) && StringUtils.isBlank(strMainCondition)) {
            strFilters = String.format(" where " + this.getDemographicTable() + ".phrase MATCH '*%s*'", filters);
        }

        return "select count(" + this.getDemographicTable() + ".object_id) from " + CommonFtsObject.searchTableName(this.getDemographicTable()) + " " + this.getDemographicTable() + "  join " + this.getChildDetailsTable() + " on " + this.getDemographicTable() + ".object_id =  " + this.getChildDetailsTable() + ".id left join " + CommonFtsObject.searchTableName(this.getChildDetailsTable()) + " on " + this.getDemographicTable() + ".object_id =  " + CommonFtsObject.searchTableName(this.getChildDetailsTable()) + ".object_id " + strMainCondition + strFilters;
    }

    public String mainRegisterQuery() {
        return "select " + StringUtils.join(this.mainColumns(), ",") + " from " + this.getChildDetailsTable() + " join " + this.getMotherDetailsTable() + " on " + this.getChildDetailsTable() + "." + "relational_id" + " = " + this.getMotherDetailsTable() + "." + "base_entity_id" + " join " + this.getDemographicTable() + " on " + this.getDemographicTable() + "." + "base_entity_id" + " = " + this.getChildDetailsTable() + "." + "base_entity_id" + " join " + this.getDemographicTable() + " mother on mother." + "base_entity_id" + " = " + this.getMotherDetailsTable() + "." + "base_entity_id";
    }

    public String mainRegisterQuery(String select) {
        if (StringUtils.isBlank(select)) {
            select = StringUtils.join(this.mainColumns(), ",");
        }

        return "select " + select + " from " + this.getChildDetailsTable() + " join " + this.getMotherDetailsTable() + " on " + this.getChildDetailsTable() + "." + "relational_id" + " = " + this.getMotherDetailsTable() + "." + "base_entity_id" + " join " + this.getDemographicTable() + " on " + this.getDemographicTable() + "." + "base_entity_id" + " = " + this.getChildDetailsTable() + "." + "base_entity_id" + " join " + this.getDemographicTable() + " mother on mother." + "base_entity_id" + " = " + this.getMotherDetailsTable() + "." + "base_entity_id";
    }

    public String[] mainColumns() {
        return new String[]{this.getDemographicTable() + "." + "id" + " as _id", this.getDemographicTable() + "." + "relationalid", this.getDemographicTable() + "." + "zeir_id", this.getChildDetailsTable() + "." + "relational_id", this.getDemographicTable() + "." + "gender", this.getDemographicTable() + "." + "base_entity_id", this.getDemographicTable() + "." + "first_name", this.getDemographicTable() + "." + "last_name", "mother.first_name as mother_first_name", "mother.last_name as mother_last_name", this.getDemographicTable() + "." + "dob", "mother.dob as mother_dob", this.getMotherDetailsTable() + "." + "nrc_number" + " as mother_nrc_number", this.getMotherDetailsTable() + "." + "father_name", this.getMotherDetailsTable() + "." + "epi_card_number", this.getDemographicTable() + "." + "client_reg_date", this.getChildDetailsTable() + "." + "pmtct_status", this.getDemographicTable() + "." + "last_interacted_with", this.getChildDetailsTable() + "." + "inactive", this.getChildDetailsTable() + "." + "lost_to_follow_up", this.getChildDetailsTable() + "." + "mother_guardian_phone_number", this.getDemographicTable() + ".address1"};
    }

    public String getChildDetailsTable() {
        return "ec_child_details";
    }

    public String getMotherDetailsTable() {
        return "ec_mother_details";
    }

    public String getDemographicTable() {
        return "ec_client";
    }
}
