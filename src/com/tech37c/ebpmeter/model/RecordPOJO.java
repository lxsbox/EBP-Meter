package com.tech37c.ebpmeter.model;

public class RecordPOJO {
	private String Dev_Type;
	private String Dev_ID;
	private String User_ID;
	private String Measure_Time;
	private String HBP;
	private String LBP;
	private String Beat;
	
	public String getDev_Type() {
		return Dev_Type;
	}
	public void setDev_Type(String dev_Type) {
		Dev_Type = dev_Type;
	}
	public String getDev_ID() {
		return Dev_ID;
	}
	public void setDev_ID(String dev_ID) {
		Dev_ID = dev_ID;
	}
	public String getUser_ID() {
		return User_ID;
	}
	public void setUser_ID(String user_ID) {
		User_ID = user_ID;
	}
	public String getMeasure_Time() {
		return Measure_Time;
	}
	public void setMeasure_Time(String measure_Time) {
		Measure_Time = measure_Time;
	}
	public String getHBP() {
		return HBP;
	}
	public void setHBP(String hBP) {
		HBP = hBP;
	}
	public String getLBP() {
		return LBP;
	}
	public void setLBP(String lBP) {
		LBP = lBP;
	}
	public String getBeat() {
		return Beat;
	}
	public void setBeat(String beat) {
		Beat = beat;
	}
}
