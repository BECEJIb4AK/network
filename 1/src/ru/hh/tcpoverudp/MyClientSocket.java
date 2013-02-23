package ru.hh.tcpoverudp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class MyClientSocket extends MyAbstractSocket {
	private static final int LOCAL_PORT = 12347; 
	
	public MyClientSocket(String serverName, int serverPort) throws UnknownHostException, IOException, ClassNotFoundException {
		super(LOCAL_PORT, InetAddress.getByName(serverName), serverPort);
		
		MyTcpPacket tcpPacket0 = new MyTcpPacket(LOCAL_PORT, serverPort, 0, 0, true, false, false, "");
		MyTcpPacket tcpPacket1 = null;
		
//		for (int i = 0; i < 3; ++i) {
			sendMyTcpPacket(tcpPacket0);
			try {
				tcpPacket1 = tryReceiveMyTcpPacket();
				if ((tcpPacket1.getAcknowledgementNumber() == 1) &&
					(tcpPacket1.getSequenceNumber() == 0) &&
					(tcpPacket1.isAck()) &&
					(!tcpPacket1.isSyn())) {
//					break;
				} else {
					System.out.println(tcpPacket1.getAcknowledgementNumber() + " " + tcpPacket1.getSequenceNumber() + " " + tcpPacket1.isAck() + " " + tcpPacket1.isSyn());
					tcpPacket1 = null;
				}
			} catch (SocketTimeoutException e) {
			}
//		}
		
		if (tcpPacket1 != null) {
			MyTcpPacket tcpPacket2 = new MyTcpPacket(LOCAL_PORT, serverPort, 1, 1, true, true, false, "");
			sendMyTcpPacket(tcpPacket2);
		} else {
			throw new IOException("server doesn't accept connection");
		}
		sequenceNumber = 2;
		acknowledgementNumber = 1;
		connectionEstablshed = true;
		synchronized (monitor) {
			monitor.notify();
		}
	}
		
}
