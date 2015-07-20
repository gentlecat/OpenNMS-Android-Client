package org.opennms.android.data.api.model;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

public final class Event {

    @SerializedName("@id") public long id;
    @SerializedName("@log") public String log;
    @SerializedName("@display") public String display;
    @SerializedName("@severity") public String severity;
    public String host;
    public String description;
    public String logMessage;
    public String source;
    public String uei;
    public String ipAddress;
    public int nodeId;
    public String nodeLabel;
    public ServiceType serviceType;
    public DateTime createTime;
}
