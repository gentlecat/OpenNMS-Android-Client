package org.opennms.android.data.api.model;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

public final class Outage {
    @SerializedName("@id")
    public long id;
    public String ipAddress;
    public DateTime ifLostService;
    public DateTime ifRegainedService;
    public Event serviceLostEvent;
    public Event serviceRegainedEvent;
    // TODO: Add monitoredService
}
