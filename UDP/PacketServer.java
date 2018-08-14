//UDP server
/*
Each message is sent as a packet. Each packet contains:
- the data of the message (i.e., the message itself)
- the length of the message (i.e., the number of bytes)
- the address of the sender (as an InetAddress)
- the port of the sender

The code for packaging and sending an outgoing packet involves creating a DatagramSocket
and then constructing a DatagramPacket. The packet requires an array of bytes, as well as
the address and port in which to send to. A byte array can be obtained from most objects by
sending a getBytes() message to the object. Finally, a send() message is used to send the
packet: 
*/

import java.net.*;
import java.io.*;

public class PacketServer {
	public static int SERVER_PORT = 5000;
	private static int INPUT_BUFFER_LIMIT = 500;
	private int counter = 0;

	// Helper method to get the DatagramSocket started
	private DatagramSocket goOnline() {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(SERVER_PORT);
			System.out.println("SERVER Listening On Port: " + SERVER_PORT);
		}
		catch (SocketException e) {
			System.out.println("SERVER: no network connection");
			System.exit(-1);
		}
	return socket;
	}

	// Handle all requests
	private void handleRequests(DatagramSocket socket) {
		while(true) {
			try {
				// Wait for an incoming client request
				byte[] recieveBuffer = new byte[INPUT_BUFFER_LIMIT];
				DatagramPacket receivePacket;
				receivePacket = new DatagramPacket(recieveBuffer,
				recieveBuffer.length);
				socket.receive(receivePacket);

				// Extract the packet data that contains the request
				InetAddress address = receivePacket.getAddress();
				int clientPort = receivePacket.getPort();
				String request = new String(receivePacket.getData(), 0,
				receivePacket.getLength());
				System.out.println("SERVER: Packet received: \"" + request +
				"\" from " + address + ":" + clientPort); 

				// Decide what should be sent back to the client
				byte[] sendBuffer;
				if (request.equals("What Time is It ?")) {
				System.out.println("SERVER: sending packet with time info");
				sendResponse(socket, address, clientPort,
				new java.util.Date().toString().getBytes());
				counter++;
			}
			else if (request.equals("How many requests have you handled ?")) {
				System.out.println("SERVER: sending packet with num requests");
				sendResponse(socket, address, clientPort,
				("" + ++counter).getBytes());
			}
			else
				System.out.println("SERVER: Unknown request: " + request);
			}
			catch(IOException e) {
				System.out.println("SERVER: Error receiving client requests");
			}
		}
	}

	// This helper method sends a given response back to the client
	private void sendResponse(DatagramSocket socket, InetAddress address, int clientPort, byte[] response) {
		try {
			// Now create a packet to contain the response and send it
			DatagramPacket sendPacket = new DatagramPacket(response,
			response.length, address, clientPort);
			socket.send(sendPacket);
		}
		catch (IOException e) {
			System.out.println("SERVER: Error sending response to client");
		}
	}

	public static void main (String args[]) {
		PacketServer s = new PacketServer();
		DatagramSocket ds = s.goOnline();
		if (ds != null)
		s.handleRequests(ds);
	}
} 
