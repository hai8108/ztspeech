package com.ztspeech.unisay.trans.model;

public class ZtspeechSmsInfoModel {
	public String smsName;
	public String smsNumber;
	public long date;
	public boolean isChekced = false;

	public ZtspeechSmsInfoModel(String smsName, String smsNumber, long date) {
		this.smsName = smsName;
		this.smsNumber = smsNumber;
		this.date = date;
	}

	public ZtspeechSmsInfoModel() {
	}

	@Override
	public String toString() {
		return "ContactsInfo [smsName=" + smsName + ", smsNumber=" + smsNumber + ", date=" + date + "]";
	}
}
