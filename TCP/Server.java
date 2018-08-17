/*
TCP server

Normally, we wrap the input/output streams with text-based, datatypebased
or object-based wrappers:

ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
PrintWriter out = new PrintWriter(socket.getOutputStream());

DataInputStream in = new DataInputStream(socket.getInputStream());
DataOutputStream out = new DataOutputStream(socket.getOutputStream());

You may look back at the notes on file I/O to see how to write to the streams. However, one
more point ... when data is sent through the output stream, the flush() method should be sent
to the output stream so that the data is not buffered, but actually sent right away.

You must be careful when using ObjectInputStreams and ObjectOutputStreams.
When you create an ObjectInputStream, it blocks while it tries to read a header from the
underlying SocketInputStream. When you create the corresponding ObjectOutputStream at
the far end, it writes the header that the ObjectInputStream is waiting for, and both are able to
continue. If you try to create both ObjectInputStreams first, each end of the connection is
waiting for the other to complete before proceeding which results in a deadlock situation (i.e.,
the programs seems to hang/halt). This behavior is described in the API documentation for
the ObjectInputStream and ObjectOutputStream constructors. 
*/

import java.net.*; // all socket stuff is in here
import java.io.*;

public class Server {
	public static int SERVER_PORT = 5000; // arbitrary, but above 1023
	private int counter = 0;
	 
	// Helper method to get the ServerSocket started
	private ServerSocket goOnline() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(SERVER_PORT);
			System.out.println("SERVER listening on Port: " + SERVER_PORT);
		} 
		catch (IOException e) {
			System.out.println("SERVER: Error creating network connection");
		}
		return serverSocket;
 	 }

	// Handle all requests
	private void handleRequests(ServerSocket serverSocket) {
		while(true) {
			 Socket socket = null;
			 BufferedReader in = null;
			 PrintWriter out = null;
			 try {
				// Wait for an incoming client request
				socket = serverSocket.accept();
				// At this point, a client connection has been made
				in = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream());
			 } 
			 catch(IOException e) {
				System.out.println("SERVER: Error connecting to client");
				System.exit(-1);
			 }
			 
			 // Read in the client's request
			 try {
				String request = in.readLine();
				System.out.println("SERVER: Client Message Received: " + request);
			 if (request.equals("What Time is It ?")) {
				out.println(new java.util.Date());
				counter++;
			 }
			 else if (request.equals("How many requests have you handled ?"))
			 	out.println(counter++);
			 else
				System.out.println("SERVER: Unknown request: " + request);
				out.flush(); // Now make sure that the response is sent
				socket.close(); // We are done with the client's request
			 }
			 catch(IOException e) {
			 	System.out.println("SERVER: Error communicating with client");
			 }
		}
	}
	public static void main (String[] args) {
		Server s = new Server();
		ServerSocket ss = s.goOnline();
		if (ss != null)
		s.handleRequests(ss);
	}
}
