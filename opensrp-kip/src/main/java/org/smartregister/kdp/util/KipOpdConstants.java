package org.smartregister.kdp.util;

import org.smartregister.opd.utils.OpdConstants;

public class KipOpdConstants extends OpdConstants {
    public interface KipForms {
        String OPD_SMS_REMINDER = "opd_sms_reminder";
    }

    public interface RegisterType {
        String CHILD = "child";
        String OPD = "opd";
    }

    public interface FactKey {

        interface OpdVisit {
            String VISIT_DATE = "visit_date";

            String ANTIGEN_ADMINISTERED_LAST = "antigen_administered_last";
            String ANTIGEN_ADMINISTERED_LAST_LABEL = "antigen_administered_last_label";
            String DADMINISTRATION_DATE = "administration_date";
            String DADMINISTRATION_DATE_LABEL = "administration_date_label";


            String ANTIGEN_MISSED = "missed_doses";
            String ANTIGEN_MISSED_LABEL = "antigen_missed_label";
            String RETURN_DATE = "return_date";
            String RETURN_DATE_LABEL = "return_date_label";

            String PHONE_TRACING = "phone_tracing";
            String PHONE_TRACING_LABEL = "phone_tracing_label";
            String PHYSICAL_TRACING = "physical_tracing";
            String PHYSICAL_TRACING_LABEL = "physical_tracing_label";


            String CHV_PHONE_NUMBER = "chv_phone_number";
            String CHV_PHONE_NUMBER_LABEL = "chv_phone_number_label";
            String CHW_NAME = "chv_name";
            String CHW_NAME_LABEL = "chv_name_label";



            String PHONE_TRACING_OUTCOME = "phone_tracing_outcome";
            String PHYSICAL_TRACING_OUTCOME = "physical_tracing_outcome";
            String TRACING_OUTCOME_LABEL = "tracing_outcome_label";
            String HOME_ADMINISTRATION_DATE = "home_administration_date";
            String HOME_ADMINISTRATION_DATE_LABEL = "home_administration_date_label";
            String OTHER_FACILITY_ADMINISTRATION_DATE = "other_facility_administration_date";
            String OTHER_FACILITY_ADMINISTRATION_DATE_LABEL = "other_facility_administration_date_label";
            String OTHER_FACILITY_NAME = "other_facility_name";
            String OTHER_FACILITY_NAME_LABEL = "other_facility_name_label";
            String DATE_TO_CONFIRM_VACCINATION = "date_to_confirm_vaccination";
            String DATE_TO_CONFIRM_VACCINATION_LABEL = "date_to_confirm_vaccination_label";

        }

    }
}
