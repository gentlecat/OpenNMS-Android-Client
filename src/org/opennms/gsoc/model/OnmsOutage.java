package org.opennms.gsoc.model;

import java.io.Serializable;

public class OnmsOutage implements Serializable, Comparable<OnmsOutage>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String ipAddress;
	private String ifLostService;
	private String ifRegainedService;
	private String serviceTypeName;

	public OnmsOutage(Integer id, String ipAddress) {
		this.id = id;
		this.ipAddress = ipAddress;
	}

	public OnmsOutage(Integer id, String ipAddress, String ifLostService, String ifRegainedService, String serviceTypeName) {
		this.id = id;
		this.ipAddress = ipAddress;
		this.ifLostService = ifLostService;
		this.ifRegainedService = ifRegainedService;
		this.serviceTypeName = serviceTypeName;
	}

	public Integer getId() {
		return this.id;
	}

	public String getIpAddress() {
		return this.ipAddress;
	}

	public String getIfLostService() {
		return this.ifLostService;
	}

	public String getIfRegainedService() {
		return this.ifRegainedService;
	}

	public String getServiceTypeName() {
		return this.serviceTypeName;
	}

	@Override
	public String toString() {
		return "OnmsOutage [id=" + this.id + ", ipAddress=" + this.ipAddress
				+ ", ifLostService=" + this.ifLostService + ", ifRegainedService="
				+ this.ifRegainedService + "]";
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
