package org.opennms.android.data.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public final class Events {
    @SerializedName("event")
    public List<Event> events;
}
