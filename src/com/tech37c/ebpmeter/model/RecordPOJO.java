package com.tech37c.ebpmeter.model;

public class RecordPOJO {
	private String recordId;
	private String userId;
	private String checkingTime;
	private String highValue;
	private String lowValue;
	private String heartBeat;
	public String getRecordId() {
		return recordId;
	}
	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getCheckingTime() {
		return checkingTime;
	}
	public void setCheckingTime(String checkingTime) {
		this.checkingTime = checkingTime;
	}
	public String getHighValue() {
		return highValue;
	}
	public void setHighValue(String highValue) {
		this.highValue = highValue;
	}
	public String getLowValue() {
		return lowValue;
	}
	public void setLowValue(String lowValue) {
		this.lowValue = lowValue;
	}
	public String getHeartBeat() {
		return heartBeat;
	}
	public void setHeartBeat(String heartBeat) {
		this.heartBeat = heartBeat;
	}
}
