package org.opennms.gsoc.model;

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
