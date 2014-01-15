package org.opennms.android.settings;

import android.os.Parcel;
import android.os.Parcelable;

public class Configuration implements Parcelable {
    // Connection
    public String host;
    public int port;
    public boolean isHttps;
    public String restUrl;
    public String user;
    public String password;

    // Notifications and sync
    public boolean notificationsOn;
    public String minSeverity;
    public int syncRate;
    public boolean isWifiOnly;

    public Configuration() {
    }

    private Configuration(Parcel in) {
        host = in.readString();
        port = in.readInt();
        isHttps = in.readInt() == 1;
        restUrl = in.readString();
        user = in.readString();
        password = in.readString();

        notificationsOn = in.readInt() == 1;
        minSeverity = in.readString();
        syncRate = in.readInt();
        isWifiOnly = in.readInt() == 1;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(host);
        out.writeInt(port);
        out.writeInt(isHttps ? 1 : 0);
        out.writeString(restUrl);
        out.writeString(user);
        out.writeString(password);

        out.writeInt(notificationsOn ? 1 : 0);
        out.writeString(minSeverity);
        out.writeInt(syncRate);
        out.writeInt(isWifiOnly ? 1 : 0);
    }

    public static final Parcelable.Creator<Configuration> CREATOR = new Parcelable.Creator<Configuration>() {
        public Configuration createFromParcel(Parcel in) {
            return new Configuration(in);
        }

        public Configuration[] newArray(int size) {
            return new Configuration[size];
        }
    };
}