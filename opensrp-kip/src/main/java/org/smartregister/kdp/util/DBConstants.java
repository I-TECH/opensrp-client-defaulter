package org.smartregister.kdp.util;

/**
 * Created by Stephen.
 */
public class DBConstants {

    public interface RegisterTable {
        String CHILD_DETAILS = "ec_child_details";
        String MOTHER_DETAILS = "ec_mother_details";
        String CLIENT = "ec_client";
        String FATHER_DETAILS = "ec_father_details";
    }

    public static final class KEY {
        public static final String FIRST_NAME = "first_name";
        public static final String LAST_NAME = "last_name";
        public static final String BASE_ENTITY_ID = "base_entity_id";
        public static final String ZEIR_ID = "zeir_id";
        public static final String INACTIVE = "inactive";
        public static final String LOST_TO_FOLLOW_UP = "lost_to_follow_up";

        public KEY() {
        }
    }
}
