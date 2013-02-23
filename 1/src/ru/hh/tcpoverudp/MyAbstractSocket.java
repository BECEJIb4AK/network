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
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

abstract public class MyAbstractSocket {
	private final static int BUFFER_SIZE = 4096;
	private final static int TIMEOUT = 200;

	private final byte[] buffer = new byte[BUFFER_SIZE];

	private DatagramSocket socket;
	protected int sequenceNumber;
	protected int acknowledgementNumber;

	private List<String> dataForReceiving = new LinkedList<String>();
	private List<MyTcpPacket> packetsForSending = new LinkedList<MyTcpPacket>();

	protected InetAddress partnerAddress;
	protected int partnerPort;
	protected int localPort;
	
	protected final Object monitor = new Object();
	protected boolean connectionEstablshed = false;

	private Thread netThread;
	
	private Random random = new Random();
	
	public void send(String data) {
		synchronized (packetsForSending) {
			packetsForSending.add(new MyTcpPacket(localPort, partnerPort, 0, 0, false, false, false, data));
		}
	}

	String receive() {
		synchronized (dataForReceiving) {
			if (dataForReceiving.size() == 0) {
				try {
					dataForReceiving.wait();
				} catch (InterruptedException e) {
				}
			}
			String data = dataForReceiving.get(0);
			dataForReceiving.remove(0);
			return data;
		}
	}

	protected void sendMyTcpPacket(MyTcpPacket tcpPacket) throws IOException {
		if (connectionEstablshed) {
			int rand = random.nextInt(); 
			if (rand % 3 == 1) {
				return;
			}
			if (rand % 3 == 2) {
				tcpPacket.corrupt();
			}
			tcpPacket.setAcknowledgementNumber(acknowledgementNumber);
			tcpPacket.setSequenceNumber(sequenceNumber);
			tcpPacket.countCheckSum();
		}
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
		objectOutputStream.writeObject(tcpPacket);
		byte[] packetBytes = byteArrayOutputStream.toByteArray();
		DatagramPacket udpPacket = new DatagramPacket(packetBytes,
				packetBytes.length, partnerAddress, partnerPort);
		socket.send(udpPacket);
	}

	protected MyTcpPacket tryReceiveMyTcpPacket() throws IOException, ClassNotFoundException {
		DatagramPacket udpPacket = new DatagramPacket(buffer, BUFFER_SIZE);
		socket.receive(udpPacket);
		if (partnerPort == 0) {
			partnerPort = udpPacket.getPort();
		}
		if (partnerAddress == null) {
			partnerAddress = udpPacket.getAddress();
		}
		ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(buffer));
		MyTcpPacket tcpPacket = (MyTcpPacket) objectInputStream.readObject();
		return tcpPacket;
	}

	void close() throws IOException {
		try {
			Thread.sleep(700);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		connectionEstablshed = false;
		try {
			Thread.sleep(700);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		sendMyTcpPacket(new MyTcpPacket(localPort, partnerPort, 0, 0, false, false, true, ""));
		socket.close();
		synchronized (dataForReceiving) {
			dataForReceiving.clear();
		}
		synchronized (packetsForSending) {
			packetsForSending.clear();
		}
	}

	public MyAbstractSocket(int localPort) throws SocketException {
		this(localPort, null, 0);
	}

	public MyAbstractSocket(int localPort, InetAddress partnerAddress, int partnerPort) throws SocketException {
		this.localPort = localPort;
		this.partnerAddress = partnerAddress;
		this.partnerPort = partnerPort;
		socket = new DatagramSocket(localPort);
		socket.setSoTimeout(TIMEOUT);
			
			netThread = new Thread(new Runnable() {
				public void run() {
					synchronized (monitor) {
						try {
							monitor.wait();
						} catch (InterruptedException e1) {
						}
					}
					while (connectionEstablshed) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {
						}
						MyTcpPacket tcpPacket = null;
						try {
							tcpPacket = tryReceiveMyTcpPacket();
						} catch (SocketTimeoutException e) {
						} catch (IOException e) {
							e.printStackTrace();
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
						if (tcpPacket != null) {
							int checkSum = tcpPacket.getCheckSum();
							tcpPacket.countCheckSum();
							if (checkSum == tcpPacket.getCheckSum()) {
								if (tcpPacket.isFin()) {
									try {
										close();
									} catch (IOException e) {
										e.printStackTrace();
									}
									break;
								}
								if (tcpPacket.getData().length() > 0) {//пришедшие данные
									if (acknowledgementNumber == tcpPacket.getSequenceNumber()) {
										acknowledgementNumber += tcpPacket.getData().length();
										synchronized (dataForReceiving) {
											dataForReceiving.add(tcpPacket.getData());
											dataForReceiving.notify();
										}
									}
								}
								synchronized (packetsForSending) {//пришедшее подтверждение
									if (packetsForSending.size() > 0) {
										MyTcpPacket tcpPacket_ = packetsForSending.get(0);
										if (sequenceNumber + tcpPacket_.getData().length() == tcpPacket.getAcknowledgementNumber()) {
											sequenceNumber += tcpPacket_.getData().length();
											packetsForSending.remove(0);
										}										
									}
								}
							} else {
							}
							
						} else {
						}
						
						synchronized (packetsForSending) {//отправка
							MyTcpPacket tcpPacket_;
							if (packetsForSending.size() > 0) {
								tcpPacket_ = packetsForSending.get(0);
							} else {
								tcpPacket_ = new MyTcpPacket(getLocalPort(), getPartnerPort(), sequenceNumber, acknowledgementNumber, false, false, false, "");
							}
							try {
								sendMyTcpPacket(tcpPacket_);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					
					}
				}
			});
			netThread.start();
		}
	

	public void processNet() {

	}
	
	int getPartnerPort() {
		return partnerPort;
	}

	int getLocalPort() {
		return localPort;
	}

}
