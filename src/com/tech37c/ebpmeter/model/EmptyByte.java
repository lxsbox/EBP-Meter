package com.tech37c.ebpmeter.model;

public class EmptyByte {
	private byte[] sbyte;
	
	public EmptyByte(int len) {
		byte[] b = new byte[len];
		for(int i=0; i<len; i++) {//清空
			b[i] = 0;
		}
		sbyte = b;
	}
	
	public byte[] getSbyte() {
		return sbyte;
	}
	public void setSbyte(byte[] sbyte) {
		this.sbyte = sbyte;
	}
}
