package org.smartregister.kdp.exceptions;

import androidx.annotation.NonNull;

public class SmsReminderException extends Exception {
    public SmsReminderException() {
       super("Could not process this OPD SMS Reminder  Event");
    }

    public SmsReminderException(@NonNull String message) {
        super("Could not process this OPD SMS Reminder Event because " + message);
    }

}
