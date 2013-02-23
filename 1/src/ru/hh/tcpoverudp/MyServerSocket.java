package ru.hh.tcpoverudp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class MyServerSocket extends MyAbstractSocket {

	public MyServerSocket(int localPort) throws IOException {
		super(localPort);
	}
	
	public void accept() throws IOException, ClassNotFoundException {

//		while (true) {
			try {
				MyTcpPacket tcpPacket0 = tryReceiveMyTcpPacket();
				if ((tcpPacket0.getAcknowledgementNumber() == 0) &&
					(tcpPacket0.getSequenceNumber() == 0) &&
					(!tcpPacket0.isAck()) &&
					(tcpPacket0.isSyn())) {
//					break;
				}
			} catch (SocketTimeoutException e) {
//			}
		}

		MyTcpPacket tcpPacket1 = new MyTcpPacket(localPort, partnerPort, 0, 1, false, true, false, "");
		sendMyTcpPacket(tcpPacket1);
	
		MyTcpPacket tcpPacket2 = null;
//		for (int i = 0; i < 3; ++i) {
			try {
				tcpPacket2 = tryReceiveMyTcpPacket();
				if ((tcpPacket2.getAcknowledgementNumber() == 1) &&
					(tcpPacket2.getSequenceNumber() == 1) &&
					(tcpPacket2.isAck()) &&
					(tcpPacket2.isSyn())) {
//					break;
				}
			} catch (SocketTimeoutException e) {
			}
//		}
		if (tcpPacket2 == null) {
			throw new IOException("Client's connection failed");
		}
		sequenceNumber = 1;
		acknowledgementNumber = 2;
		connectionEstablshed = true;
		synchronized (monitor) {
			monitor.notify();
		}
	}

}
