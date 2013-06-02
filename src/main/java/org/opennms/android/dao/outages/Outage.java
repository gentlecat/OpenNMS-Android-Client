package org.opennms.android.dao.outages;

import java.io.Serializable;

public class Outage implements Serializable {

    private Integer id;
    private String ipAddress;
    private String ifLostService;
    private String ifRegainedService;
    private String serviceTypeName;

    public Outage(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIfLostService() {
        return ifLostService;
    }

    public void setIfLostService(String ifLostService) {
        this.ifLostService = ifLostService;
    }

    public String getIfRegainedService() {
        return ifRegainedService;
    }

    public void setIfRegainedService(String ifRegainedService) {
        this.ifRegainedService = ifRegainedService;
    }

    public String getServiceTypeName() {
        return serviceTypeName;
    }

    public void setServiceTypeName(String serviceTypeName) {
        this.serviceTypeName = serviceTypeName;
    }

}
