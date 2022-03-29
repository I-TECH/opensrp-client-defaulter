package org.smartregister.kdp.util;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 13-03-2020.
 */
public interface DbConstants {

    interface Table {
        interface Hia2IndicatorsRepository {

            String TABLE_NAME = "indicators";
            String ID = "_id";
            String INDICATOR_CODE = "indicator_code";
            String DESCRIPTION = "description";
        }

        interface MonthlyTalliesRepository {
            String TABLE_NAME = "monthly_tallies";
            String ID = "_id";
            String PROVIDER_ID = "provider_id";
            String INDICATOR_CODE = "indicator_code";
            String VALUE = "value";
            String MONTH = "month";
            String EDITED = "edited";
            String DATE_SENT = "date_sent";
            String INDICATOR_GROUPING = "indicator_grouping";
            String UPDATED_AT = "updated_at";
            String CREATED_AT = "created_at";
        }
    }

    public interface OpdDetails {
        String ID = "_id";
        String BASE_ENTITY_ID = "base_entity_id";
        String PENDING_DIAGNOSE_AND_TREAT = "pending_diagnose_and_treat";
        String CURRENT_VISIT_START_DATE = "current_visit_start_date";
        String CURRENT_VISIT_END_DATE = "current_visit_end_date";
        String CURRENT_VISIT_ID = "visit_id";
        String CREATED_AT = "created_at";
    }
}
