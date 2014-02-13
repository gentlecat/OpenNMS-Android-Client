package org.opennms.android.data.api.model;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

public final class Node {

  @SerializedName("@id") public long id;
  @SerializedName("@label") public String label;
  public String labelSource;
  @SerializedName("@type") public String type;
  public String sysContact;
  public String sysDescription;
  public String sysLocation;
  public String sysName;
  public String sysObjectId;
  public AssetRecord assetRecord;
  public DateTime createTime;
  // TODO: Add categories
}
