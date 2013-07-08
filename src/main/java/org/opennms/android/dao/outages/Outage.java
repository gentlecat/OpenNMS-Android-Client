package org.opennms.android.dao.outages;

import java.io.Serializable;

public class Outage implements Serializable {

    private int id;
    private int serviceId;
    private int ipInterfaceId;
    private String ipAddress;
    private String lostServiceTime;
    private int serviceLostEventId;
    private String regainedServiceTime;
    private int serviceRegainedEventId;
    private int serviceTypeId;
    private String serviceTypeName;

    public Outage(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(int serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public int getIpInterfaceId() {
        return ipInterfaceId;
    }

    public void setIpInterfaceId(int ipInterfaceId) {
        this.ipInterfaceId = ipInterfaceId;
    }

    public int getServiceLostEventId() {
        return serviceLostEventId;
    }

    public void setServiceLostEventId(int serviceLostEventId) {
        this.serviceLostEventId = serviceLostEventId;
    }

    public int getServiceRegainedEventId() {
        return serviceRegainedEventId;
    }

    public void setServiceRegainedEventId(int serviceRegainedEventId) {
        this.serviceRegainedEventId = serviceRegainedEventId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getLostServiceTime() {
        return lostServiceTime;
    }

    public void setLostServiceTime(String lostServiceTime) {
        this.lostServiceTime = lostServiceTime;
    }

    public String getRegainedServiceTime() {
        return regainedServiceTime;
    }

    public void setRegainedServiceTime(String regainedServiceTime) {
        this.regainedServiceTime = regainedServiceTime;
    }

    public String getServiceTypeName() {
        return serviceTypeName;
    }

    public void setServiceTypeName(String serviceTypeName) {
        this.serviceTypeName = serviceTypeName;
    }

}
