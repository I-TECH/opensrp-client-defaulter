package org.smartregister.kdp.listener;

import org.jetbrains.annotations.Nullable;

public interface OnLocationChangeListener {
    void updateUi(@Nullable String location);
}
