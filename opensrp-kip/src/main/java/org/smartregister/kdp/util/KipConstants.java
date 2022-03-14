package org.smartregister.kdp.util;

public class KipConstants {

    public static final String AGE = "age";
    public static final String VALUE = "value";

    public interface DateFormat {
        String HH_MM_AMPM = "h:mm a";
    }

    public interface RegisterType {
        String CHILD = "child";
        String OPD = "opd";
        String ALL_CLIENTS = "all_clients";
    }

    public static final class KEY {
        public static final String CHILD = "child";
        public static final String FIRST_NAME = "first_name";
        public static final String LAST_NAME = "last_name";
        public static final String BIRTHDATE = "birthdate";
        public static final String ZEIR_ID = "zeir_id";
        public static final String OPENMRS_ID = "OPENMRS_ID";
        public static final String LOST_TO_FOLLOW_UP = "lost_to_follow_up";
        public static final String GENDER = "gender";
        public static final String INACTIVE = "inactive";
        public static final String LAST_INTERACTED_WITH = "last_interacted_with";
        public static final String ID_LOWER_CASE = "_id";
        public static final String BASE_ENTITY_ID = "base_entity_id";
        public static final String DOB = "dob";//Date Of Birth
        public static final String DOD = "dod";//Date Of Birth
        public static final String DATE_REMOVED = "date_removed";
        public static final String MOTHER_NRC_NUMBER = "nrc_number";
        public static final String MOTHER_GUARDIAN_NUMBER = "mother_guardian_number";
        public static final String MOTHER_SECOND_PHONE_NUMBER = "second_phone_number";
        public static final String VIEW_CONFIGURATION_PREFIX = "ViewConfiguration_";
        public static final String KIP_ID = "zeir_id";
        public static final String MIDDLE_NAME = "middle_name";
        public static final String ADDRESS_3 = "address3";
        public static final String BIRTH_FACILITY_NAME = "Birth_Facility_Name";
        public static final String RESIDENTIAL_AREA = "Residential_Area";
        public static final String IDENTIFIERS = "identifiers";
        public static final String FIRSTNAME = "firstName";
        public static final String MIDDLENAME = "middleName";
        public static final String LASTNAME = "lastName";
        public static final String ATTRIBUTES = "attributes";
        public static final int FIVE_YEAR = 5;
        public static String SITE_CHARACTERISTICS = "site_characteristics";
        public static final String KEY = "key";
        public static final String FATHER_PHONE = "father_phone";
        public static final String ENCOUNTER_TYPE = "encounter_type";
    }

    public static final class DrawerMenu {
        public static final String OPD_CLIENTS = "Defaulter Child";
        public static final String ALL_CLIENTS = "All Clients";
        public static final String KEPI_CLIENT = "KEPI Clients";
        public static final String COVID_19_CLIENT = "COVID 19 Clients";
    }

    public static class CONFIGURATION {
        public static final String LOGIN = "login";

    }

    public static final class EventType {
        public static final String OPD_SMS_REMINDER = "OPD SMS Reminder";
        public static final String UPDATE_DEFAULT = "Tracing Outcome";
        public static final String RECORD_DEFAULTER_FORM = "Record Defaulter Form";
        public static final String RECORD_COVID_DEFAULTER_FORM = "Record Covid 19 Defaulter Form";
        public static final String UPDATE_COVID_DEFAULT = "Covid 19 Tracing Outcome";
    }

    public static class JSON_FORM {
        public static final String OPD_RECORD_DEFAULTER_FORM = "opd_last_vaccine_given";
        public static final String OPD_UPDATE_DEFAULTER_FORM = "opd_update_defaulter_form";

        public static final String OPD_COVID_DEFAULTER_FORM = "opd_covid_defaulter_form";
        public static final String OPD_UPDATE_COVID_DEFAULTER_FORM = "opd_update_covid_defaulter_form";

    }

    public static class TABLE_NAME {
        public static final String ALL_CLIENTS = "ec_client";
        public static final String REGISTER_TYPE = "client_register_type";
        public static final String CHILD = "ec_client";
        public static final String CHILD_UPDATED_ALERTS = "child_updated_alerts";
    }

    public interface Columns {
        interface RegisterType {
            String BASE_ENTITY_ID = "base_entity_id";
            String REGISTER_TYPE = "register_type";
            String DATE_REMOVED = "date_removed";
            String DATE_CREATED = "date_created";
        }
    }

    public class IntentKeyUtils {
        public static final String IS_REMOTE_LOGIN = "is_remote_login";
    }

    public interface Pref {
        String APP_VERSION_CODE = "APP_VERSION_CODE";
    }

    public static final class KeyUtils {
        public static final String ID = "_ID";
        public static final String KEY = "key";
        public static final String BASE_ENTITY_ID = "base_entity_id";
        public static final String LAST_INTERACTED_WITH = "last_interacted_with";
    }

