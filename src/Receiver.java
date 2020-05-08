import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;


public class Receiver extends Thread
{
	int nodeID, NUMNODES;
	IOHandler IOH;
	int ID;	
	Receiver(int nodeID, int NUMNODES, IOHandler IOH)
	{
		super();
		start();
		this.nodeID = nodeID;
		this.NUMNODES = NUMNODES;
		this.IOH = IOH;
	}
	
	public void run()
	{
		try
		{
			System.out.println("Inside run() of Receiver");
			// Start Node at the specified port
			int port = Integer.parseInt(IOH.map.get(Integer.toString(nodeID)).get(1));
			ServerSocket server = new ServerSocket(port);
			System.out.println("Node "+nodeID+" listening at "+port);

			// First connection
			int i = 1;
			while (NUMNODES>1)
			{
				//Listens for a connection to be made to this socket and accepts it
				//The method blocks until a connection is made
				Socket socket = server.accept();
				System.out.println("Socket at "+nodeID+" for listening "+i + " "+ socket);
				System.out.println("-------------------------");
				
				Proj2.socketMap.put(Integer.toString(i),socket);
				Proj2.readers.put(socket,new BufferedReader(new InputStreamReader(socket.getInputStream())));
				Proj2.writers.put(socket,new PrintWriter(socket.getOutputStream()));

	            // incrementing i so that all incoming connections can be put in array in order.
	            i++;
	            
	            // Total no of incoming connections left
	            NUMNODES--;
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
