package org.smartregister.kdp.exceptions;

import androidx.annotation.NonNull;

public class RecordDefaulterException extends Exception {
    public RecordDefaulterException() {
       super("Could not process this OPD Record Defaulter Tracing  Event");
    }

    public RecordDefaulterException(@NonNull String message) {
        super("Could not process this OPD Record Defaulter Tracing  Event because " + message);
    }
}
