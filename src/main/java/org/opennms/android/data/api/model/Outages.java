package org.opennms.android.data.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public final class Outages {

  @SerializedName("outage") public List<Outage> outages;
}
