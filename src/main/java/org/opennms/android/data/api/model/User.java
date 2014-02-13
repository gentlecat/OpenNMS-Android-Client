package org.opennms.android.data.api.model;

import com.google.gson.annotations.SerializedName;

public final class User {

  @SerializedName("user-id") public long id;
  @SerializedName("full-name") public String fullName;
  @SerializedName("user-comments") public String userComments;
  public String password;
  public boolean passwordSalt;
}
