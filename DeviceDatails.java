package com.vin.server.monitor;

public class DeviceDatails {
	
	private int id=0;
	private String clientName="";
	private String ipAddress="";
	private String deviceName="";
	private int checkingPinStatus=0;
	private int checkingValueCompare=0;
	private int alarmStatus=0;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}


	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	
	public int getCheckingPinStatus() {
		return checkingPinStatus;
	}
	public void setCheckingPinStatus(int checkingPinStatus) {
		this.checkingPinStatus = checkingPinStatus;
	}
	public int getCheckingValueCompare() {
		return checkingValueCompare;
	}
	public void setCheckingValueCompare(int checkingValueCompare) {
		this.checkingValueCompare = checkingValueCompare;
	}
	public int getAlarmStatus() {
		return alarmStatus;
	}
	public void setAlarmStatus(int alarmStatus) {
		this.alarmStatus = alarmStatus;
	}
			
	

}