    public interface Settings {
        String VACCINE_STOCK_IDENTIFIER = "covid19_vaccine_stock";
    }

    public interface DbConstants {
        interface Tables {
            String OPD_SMS_REMINDER = "opd_sms_reminder";

            String RECORD_DEFAULTER_FORM = "last_vaccine_given_table";
            String UPDATE_DEFAULTER_FORM_TABLE = "update_defaulter_form_table";

            String RECORD_COVID_DEFAULTER_FORM = "covid_defaulter_form_table";
            String UPDATE_COVID_DEFAULTER_FORM_TABLE = "update_covid_defaulter_form_table";
        }

        interface Columns {

            interface RecordCovidDefaulerForm {
                String ID = "id";
                String VISIT_ID = "visit_id";
                String VISIT_DATE = "visit_date";
                String BASE_ENTITY_ID = "base_entity_id";
                String VISIT_TYPE = "visit_type";
                String COVID_ANTIGEN_ADMINISTERED_LAST = "covid_antigen_administered_last";
                String COVID_ADMINISTRATION_DATE = "covid_administration_date";
                String COVID_MISSED_VACCINE = "covid_missed_doses";
                String COVID_RETURN_DATE = "covid_return_date";
                String COVID_CHV_NAME = "covid_chv_name";
                String COVID_CHV_PHONE_NUMBER = "covid_chv_phone_number";
                String AGE = "age";
                String DATE = "date";
                String CREATED_AT = "created_at";
            }

            interface UpdateCovidDefaulterForm {
                String ID = "id";
                String VISIT_ID = "visit_id";
                String BASE_ENTITY_ID = "base_entity_id";
                String VISIT_TYPE = "visit_type";
                String COVID_PHONE_TRACING_OUTCOME = "covid_phone_tracing_outcome";
                String COVID_PHYSICAL_TRACING_OUTCOME = "covid_physical_tracing_outcome";
                String COVID_PHONE_TRACING = "covid_phone_tracing";
                String COVID_PHYSICAL_TRACING = "covid_physical_tracing";
                String COVID_HOME_ADMINISTRATION_DATE = "covid_home_administration_date";
                String COVID_OTHER_FACILITY_ADMINISTRATION_DATE = "covid_other_facility_administration_date";
                String COVID_OTHER_FACILITY_NAME = "covid_other_facility_name";
                String COVID_DATE_TO_CONFIRM_VACCINATION = "covid_date_to_confirm_vaccination";
                String COVID_MODE_OF_TRACING = "covid_mode_of_tracing";
                String DATE = "date";
                String AGE = "age";
                String CREATED_AT = "created_at";

            }

            interface RecordDefaulerForm {
                String ID = "id";
                String VISIT_ID = "visit_id";
                String VISIT_DATE = "visit_date";
                String BASE_ENTITY_ID = "base_entity_id";
                String VISIT_TYPE = "visit_type";
                String ANTIGEN_ADMINISTERED_LAST = "antigen_administered_last";
                String ADMINISTRATION_DATE = "administration_date";
                String MISSED_VACCINE = "missed_doses";
                String RETURN_DATE = "return_date";
                String CHV_NAME = "chv_name";
                String CHV_PHONE_NUMBER = "chv_phone_number";
                String BIRTH_DOSE_ANTIGEN = "last_birth_dose_antigen";
                String SIX_WKS_ANTIGEN = "last_six_wks_antigen";
                String TEN_WKS_ANTIGEN = "last_ten_wks_antigen";
                String FOURTEEN_WKS_ANTIGEN = "last_fourteen_wks_antigen";
                String NINE_MONTHS_ANTIGEN = "last_nine_months_antigen";
                String EIGHTEEN_MONTHS_ANTIGEN = "last_eighteen_months_antigen";
                String AGE = "age";
                String DATE = "date";
                String CREATED_AT = "created_at";
            }

            interface UpdateDefaulterForm {
                String ID = "id";
                String VISIT_ID = "visit_id";
                String BASE_ENTITY_ID = "base_entity_id";
                String VISIT_TYPE = "visit_type";
                String PHONE_TRACING_OUTCOME = "phone_tracing_outcome";
                String PHYSICAL_TRACING_OUTCOME = "physical_tracing_outcome";
                String PHONE_TRACING = "phone_tracing";
                String PHYSICAL_TRACING = "physical_tracing";
                String HOME_ADMINISTRATION_DATE = "home_administration_date";
                String OTHER_FACILITY_ADMINISTRATION_DATE = "other_facility_administration_date";
                String OTHER_FACILITY_NAME = "other_facility_name";
                String DATE_TO_CONFIRM_VACCINATION = "date_to_confirm_vaccination";
                String MODE_OF_TRACING = "mode_of_tracing";
                String DATE = "date";
                String AGE = "age";
                String CREATED_AT = "created_at";

            }
        }
    }
}
