package ru.hh.tcpoverudp;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Test {

	private final static String[] MESSAGES = {"1", "22", "333", "4444", "55555", "666666", "7777777", "88888888", "999999999", "0000000000"};
	private final static int SERVER_PORT = 6666;
	private final static int CYCLES_COUNT = 10;
	
	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException {
		new Thread(new Runnable() {
			public void run() {
				MyServerSocket serverSocket;
				try {
					serverSocket = new MyServerSocket(SERVER_PORT);
					serverSocket.accept();
					System.out.println("New thread, server accepted connection");
					
					for (int i = 0; i < CYCLES_COUNT; ++i) {
						for (int j = 0; j < MESSAGES.length; ++j) {
							String message = serverSocket.receive();
							if (!MESSAGES[j].equals(message)) {
								System.out.println("incorrect message, should be " + MESSAGES[i]);
							} else {
								System.out.println(message);
							}
						}
					}
					serverSocket.close();
					System.out.println("== Test finished ==");
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}).start();
		MyClientSocket socket = new MyClientSocket("localhost", SERVER_PORT);
		System.out.println("Main thread, client connected to server");
		
		for (int i = 0; i < CYCLES_COUNT; ++i) {
			for (int j = 0; j < MESSAGES.length; ++j) {
				socket.send(MESSAGES[j]);
			}
		}
	}

}
