package ru.hh.tcpoverudp;

import java.io.Serializable;

public class MyTcpPacket implements Serializable {

	private int portFrom;
	private int portTo;
	private int sequenceNumber;
	private int acknowledgementNumber;
	private boolean syn;
	private boolean ack;
	private boolean fin;
	private int checkSum;
	private String data;
	
	
/*	public void print() {
		System.out.println("[" + portFrom + " " + portTo + " " + sequenceNumber + " " + acknowledgementNumber + " " + syn + " " + ack + " " + fin + " " + checkSum + " " + data.length() + "]");
	}
*/	
	public void corrupt() {
		checkSum++;
	}
	
	public int getPortFrom() {
		return portFrom;
	}

	public void setPortFrom(int portFrom) {
		this.portFrom = portFrom;
	}

	public int getPortTo() {
		return portTo;
	}

	public void setPortTo(int portTo) {
		this.portTo = portTo;
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public int getAcknowledgementNumber() {
		return acknowledgementNumber;
	}

	public void setAcknowledgementNumber(int acknowledgementNumber) {
		this.acknowledgementNumber = acknowledgementNumber;
	}

	public boolean isSyn() {
		return syn;
	}

	public void setSyn(boolean syn) {
		this.syn = syn;
	}

	public boolean isAck() {
		return ack;
	}

	public void setAck(boolean ack) {
		this.ack = ack;
	}

	public boolean isFin() {
		return fin;
	}

	public void setFin(boolean fin) {
		this.fin = fin;
	}

	public int getCheckSum() {
		return checkSum;
	}

	public void countCheckSum() {
		checkSum = portFrom ^ portTo ^ sequenceNumber ^ acknowledgementNumber ^ (syn ? 1 : 0) ^ (ack ? 1 : 0) ^ (fin ? 1 : 0) ^ data.hashCode();
	}

	@Override
	public String toString() {
		return "MyTcpPacket [portFrom=" + portFrom + ", portTo=" + portTo
				+ ", sequenceNumber=" + sequenceNumber
				+ ", acknowledgementNumber=" + acknowledgementNumber + ", syn="
				+ syn + ", ack=" + ack + ", fin=" + fin + ", checkSum="
				+ checkSum + ", data=" + data + "]";
	}

	public MyTcpPacket(int portFrom, int portTo, int sequenceNumber,
			int acknowledgementNumber, boolean syn, boolean ack, boolean fin,
			String data) {
		super();
		this.portFrom = portFrom;
		this.portTo = portTo;
		this.sequenceNumber = sequenceNumber;
		this.acknowledgementNumber = acknowledgementNumber;
		this.syn = syn;
		this.ack = ack;
		this.fin = fin;
		this.data = data;
		countCheckSum();
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
