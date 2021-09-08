package com.vin.server.monitor;

public class Users {
	
	private int id;
	private String name="";
	private String mobileNp="";
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobileNp() {
		return mobileNp;
	}
	public void setMobileNp(String mobileNp) {
		this.mobileNp = mobileNp;
	}
	@Override
	public String toString() {
		return "Users [id=" + id + ", name=" + name + ", mobileNp=" + mobileNp + "]";
	}

}

changes happen
