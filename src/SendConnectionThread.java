import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;


public class SendConnectionThread extends Thread
{
	int nodeID, NUMNODES;
	IOHandler IOH;
	
	public SendConnectionThread(int nodeID, int NUMNODES, IOHandler IOH)
	{
		super();
		start();
		this.nodeID = nodeID;
		this.NUMNODES = NUMNODES;
		this.IOH = IOH;
	}
	
	public void run()
	{
		System.out.println("Inside run() of SendConnection*********");
		Socket socket;
		for(int i=1;i<=NUMNODES;i++)
		{
			// Send connection to all nodes with nodeID > current node
			if (nodeID < i)
			{
				String host = IOH.map.get(Integer.toString(i)).get(0);
				int port = Integer.parseInt(IOH.map.get(Integer.toString(i)).get(1));
				try
				{
					System.out.println("Connecting "+host+":"+port);
					socket = new Socket(host,port);
					System.out.println("Connection established");
					
					System.out.println("Socket at "+nodeID+" for sending to "+i + " "+ socket);
					System.out.println("-------------------------");
					
					Proj2.socketMap.put(Integer.toString(i),socket);
					Proj2.readers.put(socket,new BufferedReader(new InputStreamReader(socket.getInputStream())));
					Proj2.writers.put(socket,new PrintWriter(socket.getOutputStream()));
				}
				catch (IOException e)
				{
					
					e.printStackTrace();
				}
				
			}
		}
	}
}
