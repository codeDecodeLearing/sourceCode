package com.vin.server.monitor;

import javax.swing.CellEditor;

public class SmsSendOut {

	private int id=0;
	private String mobileNo="";
	private String Messages="";
	private long occurredOn;
	private int sendStatus=0;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getMessages() {
		return Messages;
	}
	public void setMessages(String messages) {
		Messages = messages;
	}
	public long getOccurredOn() {
		return occurredOn;
	}
	public void setOccurredOn(long occurredOn) {
		this.occurredOn = occurredOn;
	}
	@Override
	public String toString() {
		return "SmsSendOut [id=" + id + ", mobileNo=" + mobileNo + ", Messages=" + Messages + ", occurredOn="
				+ occurredOn + "]";
	}
	public int getSendStatus() {
		return sendStatus;
	}
	public void setSendStatus(int sendStatus) {
		this.sendStatus = sendStatus;
	}
	
}
