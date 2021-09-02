package org.smartregister.kdp.exceptions;

import androidx.annotation.NonNull;

public class UpdateDefaulterTracingException extends Exception {
    public UpdateDefaulterTracingException() {
       super("Could not process this OPD update Defaulter Tracing Event");
    }

    public UpdateDefaulterTracingException(@NonNull String message) {
        super("Could not process this OPD update Defaulter Tracing Event because " + message);
    }
}
