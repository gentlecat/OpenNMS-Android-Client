package org.opennms.gsoc.outages;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import org.opennms.gsoc.dao.DatabaseHelper;
import org.opennms.gsoc.outages.dao.OutagesListProvider;

import java.io.Serializable;

public class Outage implements Serializable {

    private Integer id;
    private String ipAddress;
    private String ifLostService;
    private String ifRegainedService;
    private String serviceTypeName;

    public Outage(Integer id, String ipAddress, String ifLostService, String ifRegainedService, String serviceTypeName) {
        this.id = id;
        this.ipAddress = ipAddress;
        this.ifLostService = ifLostService;
        this.ifRegainedService = ifRegainedService;
        this.serviceTypeName = serviceTypeName;
    }

    public Outage(ContentResolver contentResolver, long listItemId) {
        String projection[] = {
                DatabaseHelper.COL_OUTAGE_ID,
                DatabaseHelper.COL_IP_ADDRESS,
                DatabaseHelper.COL_IF_REGAINED_SERVICE,
                DatabaseHelper.COL_SERVICE_TYPE_NAME,
                DatabaseHelper.COL_IF_LOST_SERVICE
        };
        Cursor outagesCursor = contentResolver.query(
                Uri.withAppendedPath(OutagesListProvider.CONTENT_URI, String.valueOf(listItemId)),
                projection, null, null, null);
        if (outagesCursor.moveToFirst()) {
            id = outagesCursor.getInt(0);
            ipAddress = outagesCursor.getString(1);
            ifRegainedService = outagesCursor.getString(2);
            serviceTypeName = outagesCursor.getString(3);
            ifLostService = outagesCursor.getString(4);
        }
        outagesCursor.close();
    }

    public Integer getId() {
        return id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getIfLostService() {
        return ifLostService;
    }

    public String getIfRegainedService() {
        return ifRegainedService;
    }

    public String getServiceTypeName() {
        return serviceTypeName;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Outage ID: " + id + "\n");
        builder.append("IP address: " + ipAddress + "\n");
        builder.append("If lost service: " + ifLostService + "\n");
        builder.append("If regained service: " + ifRegainedService + "\n");
        builder.append("Service type name: " + serviceTypeName + "\n");
        return builder.toString();
    }

}
