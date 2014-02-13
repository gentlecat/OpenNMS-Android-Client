package org.opennms.android.data.api.model;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

public final class Alarm {

  @SerializedName("@id") public long id;
  @SerializedName("@count") public int count;
  @SerializedName("@type") public int type;
  @SerializedName("@severity") public String severity;
  public String parms;
  public String description;
  public DateTime ackTime;
  public String ackUser;
  public String logMessage;
  public Event lastEvent;
  public String uei;
  public String ipAddress;
  public int nodeId;
  public String nodeLabel;
  public ServiceType serviceType;
  public String reductionKey;
  public DateTime firstEventTime;
  public DateTime lastEventTime;
  public DateTime suppressedTime;
  public DateTime suppressedUntil;
}
