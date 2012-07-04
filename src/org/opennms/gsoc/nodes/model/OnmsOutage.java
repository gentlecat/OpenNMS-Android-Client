package org.opennms.gsoc.nodes.model;

import java.io.Serializable;

public class OnmsOutage implements Serializable, Comparable<OnmsOutage>{
	private Integer id;
	private String ipAddress;
	private String ifLostService;
	private String ifRegainedService;
	
	public OnmsOutage(Integer id, String ipAddress, String ifLostService, String ifRegainedService) {
		this.id = id;
		this.ipAddress = ipAddress;
		this.ifLostService = ifLostService;
		this.ifRegainedService = ifRegainedService;
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

	@Override
	public String toString() {
		return "OnmsOutage [id=" + id + ", ipAddress=" + ipAddress
				+ ", ifLostService=" + ifLostService + ", ifRegainedService="
				+ ifRegainedService + "]";
	}

	@Override
	public int compareTo(OnmsOutage o) {
        Integer compareId = 0;

        if (o != null) {
            compareId = o.getId();
        }

        return this.getId().compareTo(compareId);
	}
	
}
